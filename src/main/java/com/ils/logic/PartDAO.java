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
import java.util.stream.Stream;

import com.ils.db.CRUDUtil;
import com.ils.db.Database;
import com.ils.models.Part;
import com.ils.models.Product;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class PartDAO {
    private static final String tableName = "PART";
    private static final String nameColumn = "PARTNAME";
    private static final String creationDateTimeColumn = "CREATIONDATETIME";
    private static final String quantityColumn = "PARTQUANTITY";
    private static final String productIdColumn = "PRODUCTID";
    private static final String nextPartIdColumn = "NEXTPARTID";
    private static final String idColumn = "PARTID";

    private static final ObservableList<Part> parts;

    static {
        parts = FXCollections.observableArrayList();
        updatePartsFromDB();
        ProductDAO.updateDefaultParts();
    }

    protected static FilteredList<Part> getParts() {
        return new FilteredList<>(parts);
    }

    private static void updatePartsFromDB() {
        String query = "SELECT * FROM " + tableName;
        try (Connection connection = Database.connect()) {
            PreparedStatement statement = connection.prepareStatement(query); 
            ResultSet rs = statement.executeQuery();
            parts.clear();
            while (rs.next()) {
                Optional<Product> product = ProductDAO.getProduct(rs.getInt(productIdColumn));
                if (!product.isPresent()) {
                    Logger.getAnonymousLogger().log(
                        Level.SEVERE,
                        LocalDateTime.now() + ": Could not load Part from database, Product with id " + rs.getInt(productIdColumn) + " not found"
                    );
                    continue;
                }
                parts.add(new Part(
                    rs.getString(nameColumn),
                    LocalDateTime.parse(rs.getString(creationDateTimeColumn)),
                    rs.getInt(quantityColumn),
                    product.get(),
                    new Part(null, null, 0, null, rs.getInt(nextPartIdColumn)),
                    rs.getInt(idColumn)
                ));
            } 
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                Level.SEVERE,
                LocalDateTime.now() + ": Could not load Parts from database "
            );
            parts.clear();
        }
    }

    public static Optional<Part> getPart(int id) {
        return parts.stream().filter(part -> part.getId() == id).findFirst();
    }

    public static Optional<Part> getPartByNameAndProduct(String partName, Product product) {
        return parts.stream().filter(part -> part.getPartName().equals(partName) && part.getProduct().equals(product)).findFirst();
    }

    public static Stream<Part> getPartsByProduct(Product product) {
        return parts.stream().filter(part -> part.getProduct().equals(product));
    }

    public static void insertPart(String partName, int quantity, Product product) {
        LocalDateTime now = LocalDateTime.now();
        int id = (int) CRUDUtil.create(
            tableName,
            new String[]{nameColumn, creationDateTimeColumn, quantityColumn, productIdColumn},
            new Object[]{partName, now, quantity, product.getId()},
            new int []{Types.VARCHAR, Types.TIMESTAMP, Types.INTEGER, Types.INTEGER});
        parts.add(new Part(partName, now, quantity, product, id));
    }

    public static void updatePart(Part newPart) {
        int rows = CRUDUtil.update(
            tableName,
            new String[]{nameColumn, quantityColumn, productIdColumn, nextPartIdColumn},
            new Object[]{newPart.getPartName(), newPart.getPartQuantity(), newPart.getProduct().getId(), newPart.getNextPart().getId()},
            new int[]{Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER},
            idColumn,
            Types.INTEGER,
            newPart.getId()
        );


        if (rows == 0) {
            throw new IllegalStateException("Part to be updated with id" + newPart.getId() + " does not exist in database");
        }

        Optional<Part> optionalPart= getPart(newPart.getId());
        optionalPart.ifPresent((oldPart) -> {
            parts.remove(oldPart);
            parts.add(newPart);
        });
        optionalPart.orElseThrow(() -> {
            throw new IllegalStateException("Part to be updated with id" + newPart.getId() + " does not exist in database.");
        });
    }

    public static void deletePart(int id) {
        CRUDUtil.delete(tableName, id);

        Optional<Part> part = getPart(id);
        part.ifPresent((e) -> {
            parts.remove(e);
        });
    }
}