package com.ils.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ils.MainApp;
import com.ils.db.CRUDUtil;
import com.ils.db.Database;
import com.ils.models.Customer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class CustomerDAO {
    private static final String tableName = "CUSTOMER";
    private static final String nameColumn = "CUSTOMERNAME";
    private static final String creationDateTimeColumn = "CREATIONDATETIME";
    private static final String idColumn = "CUSTOMERID";

    private static final ObservableList<Customer> customers;

    static {
        customers = FXCollections.observableArrayList();
        updateCustomersFromDB();
    }

    protected static FilteredList<Customer> getCustomers() {
        return new FilteredList<Customer>(customers);
    }

    private static void updateCustomersFromDB() {
        String query = "SELECT * FROM " + tableName;
        try (Connection connection = Database.connect()) {
            PreparedStatement statement = connection.prepareStatement(query); 
            ResultSet rs = statement.executeQuery();
            customers.clear();
            while (rs.next()) {
                customers.add(new Customer(
                    rs.getString(nameColumn),
                    LocalDateTime.parse(rs.getString(creationDateTimeColumn)),
                    rs.getInt(idColumn)
                ));
            } 
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(
                Level.SEVERE,
                LocalDateTime.now() + ": Could not load Customers from database " + e.getMessage()
            );
            customers.clear();
        }
    }

    public static Optional<Customer> getCustomer(int id) {
        for (Customer customer : customers) {
            if (customer.getId() == id) {
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }

    public static Optional<Customer> getCustomer(String customerName) {
        for (Customer customer : customers) {
            if (customer.getCustomerName().equals(customerName)) {
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }

    public static void insertCustomer(String customerName) {
        LocalDateTime now = LocalDateTime.now();
        int id = (int) CRUDUtil.create(tableName, new String[]{nameColumn, creationDateTimeColumn}, new Object[]{customerName, now}, new int[]{Types.VARCHAR, Types.TIMESTAMP});
        customers.add(new Customer(customerName, now, id));
    }

    public static void updateCustomer(Customer newCustomer) {
        int rows = CRUDUtil.update(
            tableName,
            new String[]{nameColumn},
            new Object[]{newCustomer.getCustomerName()},
            new int[]{Types.VARCHAR},
            idColumn,
            Types.INTEGER,
            newCustomer.getId()
        );
       
        if (rows == 0) {
            throw new IllegalStateException("Customer to be updated with id" + newCustomer.getId() + " does not exist in database.");
        }

        Optional<Customer> optionalCustomer = getCustomer(newCustomer.getId());
        optionalCustomer.ifPresent((oldCustomer) -> {
            customers.remove(oldCustomer);
            customers.add(newCustomer);
        });
        optionalCustomer.orElseThrow(() -> {
            throw new IllegalStateException("Customer to be updated with id" + newCustomer.getId() + " does not exist in database.");
        });
    }

    public static void deleteCustomer(int id) {
        CRUDUtil.delete(tableName, id);

        Optional<Customer> customer = getCustomer(id);
        customer.ifPresent(customers::remove);
    }
}
