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
    // Initialize from and to to the first and last day of the current month.
    private static ObjectProperty<LocalDate> from = new SimpleObjectProperty<>(
            YearMonth.from(LocalDate.now()).atDay(1));
    private static ObjectProperty<LocalDate> to = new SimpleObjectProperty<>(
            YearMonth.from(LocalDate.now()).atEndOfMonth());

    /**
     * Get the from date.
     * @return ObjectProperty<LocalDate>
     */
    public static ObjectProperty<LocalDate> getFrom() {
        return from;
    }

    /**
     * Get the to date.
     * @return ObjectProperty<LocalDate>
     */
    public static ObjectProperty<LocalDate> getTo() {
        return to;
    }

    /**
     * Set the from date.
     * @param from
     */
    public static void setFrom(LocalDate from) {
        Quantities.from.setValue(from);
    }

    /**
     * Set the to date.
     * @param to
     */
    public static void setTo(LocalDate to) {
        Quantities.to.setValue(to);
    }

    /**
     * Get the opening balance of a customer.
     * @param cust
     * @return Integer
     */
    public static Integer getOpeningBalByCustomer(Customer cust) {
        // Get the opening balance of each product and sum them up.
        return ProductDAO.getProductsByCustomer(cust).mapToInt(Quantities::getOpeningBalByProduct).sum();
    }

    /**
     * Get the opening balance of a product.
     * @param prod
     * @return Integer
     */
    public static Integer getOpeningBalByProduct(Product prod) {
        // Get the opening balance of each part and sum them up.
        return PartDAO.getPartsByProduct(prod).mapToInt(Quantities::getOpeningBalByPart).sum();
    }

    /**
     * Get the opening balance of a part.
     * @param part
     * @return Integer
     */
    public static Integer getOpeningBalByPart(Part part) {
        // Get the earliest transfer of the part after the from date.
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

    /**
     * Get the closing balance of a customer.
     * @param cust
     * @return Integer
     */
    public static Integer getClosingBalByCustomer(Customer cust) {
        // Get the closing balance of each product and sum them up.
        return ProductDAO.getProductsByCustomer(cust).mapToInt(Quantities::getClosingBalByProduct).sum();
    }

    /**
     * Get the closing balance of a product.
     * @param prod
     * @return Integer
     */
    public static Integer getClosingBalByProduct(Product prod) {
        // Get the closing balance of each part and sum them up.
        return PartDAO.getPartsByProduct(prod).mapToInt(Quantities::getClosingBalByPart).sum();
    }

    /**
     * Get the closing balance of a part.
     * @param part
     * @return Integer
     */
    public static Integer getClosingBalByPart(Part part) {
        // Get the latest transfer before the to date.
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
        // Get the closing balance based on the transfer type and previous quantity.
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

    /**
     * Get the daily transfer sum of a product.
     * @param prod
     * @return Integer
     */
    public static Integer getDailyTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.DAILY)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    /**
     * Get the daily transfer sum of a part.
     * @param part
     * @return Integer
     */
    public static Integer getDailyTransferSumByPart(Part part) {
        return TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.DAILY)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    /**
     * Get the renewal transfer sum of a product.
     * @param prod
     * @return Integer
     */
    public static Integer getRenewalTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.RENEWAL)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    /**
     * Get the renewal transfer sum of a part.
     * @param part
     * @return Integer
     */
    public static Integer getRenewalTransferSumByPart(Part part) {
        return TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.RENEWAL)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    /**
     * Get the project transfer sum of a product.
     * @param prod
     * @return Integer
     */
    public static Integer getProjectTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.PROJECT)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    /**
     * Get the project transfer sum of a part.
     * @param part
     * @return Integer
     */
    public static Integer getProjectTransferSumByPart(Part part) {
        return TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.PROJECT)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    /**
     * Get the reject-daily transfer sum of a product.
     * @param prod
     * @return Integer
     */
    public static Integer getRejectDailyTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_DAILY)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    /**
     * Get the reject-daily transfer sum of a part.
     * @param part
     * @return
     */
    public static Integer getRejectDailyTransferSumByPart(Part part) {
        return TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_DAILY)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    /**
     * Get the reject-renewal transfer sum of a product.
     * @param prod
     * @return
     */
    public static Integer getRejectRenewalTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_RENEWAL)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    /**
     * Get the reject-renewal transfer sum of a part.
     * @param part
     * @return Integer
     */
    public static Integer getRejectRenewalTransferSumByPart(Part part) {
        return TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_RENEWAL)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    /**
     * Get the reject-project transfer sum of a product.
     * @param prod
     * @return
     */
    public static Integer getRejectProjectTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_PROJECT)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    /**
     * Get the reject-project transfer sum of a part.
     * @param part
     * @return Integer
     */
    public static Integer getRejectProjectTransferSumByPart(Part part) {
        return TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_PROJECT)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    /**
     * Get the reject-sample transfer sum of a product.
     * @param prod
     * @return Integer
     */
    public static Integer getSampleTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.SAMPLE)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    /**
     * Get the reject-sample transfer sum of a part.
     * @param part
     * @return Integer
     */
    public static Integer getSampleTransferSumByPart(Part part) {
        return TransferDAO.getTransfersByPart(part)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.SAMPLE)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    /**
     * Get the reject-renewal transfer sum of a product.
     * @param prod
     * @return Integer
     */
    public static Integer getReceivedTransferSumByProduct(Product prod) {
        return TransferDAO.getTransfersByProduct(prod)
                .filter(t -> t.getTransferDateTime().toLocalDate().isAfter(from.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(from.getValue()))
                .filter(t -> t.getTransferDateTime().toLocalDate().isBefore(to.getValue())
                        || t.getTransferDateTime().toLocalDate().isEqual(to.getValue()))
                .filter(t -> t.getTransferType() == Transfer.Action.RECEIVED)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    /**
     * Get the reject-renewal transfer sum of a part.
     * @param part
     * @return Integer
     */
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