package com.ils.logic;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.ils.db.CRUDUtil;
import com.ils.db.Database;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class ProductDAO {
    private static final String tableName = "PRODUCT";
    private static final String nameColumn = "PRODUCTNAME";
    private static final String creationDateTimeColumn = "CREATIONDATETIME";
    private static final String customerIdColumn = "CUSTOMERID";
    private static final String defaultPartIdColumn = "DEFAULTPARTID";
    private static final String idColumn = "PRODUCTID";

    private static final ObservableList<Product> products;

    static {
        products = FXCollections.observableArrayList();
        updateProductsFromDB();
    }

    protected static FilteredList<Product> getProducts() {
        return new FilteredList<>(products);
    }

    private static void updateProductsFromDB() {
        String query = "SELECT * FROM " + tableName;
        try (Connection connection = Database.connect()) {
            PreparedStatement statement = connection.prepareStatement(query); 
            ResultSet rs = statement.executeQuery();
            products.clear();
            while (rs.next()) {
                Integer customerId = rs.getInt(customerIdColumn);
                Integer defaultPartId = rs.getInt(defaultPartIdColumn);
                Optional<Customer> customer = CustomerDAO.getCustomer(customerId);
                // Part has not init
                // Optional<Part> defaultPart = PartDAO.getPart(defaultPartId);
                customer.orElseThrow(() -> new IllegalStateException("Could not find Customer with id " + customerId));
                products.add(new Product(
                    rs.getString(nameColumn),
                    LocalDateTime.parse(rs.getString(creationDateTimeColumn)),
                    customer.get(),
                    new Part(null, null, 0, null, defaultPartId),
                    rs.getInt(idColumn)
                ));
            } 
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                Level.SEVERE,
                LocalDateTime.now() + ": Could not load Products from database " + e.getMessage()
            );
            products.clear();
        }
    }

    protected static void updateDefaultParts() {
        // ConcurrentModificationException
        // Store products to be updated in a list and update them after the loop
        ObservableList<Product> copyProducts = FXCollections.observableArrayList(ProductDAO.products);
        copyProducts.forEach((product) -> {
            Optional<Part> defaultPart = PartDAO.getPart(product.getDefaultPart().getId());
            defaultPart.ifPresent((part) -> {
                ProductDAO.updateProduct(new Product(
                    product.getProductName(),
                    product.getCreationDateTime(),
                    product.getCustomer(),
                    part,
                    product.getId()
                ));
            });
            defaultPart.orElseThrow(() -> new IllegalStateException("Could not find Part with id " + product.getDefaultPart().getId()));
        });
    }

    public static Optional<Product> getProduct(int id) {
        return products.stream().filter((product) -> product.getId() == id).findFirst();
    }

    public static Optional<Product> getProductByName(String name) {
        return products.stream().filter((product) -> product.getProductName().equals(name)).findFirst();
    }

    public static Stream<Product> getProductsByCustomer(Customer customer) {
        return products.stream().filter((product) -> product.getCustomer().equals(customer));
    }

    public static void insertProduct(String productName, Customer customer) {
        LocalDateTime now = LocalDateTime.now();
        int id = (int) CRUDUtil.create(tableName, new String[]{nameColumn, creationDateTimeColumn, customerIdColumn}, new Object[]{productName, now, customer.getId()}, new int[]{Types.VARCHAR, Types.TIMESTAMP, Types.INTEGER});
        products.add(new Product(productName, now, customer, id));
    }

    public static void updateProduct(Product newProduct) {
        int rows = CRUDUtil.update(
            tableName,
            new String[]{nameColumn, customerIdColumn, defaultPartIdColumn},
            new Object[]{newProduct.getProductName(), newProduct.getCustomer().getId(), newProduct.getDefaultPart().getId()},
            new int[]{Types.VARCHAR, Types.INTEGER, Types.INTEGER},
            idColumn,
            Types.INTEGER,
            newProduct.getId()
        );
       
        if (rows == 0) {
            throw new IllegalStateException("Product to be updated with id" + newProduct.getId() + " does not exist in database.");
        }

        Optional<Product> optionalProduct = getProduct(newProduct.getId());
        optionalProduct.ifPresent((oldProduct) -> {
            products.remove(oldProduct);
            products.add(newProduct);
        });
        optionalProduct.orElseThrow(() -> {
            throw new IllegalStateException("Product to be updated with id" + newProduct.getId() + " does not exist in database.");
        });
    }

    public static void deleteProduct(int id) {
        CRUDUtil.delete(tableName, id);

        Optional<Product> product = getProduct(id);
        product.ifPresent(products::remove);
    }
}
