package com.ils.logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.ils.MainApp;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.sqlite.CRUDUtil;
import com.ils.sqlite.Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class ProductDAO {
    // Database table name and column names
    private static final String tableName = "PRODUCT";
    private static final String dbNameColumn = "DBNAME";
    private static final String creationDateTimeColumn = "CREATIONDATETIME";
    private static final String customerIdColumn = "CUSTOMERID";
    private static final String defaultPartIdColumn = "DEFAULTPARTID";
    private static final String productNameColumn = "PRODUCTNAME";
    private static final String productNotesColumn = "PRODUCTNOTES";
    private static final String idColumn = "PRODUCTID";

    private static final ObservableList<Product> products;

    static {
        products = FXCollections.observableArrayList();
        updateProductsFromDB();
    }

    /**
     * Get a filter wrapper around the products list.
     * @return FilteredList<Product> using products list as source
     */
    public static FilteredList<Product> getProducts() {
        return new FilteredList<>(products);
    }

    /**
     * Update the products list from the database.
     */
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
                customer.orElseThrow(() -> new IllegalStateException("Could not find Customer with id " + customerId));
                products.add(new Product(
                    rs.getString(dbNameColumn),
                    LocalDateTime.parse(rs.getString(creationDateTimeColumn)),
                    customer.get(),
                    new Part(null, null, 0, null, defaultPartId),
                    rs.getString(productNameColumn),
                    rs.getString(productNotesColumn),
                    rs.getInt(idColumn)
                ));
            } 
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(
                Level.SEVERE,
                LocalDateTime.now() + ": Could not load Products from database " + e.getMessage()
            );
            products.clear();
        }
    };

    /**
     * Update the database with the default part id for each product.
     */
    protected static void updateDefaultParts() {
        ObservableList<Product> copyProducts = FXCollections.observableArrayList(products);
        copyProducts.forEach((product) -> {
            Optional<Part> defaultPart = PartDAO.getPart(product.getDefaultPart().getId());
            defaultPart.ifPresent((part) -> {
                product.setDefaultPart(part);
            });
            if (!defaultPart.isPresent()) {
                Logger.getLogger(MainApp.class.getName()).log(Level.FINE, "No default part found");
            }
        });
    }

    /**
     * Get a product by id.
     * @param id
     * @return Optional<Product>
     */
    public static Optional<Product> getProduct(int id) {
        return products.stream().filter((product) -> product.getId() == id).findFirst();
    }

    /**
     * Get a product by database name.
     * @param name
     * @return Optional<Product>
     */
    public static Optional<Product> getProductByDBName(String name) {
        return products.stream().filter((product) -> product.getDBName().equals(name)).findFirst();
    }

    /**
     * Get a stream of products by customer.
     * @param customer
     * @return Stream<Product>
     */
    public static Stream<Product> getProductsByCustomer(Customer customer) {
        return products.stream().filter((product) -> product.getCustomer().equals(customer));
    }

    /**
     * Insert a new product into the database.
     * @param dbName
     * @param customer
     */
    public static void insertProduct(String dbName, Customer customer) {
        LocalDateTime now = LocalDateTime.now();
        int id = (int) CRUDUtil.create(tableName, new String[]{dbNameColumn, creationDateTimeColumn, customerIdColumn}, new Object[]{dbName, now, customer.getId()}, new int[]{Types.VARCHAR, Types.TIMESTAMP, Types.INTEGER});
        products.add(new Product(dbName, now, customer, id));
    }

    /**
     * Update a product in the database.
     * @param newProduct
     */
    public static void updateProduct(Product newProduct) {
        int rows;
        if (newProduct.getDefaultPart() != null) {
            rows = CRUDUtil.update(
                tableName,
                new String[]{dbNameColumn, customerIdColumn, defaultPartIdColumn, productNameColumn, productNotesColumn},
                new Object[]{newProduct.getDBName(), newProduct.getCustomer().getId(), newProduct.getDefaultPart().getId(), newProduct.getProductName(), newProduct.getProductNotes()},
                new int[]{Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR},
                idColumn,
                Types.INTEGER,
                newProduct.getId()
            );
        } else {
            rows = CRUDUtil.update(
                tableName,
                new String[]{dbNameColumn, customerIdColumn, defaultPartIdColumn, productNameColumn, productNotesColumn},
                new Object[]{newProduct.getDBName(), newProduct.getCustomer().getId(), null, newProduct.getProductName(), newProduct.getProductNotes()},
                new int[]{Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR},
                idColumn,
                Types.INTEGER,
                newProduct.getId()
            );
        }
       
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

    /**
     * Delete a product from the database.
     * @param id
     */
    public static void deleteProduct(int id) {
        CRUDUtil.delete(tableName, id);

        Optional<Product> product = getProduct(id);
        product.ifPresent(products::remove);
    }
}
