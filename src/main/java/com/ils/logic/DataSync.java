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

import com.ils.db.Database;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;
import com.ils.oracle.Oracle;
import com.ils.oracle.ReadUtil;

public class DataSync {
    private String oracleUsername;
    private String oraclePassword;

    protected DataSync(String oracleUsername, String oraclePassword) {
        if (Database.isOK() && Oracle.isOK(oracleUsername, oraclePassword)) {
            this.oracleUsername = oracleUsername;
            this.oraclePassword = oraclePassword;
            return;
        }
        throw new IllegalArgumentException("Could not connect to database, please check your credentials");
    }

    protected void syncCustomers() {
        ResultSet customers = ReadUtil.readCustomers(oracleUsername, oraclePassword);
        Supplier<Stream<String>> savedCustomers = () -> CustomerDAO.getCustomers().stream().map(Customer::getCustomerName);
        try {
            while (customers.next()) {
                // Somewhere here
                String curr = customers.getString("CUSTOMERNAME");
                if (savedCustomers.get().noneMatch((s) -> s.equals(curr))) {
                    CustomerDAO.insertCustomer(curr);
                }
            }
            Logger.getAnonymousLogger().log(Level.FINE, "All customers inserted");
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, LocalDateTime.now() + ": Could not sync Customers from database " + e.getMessage());
        }
    }
    
    protected void syncTransfers(LocalDate date) {
        ResultSet transfers = ReadUtil.readTransfersByDate(oracleUsername, oraclePassword, date);
        Supplier<Stream<String>> savedProducts = () -> ProductDAO.getProducts().stream().map(Product::getProductName);
        Supplier<Stream<Part>> savedParts = () -> PartDAO.getParts().stream();
        Supplier<Stream<Transfer>> savedTransfers = () -> TransferDAO.getTransfersByDate(date);
        try {
            while (transfers.next()) {
                String customer = transfers.getString("CUSTOMER");
                String product = transfers.getString("PRODUCT");
                int quantity = transfers.getInt("QUANTITY");

                if (savedProducts.get().noneMatch((p) -> p.equals(product))) {
                    // Product does not exist in database
                    Optional<Customer> c = CustomerDAO.getCustomer(customer);
                    c.ifPresent((cust) -> ProductDAO.insertProduct(product, cust));
                    c.orElseThrow(() -> new IllegalStateException("Could not find Customer " + customer + " in database"));
                }
                // Product
                Optional<Product> p = ProductDAO.getProductByName(product);
                p.orElseThrow(() -> new IllegalStateException("Could not find Product " + product + " in database"));
                Product prod = p.get();
                if (savedParts.get().noneMatch((pt) -> pt.getProduct().getId() == prod.getId())) {
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
                    Product newProd = new Product(prod.getProductName(), prod.getCreationDateTime(), prod.getCustomer(), dflt.get(), prod.getId());
                    ProductDAO.updateProduct(newProd);
                    part = dflt.get();
                }
                if (savedTransfers.get().noneMatch((t) -> t.getPart().getProduct().getCustomer().getCustomerName().equals(customer) &&
                    t.getPart().getProduct().getProductName().equals(product) &&
                    t.getTransferQuantity() == quantity &&
                    t.getTransferDateTime().toLocalDate().isEqual(date) &&
                    t.getTransferType() == Transfer.Action.WITHDRAW)) {
                    TransferDAO.insertTransfer(part, quantity, Transfer.Action.WITHDRAW);

                    // Update Part quantity
                    Part newPart = new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity() - quantity, part.getProduct(), part.getId());
                    PartDAO.updatePart(newPart);
                }
            }
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, LocalDateTime.now() + ": Could not sync Transfers from database " + e.getMessage());
        }
    }
}
