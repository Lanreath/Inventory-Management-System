package com.ils.logic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.ils.MainApp;
import com.ils.db.Database;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;
import com.ils.oracle.Oracle;
import com.ils.oracle.ReadUtil;

public class DataSync {
    protected DataSync() {
        if (Database.isOK() && Oracle.isOK()) {
            return;
        }
        throw new IllegalArgumentException("Could not connect to database, please check your properties file at database/oracle.properties");
    }

    protected void syncCustomers() {
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
    
    protected void syncDailyTransfers(LocalDate date) {
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
                    TransferDAO.insertTransfer(part, quantity, Transfer.Action.DAILY);

                    // Update Part quantity
                    Part newPart = new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity() - quantity, part.getProduct(), part.getId());
                    PartDAO.updatePart(newPart);
                } else {
                    // Transfer already exists, but quantity might be different
                    Optional<Transfer> t = TransferDAO.getTransferByPartAndDate(part, date);
                    t.orElseThrow(() -> new IllegalStateException("Could not find matching transfer in database"));    
                    Transfer transfer = t.get();
                    if (transfer.getTransferQuantity() != quantity) {
                        // Update Transfer quantity
                        Transfer newTransfer = new Transfer(transfer.getTransferDateTime(), transfer.getPart(), transfer.getPrevPartQuantity(), transfer.getTransferQuantity(), transfer.getTransferType(), transfer.getId());
                        TransferDAO.updateTransfer(newTransfer);

                        // Update Part quantity, subtracting the difference
                        Part newPart = new Part(part.getPartName(), part.getCreationDateTime(), quantity - transfer.getTransferQuantity(), part.getProduct(), part.getId());
                        PartDAO.updatePart(newPart);
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, LocalDateTime.now() + ": Could not sync Daily Transfers from database " + e.getMessage());
        }
    }
    protected void syncRenewalTransfers(LocalDate date) {
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
                    TransferDAO.insertTransfer(part, quantity, Transfer.Action.RENEWAL);

                    // Update Part quantity
                    Part newPart = new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity() - quantity, part.getProduct(), part.getId());
                    PartDAO.updatePart(newPart);
                } else {
                    // Transfer already exists, but quantity might be different
                    Optional<Transfer> t = TransferDAO.getTransferByPartAndDate(part, date);
                    t.orElseThrow(() -> new IllegalStateException("Could not find matching transfer in database"));    
                    Transfer transfer = t.get();
                    if (transfer.getTransferQuantity() != quantity) {
                        // Update Transfer quantity
                        Transfer newTransfer = new Transfer(transfer.getTransferDateTime(), transfer.getPart(), transfer.getPrevPartQuantity(), transfer.getTransferQuantity(), transfer.getTransferType(), transfer.getId());
                        TransferDAO.updateTransfer(newTransfer);

                        // Update Part quantity, subtracting the difference
                        Part newPart = new Part(part.getPartName(), part.getCreationDateTime(), quantity - transfer.getTransferQuantity(), part.getProduct(), part.getId());
                        PartDAO.updatePart(newPart);
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, LocalDateTime.now() + ": Could not sync Renewal Transfers from database " + e.getMessage());
        }
    }

}
