package com.ils.logic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ils.MainApp;
import com.ils.logic.DAO.CustomerDAO;
import com.ils.logic.DAO.PartDAO;
import com.ils.logic.DAO.ProductDAO;
import com.ils.logic.DAO.TransferDAO;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;
import com.ils.oracle.Oracle;
import com.ils.oracle.ReadUtil;
import com.ils.sqlite.Database;

public abstract class DataSync {
    static {
        init();
    }

    private static void init() {
        if (Database.isOK() && Oracle.isOK()) {
            return;
        }
        throw new IllegalArgumentException("Could not connect to database, please check your properties file at database/oracle.properties");
    }

    private static void syncCustomers() {
        ResultSet customers = ReadUtil.readCustomers();
        Supplier<Stream<String>> savedCustomers = () -> CustomerDAO.getCustomers().stream().map(Customer::getCustomerName);
        try {
            while (customers.next()) {
                // Somewhere here
                String curr = customers.getString("CUSTOMERNAME");
                if (savedCustomers.get().noneMatch((s) -> s.equals(curr))) {
                    CustomerDAO.insertCustomer(curr);
                }
            }
            Logger.getLogger(MainApp.class.getName()).log(Level.INFO, "All customers inserted");
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, LocalDateTime.now() + ": Could not sync Customers from database " + e.getMessage());
        }
    }
    
    private static void syncDailyTransfers(LocalDate date) {
        ResultSet transfers = ReadUtil.readDailyTransfersByDate(date);
        Supplier<Stream<String>> savedProducts = () -> ProductDAO.getProducts().stream().map(Product::getDBName);
        Supplier<Stream<Part>> savedParts = () -> PartDAO.getParts().stream();
        Supplier<Stream<Transfer>> savedTransfers = () -> TransferDAO.getTransfersByDate(date);
        try {
            // Hack to insert an aliased customer
            if(!CustomerDAO.getCustomer("UOB VN").isPresent()){
                CustomerDAO.insertCustomer("UOB VN");
            }
            while (transfers.next()) {
                String customer = transfers.getString("CUSTOMER");
                String product = transfers.getString("VAULTNAME");
                int quantity = transfers.getInt("QTY");
                if (savedProducts.get().noneMatch((p) -> p.equals(product))) {
                    // Product does not exist in database
                    Optional<Customer> c = CustomerDAO.getCustomer(customer);
                    c.ifPresent((cust) -> ProductDAO.insertProduct(product, cust));
                    c.orElseThrow(() -> new IllegalStateException("Could not find Customer " + customer + " in database"));
                }
                // Product
                Optional<Product> p = ProductDAO.getProductByDBName(product);
                p.orElseThrow(() -> new IllegalStateException("Could not find Product " + product + " in database"));
                Product prod = p.get();
                if (savedParts.get().noneMatch((pt) -> pt.getProduct().equals(prod))) {
                    // Part does not exist in database
                    PartDAO.insertPart("Default", 0, prod);
                }
                // Part
                Part part = prod.getDefaultPart();
                if (part == null) {
                    // Product does not have a default part
                    Optional<Part> dflt = PartDAO.getPartByNameAndProduct("Default", prod);
                    dflt.orElseThrow(() -> new IllegalStateException("Could not find Part Default in database"));
                    // Update Product with default part
                    Product newProd = new Product(prod.getDBName(), prod.getCreationDateTime(), prod.getCustomer(), dflt.get(), prod.getId());
                    ProductDAO.updateProduct(newProd);
                    part = dflt.get();
                }
                if (savedTransfers.get().noneMatch((t) -> t.getPart().getProduct().getCustomer().getCustomerName().equals(customer) &&
                    t.getPart().getProduct().getDBName().equals(product) &&
                    t.getTransferDateTime().toLocalDate().isEqual(date) &&
                    t.getTransferType() == Transfer.Action.DAILY)) {
                    // Check if part has enough quantity
                    while (part.getPartQuantity() < quantity && part.getNextPart() != null) {
                        // Deduct and check again
                        TransferDAO.insertTransfer(part, part.getPartQuantity(), Transfer.Action.DAILY, date);
                        Part emptyPart = new Part(part.getPartName(), part.getCreationDateTime(), 0, part.getProduct(), part.getNextPart(), part.getPartNotes(), part.getId());
                        PartDAO.updatePart(emptyPart);
                        quantity -= part.getPartQuantity();
                        part = part.getNextPart();
                        Logic.getProductManagement().updateDefaultPart(part);
                    } 
                    TransferDAO.insertTransfer(part, quantity, Transfer.Action.DAILY, date);
                    Part newPart = new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity() - quantity, part.getProduct(), part.getNextPart(), part.getPartNotes(), part.getId());
                    PartDAO.updatePart(newPart);
                } else {
                    // Transfer already exists, but quantity might be different
                    List<Transfer> ts = TransferDAO.getTransfersByProductAndDate(prod, date).filter((t) -> t.getTransferType() == Transfer.Action.DAILY).collect(Collectors.toList());
                    int total = 0;
                    for (Transfer t : ts) {
                        total += t.getTransferQuantity();
                    }
                    quantity -= total;
                    Part copyPart = new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity(), part.getProduct(), part.getId());
                    Transfer transfer = null;
                    for (Transfer t : ts) {
                        if (t.getPart().equals(copyPart)) {
                            transfer = t;
                            break;
                        }
                    }
                    if (transfer == null) {
                        throw new IllegalStateException("Could not find matching transfer for part in database");
                    }
                    while (quantity >= 0 && part.getPartQuantity() < quantity && part.getNextPart() != null) {
                        // Deduct and check again
                        if (part.equals(transfer.getPart())){
                            TransferDAO.updateTransfer(new Transfer(transfer.getTransferDateTime(), transfer.getPart(), transfer.getPrevPartQuantity(), transfer.getTransferQuantity() + part.getPartQuantity(), transfer.getTransferType(), transfer.getId()));
                        } else {
                            TransferDAO.insertTransfer(part, part.getPartQuantity(), Transfer.Action.DAILY, date);
                        }
                        Part emptyPart = new Part(part.getPartName(), part.getCreationDateTime(), 0, part.getProduct(), part.getNextPart(), part.getPartNotes(), part.getId());
                        PartDAO.updatePart(emptyPart);
                        quantity -= part.getPartQuantity();
                        part = part.getNextPart();
                        Logic.getProductManagement().updateDefaultPart(part);
                    }
                    if (part.equals(transfer.getPart())){
                        TransferDAO.updateTransfer(new Transfer(transfer.getTransferDateTime(), transfer.getPart(), transfer.getPrevPartQuantity(), transfer.getTransferQuantity() + quantity, transfer.getTransferType(), transfer.getId()));
                    } else {
                        TransferDAO.insertTransfer(part, quantity, Transfer.Action.DAILY, date);
                    }
                    Part newPart = new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity() - quantity, part.getProduct(), part.getNextPart(), part.getPartNotes(), part.getId());
                    PartDAO.updatePart(newPart);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, LocalDateTime.now() + ": Could not sync Daily Transfers from database " + e.getMessage());
        }
    }
    private static void syncRenewalTransfers(LocalDate date) {
        ResultSet transfers = ReadUtil.readRenewalTransfersByDate(date);
        Supplier<Stream<String>> savedProducts = () -> ProductDAO.getProducts().stream().map(Product::getDBName);
        Supplier<Stream<Part>> savedParts = () -> PartDAO.getParts().stream();
        Supplier<Stream<Transfer>> savedTransfers = () -> TransferDAO.getTransfersByDate(date);
        try {
            // Hack to insert an aliased customer
            if(!CustomerDAO.getCustomer("UOB VN").isPresent()){
                CustomerDAO.insertCustomer("UOB VN");
            }
            while (transfers.next()) {
                String customer = transfers.getString("CUSTOMER");
                String product = transfers.getString("VAULTNAME");
                int quantity = transfers.getInt("QTY");
                if (savedProducts.get().noneMatch((p) -> p.equals(product))) {
                    // Product does not exist in database
                    Optional<Customer> c = CustomerDAO.getCustomer(customer);
                    c.ifPresent((cust) -> ProductDAO.insertProduct(product, cust));
                    c.orElseThrow(() -> new IllegalStateException("Could not find Customer " + customer + " in database"));
                }
                // Product
                Optional<Product> p = ProductDAO.getProductByDBName(product);
                p.orElseThrow(() -> new IllegalStateException("Could not find Product " + product + " in database"));
                Product prod = p.get();
                if (savedParts.get().noneMatch((pt) -> pt.getProduct().equals(prod))) {
                    // Part does not exist in database
                    PartDAO.insertPart("Default", 0, prod);
                }
                // Part
                Part part = prod.getDefaultPart();
                if (part == null) {
                    // Product does not have a default part
                    Optional<Part> dflt = PartDAO.getPartByNameAndProduct("Default", prod);
                    dflt.orElseThrow(() -> new IllegalStateException("Could not find Part Default in database"));
                    // Update Product with default part
                    Product newProd = new Product(prod.getDBName(), prod.getCreationDateTime(), prod.getCustomer(), dflt.get(), prod.getId());
                    ProductDAO.updateProduct(newProd);
                    part = dflt.get();
                }
                if (savedTransfers.get().noneMatch((t) -> t.getPart().getProduct().getCustomer().getCustomerName().equals(customer) &&
                    t.getPart().getProduct().getDBName().equals(product) &&
                    t.getTransferDateTime().toLocalDate().isEqual(date) &&
                    t.getTransferType() == Transfer.Action.RENEWAL)) {
                    // Check if part has enough quantity
                    while (part.getPartQuantity() < quantity && part.getNextPart() != null) {
                        // Deduct and check again
                        TransferDAO.insertTransfer(part, part.getPartQuantity(), Transfer.Action.RENEWAL, date);
                        Part emptyPart = new Part(part.getPartName(), part.getCreationDateTime(), 0, part.getProduct(), part.getNextPart(), part.getPartNotes(), part.getId());
                        PartDAO.updatePart(emptyPart);
                        quantity -= part.getPartQuantity();
                        part = part.getNextPart();
                        Logic.getProductManagement().updateDefaultPart(part);
                    } 
                    TransferDAO.insertTransfer(part, quantity, Transfer.Action.RENEWAL, date);
                    Part newPart = new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity() - quantity, part.getProduct(), part.getNextPart(), part.getPartNotes(), part.getId());
                    PartDAO.updatePart(newPart);
                } else {
                    // Transfer already exists, but quantity might be different
                    List<Transfer> ts = TransferDAO.getTransfersByProductAndDate(prod, date).filter((t) -> t.getTransferType() == Transfer.Action.RENEWAL).collect(Collectors.toList());
                    int total = 0;
                    for (Transfer t : ts) {
                        total += t.getTransferQuantity();
                    }
                    quantity -= total;
                    Part copyPart = new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity(), part.getProduct(), part.getId());
                    Transfer transfer = null;
                    for (Transfer t : ts) {
                        if (t.getPart().equals(copyPart)) {
                            transfer = t;
                            break;
                        }
                    }
                    if (transfer == null) {
                        throw new IllegalStateException("Could not find matching transfer for part in database");
                    }
                    while (quantity >= 0 && part.getPartQuantity() < quantity && part.getNextPart() != null) {
                        // Deduct and check again
                        if (part.equals(transfer.getPart())){
                            TransferDAO.updateTransfer(new Transfer(transfer.getTransferDateTime(), transfer.getPart(), transfer.getPrevPartQuantity(), transfer.getTransferQuantity() + part.getPartQuantity(), transfer.getTransferType(), transfer.getId()));
                        } else {
                            TransferDAO.insertTransfer(part, part.getPartQuantity(), Transfer.Action.RENEWAL, date);
                        }
                        Part emptyPart = new Part(part.getPartName(), part.getCreationDateTime(), 0, part.getProduct(), part.getNextPart(), part.getPartNotes(), part.getId());
                        PartDAO.updatePart(emptyPart);
                        quantity -= part.getPartQuantity();
                        part = part.getNextPart();
                        Logic.getProductManagement().updateDefaultPart(part);
                    }
                    if (part.equals(transfer.getPart())){
                        TransferDAO.updateTransfer(new Transfer(transfer.getTransferDateTime(), transfer.getPart(), transfer.getPrevPartQuantity(), transfer.getTransferQuantity() + quantity, transfer.getTransferType(), transfer.getId()));
                    } else {
                        TransferDAO.insertTransfer(part, quantity, Transfer.Action.RENEWAL, date);
                    }
                    Part newPart = new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity() - quantity, part.getProduct(), part.getNextPart(), part.getPartNotes(), part.getId());
                    PartDAO.updatePart(newPart);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, LocalDateTime.now() + ": Could not sync Renewal Transfers from database " + e.getMessage());
        }
    }

    public static void syncData(LocalDate date) {
        syncCustomers();
        syncDailyTransfers(date);
        syncRenewalTransfers(date);
    }
}
