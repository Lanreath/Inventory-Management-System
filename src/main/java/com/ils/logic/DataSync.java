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

    protected void syncProducts(){
        ResultSet products = ReadUtil.readProducts(oracleUsername, oraclePassword);
        Supplier<Stream<String>> savedProducts = () -> ProductDAO.getProducts().stream().map(Product::getProductName);
        try {
            while (products.next()) {
                String curr = products.getString("PRODUCTNAME").split("_")[0];
                String customer = products.getString("CUSTOMERNAME");
                if (savedProducts.get().noneMatch((s) -> s.equals(curr))) {
                    Optional<Customer> c = CustomerDAO.getCustomer(customer);
                    c.ifPresent((cust) -> ProductDAO.insertProduct(curr, cust));
                    c.orElseThrow(() -> new SQLException("Could not find Customer " + customer + " in database"));
                }
            }
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, LocalDateTime.now() + ": Could not sync Products from database: " + e.getMessage());
        }
    }

    protected void syncProductsByCustomer(Customer customer) {
        ResultSet products = ReadUtil.readProductsFromCustomer(oracleUsername, oraclePassword, customer.getId());
        Stream<String> savedProducts = ProductDAO.getProductsByCustomer(customer).map(Product::getProductName);
        try {
            while (products.next()) {
                String curr = products.getString("PRODUCTNAME").split("_")[0];
                if (savedProducts.noneMatch((s) -> s.equals(curr))) {
                    ProductDAO.insertProduct(curr, customer);
                }
            }
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, LocalDateTime.now() + ": Could not sync Products from database " + e.getMessage());
        }
    }
    
    // public void syncParts(Product product) {};

    protected void syncTransfers(LocalDate date) {
        ResultSet transfers = ReadUtil.readTransfersByDate(oracleUsername, oraclePassword, date);
        Supplier<Stream<String>> savedProducts = () -> ProductDAO.getProducts().stream().map(Product::getProductName);
        Supplier<Stream<Transfer>> savedTransfers = () -> TransferDAO.getTransfersByDate(date);
        try {
            while (transfers.next()) {
                String customer = transfers.getString("CUSTOMER");
                String product = transfers.getString("PRODUCT");
                // String vaultName = transfers.getString("VAULTNAME");
                int quantity = transfers.getInt("QUANTITY");

                // if (savedTransfers.noneMatch((t) -> t.getPart().getProduct().getCustomer().getCustomerName().equals(customer) &&
                //     t.getPart().getProduct().getProductName().equals(product) &&
                //     t.getQuantity() == quantity &&
                //     t.getTransferType() == Transfer.Action.WITHDRAW)) {
                //     Product p = ProductDAO.getProductByName(product).get();    
                //     TransferDAO.insertTransfer(p.getDefaultPart(), quantity, Transfer.Action.WITHDRAW);
                // }
                if (savedProducts.get().noneMatch((p) -> p.equals(product))) {
                    Optional<Customer> c = CustomerDAO.getCustomer(customer);
                    c.ifPresent((cust) -> ProductDAO.insertProduct(product, cust));
                    c.orElseThrow(() -> new IllegalStateException("Could not find Customer " + customer + " in database"));
                }
                if (savedTransfers.get().noneMatch((t) -> t.getProduct().getCustomer().getCustomerName().equals(customer) &&
                    t.getProduct().getProductName().equals(product) &&
                    t.getTransferQuantity() == quantity &&
                    // t.getTransferDateTime().toLocalDate().isEqual(date.toLocalDate()) &&
                    t.getTransferType() == Transfer.Action.WITHDRAW)) {
                    Product p = ProductDAO.getProductByName(product).get();
                    TransferDAO.insertTransfer(p, quantity, Transfer.Action.WITHDRAW);
                }
            }
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, LocalDateTime.now() + ": Could not sync Transfers from database " + e.getMessage());
        }
    }
}
