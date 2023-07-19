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
import com.ils.db.CRUDUtil;
import com.ils.db.Database;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class TransferDAO {
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

    public static FilteredList<Transfer> getTransfers() {
        return new FilteredList<>(transfers);
    }

    private static void updateTransfersFromDB() {
        String query = "SELECT * FROM " + tableName + " LIMIT 500";
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

    public static Optional<Transfer> getTransfer(int id) {
        return transfers.stream().filter(t -> t.getId() == id).findFirst();
    }

    public static Optional<Transfer> getTransferByPartAndDate(Part part, LocalDate date) {
        return transfers.stream().filter(t -> t.getPart().equals(part) && t.getTransferDateTime().toLocalDate().equals(date)).findFirst();
    }

    public static Stream<Transfer> getTransfersByCustomer(Customer cust) {
        return transfers.stream().filter(t -> t.getPart().getProduct().getCustomer().equals(cust));
    }

    public static Stream<Transfer> getTransfersByProduct(Product product) {
        return transfers.stream().filter(t -> t.getPart().getProduct().equals(product));
    }

    public static Stream<Transfer> getTransfersByProductAndDate(Product prod, LocalDate date) {
        return transfers.stream().filter(t -> t.getPart().getProduct().equals(prod) && t.getTransferDateTime().toLocalDate().equals(date));
    }

    public static Stream<Transfer> getTransfersByPart(Part part) {
        return transfers.stream().filter(t -> t.getPart().equals(part));
    }

    public static Stream<Transfer> getTransfersByDate(LocalDate date) {
        return transfers.stream().filter(t -> t.getTransferDateTime().toLocalDate().equals(date));
    }

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

    public static void deleteTransfer(int id) {
        CRUDUtil.delete(tableName, id);

        Optional<Transfer> transfer = getTransfer(id);
        transfer.ifPresent(transfers::remove);
    }
}