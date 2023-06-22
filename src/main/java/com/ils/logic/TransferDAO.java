package com.ils.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.ils.db.CRUDUtil;
import com.ils.db.Database;
import com.ils.models.Product;
import com.ils.models.Transfer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class TransferDAO {
    private static final String tableName = "TRANSFER";
    private static final String transferTimeColumn = "TRANSFERDATETIME";
    // private static final String partIdColumn = "PARTID";
    private static final String productIdColumn = "PRODUCTID";
    private static final String quantityColumn = "TRANSFERQUANTITY";
    private static final String transferTypeColumn = "TRANSFERTYPE";
    private static final String idColumn = "TRANSFERID";

    private static final ObservableList<Transfer> transfers;

    static {
        transfers = FXCollections.observableArrayList();
        updateTransfersFromDB();
    }

    protected static FilteredList<Transfer> getTransfers() {
        return new FilteredList<>(transfers);
    }

    private static void updateTransfersFromDB() {
        String query = "SELECT * FROM " + tableName + " LIMIT 500";
        try (Connection connection = Database.connect()) {
            // Bug here
            PreparedStatement statement = connection.prepareStatement(query); 
            ResultSet rs = statement.executeQuery();
            transfers.clear();
            while (rs.next()) {
                // Optional<Part> part = PartDAO.getPart(rs.getInt(partIdColumn));
                // if (!part.isPresent()) {
                //     Logger.getAnonymousLogger().log(
                //         Level.SEVERE,
                //         LocalDateTime.now() + ": Could not load Part with id " + rs.getInt(partIdColumn) + " from database"
                //     );
                //     continue;
                // }
                // transfers.add(new Transfer(
                //     rs.getTimestamp(transferTimeColumn).toLocalDateTime(),
                //     part.get(),
                //     rs.getInt(quantityColumn),
                //     Transfer.Action.valueOf(rs.getString(transferTypeColumn)),
                //     rs.getInt(idColumn)
                // ));
                Integer productId = rs.getInt(productIdColumn);
                Optional<Product> product = ProductDAO.getProduct(productId);
                product.orElseThrow(() -> new IllegalStateException("Could not find Product with id " + productId));
                transfers.add(new Transfer(
                    LocalDateTime.parse(rs.getString(transferTimeColumn)),
                    product.get(),
                    rs.getInt(quantityColumn),
                    Transfer.Action.valueOf(rs.getString(transferTypeColumn)),
                    rs.getInt(idColumn)
                ));
            } 
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                Level.SEVERE,
                LocalDateTime.now() + ": Could not load Transfers from database " + e.getMessage()
            );
            transfers.clear();
        }
    }

    public static Optional<Transfer> getTransfer(int id) {
        return transfers.stream().filter(t -> t.getId() == id).findFirst();
    }

    public static Stream<Transfer> getTransfersByDate(LocalDate date) {
        return transfers.stream().filter(t -> t.getTransferDateTime().toLocalDate().equals(date));
    }

    public static void insertTransfer(Product product, int quantity, Transfer.Action transferType) {
        LocalDateTime transferDateTime = LocalDateTime.now();
        // int id = (int) CRUDUtil.create(
        //     tableName,
        //     new String[] { transferTimeColumn, partIdColumn, quantityColumn, transferTypeColumn },
        //     new Object[] { transferDateTime, part.getId(), quantity, transferType.name() },
        //     new int[] { Types.TIMESTAMP, Types.INTEGER, Types.INTEGER, Types.VARCHAR }
        // );
        // transfers.add(new Transfer(transferDateTime, part, quantity, transferType, id));
        int id = (int) CRUDUtil.create(
            tableName,
            new String[] { transferTimeColumn, productIdColumn, quantityColumn, transferTypeColumn },
            new Object[] { transferDateTime, product.getId(), quantity, transferType.name() },
            new int[] { Types.TIMESTAMP, Types.INTEGER, Types.INTEGER, Types.VARCHAR }
        );
        transfers.add(new Transfer(transferDateTime, product, quantity, transferType, id));
    }

    public static void updateTransfer(Transfer newTransfer) {
        // int rows = CRUDUtil.update(
        //     tableName,
        //     new String[] { transferTimeColumn, partIdColumn, quantityColumn, transferTypeColumn },
        //     new Object[] { newTransfer.getTransferDateTime(), newTransfer.getPart().getId(), newTransfer.getQuantity(), newTransfer.getTransferType().name() },
        //     new int[] { Types.TIMESTAMP, Types.INTEGER, Types.INTEGER, Types.VARCHAR },
        //     idColumn,
        //     Types.INTEGER,
        //     newTransfer.getId()
        // );
        int rows = CRUDUtil.update(
            tableName,
            new String[] { transferTimeColumn, productIdColumn, quantityColumn, transferTypeColumn },
            new Object[] { newTransfer.getTransferDateTime(), newTransfer.getProduct().getId(), newTransfer.getTransferQuantity(), newTransfer.getTransferType().name() },
            new int[] { Types.TIMESTAMP, Types.INTEGER, Types.INTEGER, Types.VARCHAR },
            idColumn,
            Types.INTEGER,
            newTransfer.getId()
        );
        if (rows == 0) {
            throw new IllegalStateException("Transfer to be updated with id " + newTransfer.getId() + " does not exist in database");
        }

        Optional<Transfer> optionalTransfer = getTransfer(newTransfer.getId());
        optionalTransfer.ifPresent((oldTransfer) -> {
            transfers.remove(oldTransfer);
            transfers.add(newTransfer);
        });
        optionalTransfer.orElseThrow(() -> {
            throw new IllegalStateException("Transfer to be updated with id" + newTransfer.getId() + " does not exist in database.");
        });
    }

    public static void deleteTransfer(int id) {
        CRUDUtil.delete(tableName, id);

        Optional<Transfer> transfer = getTransfer(id);
        transfer.ifPresent(transfers::remove);
    }
}
