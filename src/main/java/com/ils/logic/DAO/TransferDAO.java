package com.ils.logic.DAO;

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

import com.ils.MainApp;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;
import com.ils.sqlite.CRUDUtil;
import com.ils.sqlite.Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class TransferDAO {
    // Database columns and table name
    private static final String tableName = "TRANSFER";
    private static final String transferTimeColumn = "TRANSFERDATETIME";
    private static final String partIdColumn = "PARTID";
    private static final String prevPartQuantityColumn = "PREVPARTQUANTITY";
    private static final String quantityColumn = "TRANSFERQUANTITY";
    private static final String transferTypeColumn = "TRANSFERTYPE";
    private static final String idColumn = "TRANSFERID";

    private static final ObservableList<Transfer> transfers;

    static {
        transfers = FXCollections.observableArrayList();
        updateTransfersFromDB();
    }

    /**
     * Get a filter wrapper around the transfers list.
     * @return FilteredList<Transfer> using transfers list as source
     */
    public static FilteredList<Transfer> getTransfers() {
        return new FilteredList<>(transfers);
    }

    /**
     * Update the transfers list from the database.
     * @throws SQLException 
     */
    private static void updateTransfersFromDB() {
        String query = "SELECT * FROM " + tableName;
        try (Connection connection = Database.connect()) {
            PreparedStatement statement = connection.prepareStatement(query); 
            ResultSet rs = statement.executeQuery();
            transfers.clear();
            while (rs.next()) {
                Integer partId = rs.getInt(partIdColumn);
                Optional<Part> part = PartDAO.getPart(partId);
                part.orElseThrow(() -> new IllegalStateException("Could not find Part with id " + partId));
                transfers.add(new Transfer(
                    LocalDateTime.parse(rs.getString(transferTimeColumn)),
                    part.get(),
                    rs.getInt(prevPartQuantityColumn),
                    rs.getInt(quantityColumn),
                    Transfer.Action.valueOf(rs.getString(transferTypeColumn)),
                    rs.getInt(idColumn)
                ));
            } 
        } catch (SQLException e) {
            Logger.getLogger(MainApp.class.getName()).log(
                Level.SEVERE,
                LocalDateTime.now() + ": Could not load Transfers from database " + e.getMessage()
            );
            transfers.clear();
        }
    }

    /**
     * Get a transfer by id.
     * @param id
     * @return Optional<Transfer>
     */
    public static Optional<Transfer> getTransfer(int id) {
        return transfers.stream().filter(t -> t.getId() == id).findFirst();
    }

    /**
     * Get a transfer by part and date.
     * @param part
     * @param date
     * @return Optional<Transfer>
     */
    public static Optional<Transfer> getTransferByPartAndDate(Part part, LocalDate date) {
        return transfers.stream().filter(t -> t.getPart().equals(part) && t.getTransferDateTime().toLocalDate().equals(date)).findFirst();
    }

    /**
     * Get a stream of transfers by customer.
     * @param cust
     * @return Stream<Transfer>
     */
    public static Stream<Transfer> getTransfersByCustomer(Customer cust) {
        return transfers.stream().filter(t -> t.getPart().getProduct().getCustomer().equals(cust));
    }

    /**
     * Get a stream of transfers by product.
     * @param product
     * @return Stream<Transfer>
     */
    public static Stream<Transfer> getTransfersByProduct(Product product) {
        return transfers.stream().filter(t -> t.getPart().getProduct().equals(product));
    }

    /**
     * Get a stream of transfers by product and date.
     * @param prod
     * @param date
     * @return Stream<Transfer>
     */
    public static Stream<Transfer> getTransfersByProductAndDate(Product prod, LocalDate date) {
        return transfers.stream().filter(t -> t.getPart().getProduct().equals(prod) && t.getTransferDateTime().toLocalDate().equals(date));
    }

    /**
     * Get a stream of transfers by part.
     * @param part
     * @return
     */
    public static Stream<Transfer> getTransfersByPart(Part part) {
        return transfers.stream().filter(t -> t.getPart().equals(part));
    }

    /**
     * Get a stream of transfers by date.
     * @param date
     * @return Stream<Transfer>
     */
    public static Stream<Transfer> getTransfersByDate(LocalDate date) {
        return transfers.stream().filter(t -> t.getTransferDateTime().toLocalDate().equals(date));
    }

    /**
     * Insert a transfer into the database.
     * @param part
     * @param quantity
     * @param transferType
     */
    public static void insertTransfer(Part part, int quantity, Transfer.Action transferType) {
        LocalDateTime transferDateTime = LocalDateTime.now();
        int id = (int) CRUDUtil.create(
            tableName,
            new String[] { transferTimeColumn, partIdColumn, prevPartQuantityColumn, quantityColumn, transferTypeColumn },
            new Object[] { transferDateTime, part.getId(), part.getPartQuantity(), quantity, transferType.name() },
            new int[] { Types.TIMESTAMP, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.VARCHAR }
        );
        transfers.add(new Transfer(transferDateTime, part, part.getPartQuantity(), quantity, transferType, id));
    }

    /**
     * Insert a transfer into the database with a specified date.
     * @param part
     * @param quantity
     * @param transferType
     * @param date
     */
    public static void insertTransfer(Part part, int quantity, Transfer.Action transferType, LocalDate date) {
        int id = (int) CRUDUtil.create(
            tableName,
            new String[] { transferTimeColumn, partIdColumn, prevPartQuantityColumn, quantityColumn, transferTypeColumn },
            new Object[] { date.atStartOfDay(), part.getId(), part.getPartQuantity(), quantity, transferType.name() },
            new int[] { Types.TIMESTAMP, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.VARCHAR }
        );
        transfers.add(new Transfer(date.atStartOfDay(), part, part.getPartQuantity(), quantity, transferType, id));
    }

    /**
     * Update a transfer in the database.
     * @param newTransfer
     * @throws IllegalStateException
     */
    public static void updateTransfer(Transfer newTransfer) {
        int rows = CRUDUtil.update(
            tableName,
            new String[] { partIdColumn, prevPartQuantityColumn, quantityColumn, transferTypeColumn },
            new Object[] { newTransfer.getPart().getId(), newTransfer.getPrevPartQuantity(), newTransfer.getTransferQuantity(), newTransfer.getTransferType().name() },
            new int[] { Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.VARCHAR },
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

    /**
     * Delete a transfer from the database.
     * @param id
     */
    public static void deleteTransfer(int id) {
        CRUDUtil.delete(tableName, id);

        Optional<Transfer> transfer = getTransfer(id);
        transfer.ifPresent(transfers::remove);
    }
}
