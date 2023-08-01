package com.ils.logic;

import com.ils.logic.DAO.PartDAO;
import com.ils.logic.DAO.ProductDAO;
import com.ils.logic.DAO.TransferDAO;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class Quantities {
    private static ObjectProperty<LocalDate> from = new SimpleObjectProperty<>(
            YearMonth.from(LocalDate.now()).atDay(1));
    private static ObjectProperty<LocalDate> to = new SimpleObjectProperty<>(
            YearMonth.from(LocalDate.now()).atEndOfMonth());

    public static ObjectProperty<LocalDate> getFrom() {
        return from;
    }

    public static ObjectProperty<LocalDate> getTo() {
        return to;
    }

    public static void setFrom(LocalDate from) {
        Quantities.from.setValue(from);
    }

    public static void setTo(LocalDate to) {
        Quantities.to.setValue(to);
    }

    public static Integer getOpeningBalByCustomer(Customer cust) {
        return ProductDAO.getProductsByCustomer(cust).mapToInt(Quantities::getOpeningBalByProduct).sum();
    }

    public static Integer getOpeningBalByProduct(Product prod) {
        return PartDAO.getPartsByProduct(prod).mapToInt(Quantities::getOpeningBalByPart).sum();
    }

    public static Integer getOpeningBalByPart(Part part) {
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        Optional<Transfer> earliest = matches.findFirst();
        if (!earliest.isPresent()) {
            return part.getPartQuantity();
        }
        Transfer t = earliest.get();
        return t.getPrevPartQuantity();
    }

    public static Integer getClosingBalByCustomer(Customer cust) {
        return ProductDAO.getProductsByCustomer(cust).mapToInt(Quantities::getClosingBalByProduct).sum();
    }

    public static Integer getClosingBalByProduct(Product prod) {
        return PartDAO.getPartsByProduct(prod).mapToInt(Quantities::getClosingBalByPart).sum();
    }

    public static Integer getClosingBalByPart(Part part) {
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .sorted((t1, t2) -> t2.getTransferDateTime()
                        .compareTo(t1.getTransferDateTime()));
        Optional<Transfer> latest = matches.findFirst();
        if (!latest.isPresent()) {
            return part.getPartQuantity();
        }
        Transfer t = latest.get();
        switch (t.getTransferType()) {
            case DAILY:
            case DESTRUCT:
            case PROJECT:
            case REJECT_DAILY:
            case REJECT_PROJECT:
            case REJECT_RENEWAL:
            case RENEWAL:
            case SAMPLE:
                return t.getPrevPartQuantity() - t.getTransferQuantity();
            case RECEIVED:
                return t.getPrevPartQuantity() + t.getTransferQuantity();
            default:
                return t.getPrevPartQuantity();
        }
    }

    public static Integer getDailyTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.DAILY)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getDailyTransferSumByPart(Part part) {
        return TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.DAILY)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getRenewalTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.RENEWAL)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getRenewalTransferSumByPart(Part part) {
        return TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.RENEWAL)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getProjectTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.PROJECT)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getProjectTransferSumByPart(Part part) {
        return TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.PROJECT)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getRejectDailyTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_DAILY)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getRejectDailyTransferSumByPart(Part part) {
        return TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_DAILY)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getRejectRenewalTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_RENEWAL)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getRejectRenewalTransferSumByPart(Part part) {
        return TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_RENEWAL)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getRejectProjectTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_PROJECT)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getRejectProjectTransferSumByPart(Part part) {
        return TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_PROJECT)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getSampleTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.SAMPLE)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getSampleTransferSumByPart(Part part) {
        return TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.SAMPLE)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getReceivedTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.RECEIVED)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getReceivedTransferSumByPart(Part part) {
        return TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.RECEIVED)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }
}