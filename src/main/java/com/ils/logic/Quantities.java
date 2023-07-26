package com.ils.logic;

import com.ils.logic.DAO.TransferDAO;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class Quantities {
    private static LocalDate from = YearMonth.from(LocalDate.now()).atDay(1);
    private static LocalDate to = YearMonth.from(LocalDate.now()).atEndOfMonth();

    public static LocalDate getFrom() {
        return from;
    }

    public static LocalDate getTo() {
        return to;
    }

    public static void setFrom(LocalDate from) {
        Quantities.from = from;
    }

    public static void setTo(LocalDate to) {
        Quantities.to = to;
    }

    // Refactor methods below to use static variables from and to


    public static Integer getOpeningBalByCustomer(Customer cust) {
        Stream<Transfer> matches = TransferDAO.getTransfersByCustomer(cust)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        Optional<Transfer> earliest = matches.filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                || t.getTransferDateTime().toLocalDate().isAfter(from)).findFirst();
        if (!earliest.isPresent()) {
            return -1;
        }
        Transfer t = earliest.get();
        return t.getPrevPartQuantity();
    }

    public static Integer getOpeningBalByProduct(Product prod) {
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        Optional<Transfer> earliest = matches.filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                || t.getTransferDateTime().toLocalDate().isAfter(from)).findFirst();
        if (!earliest.isPresent()) {
            return -1;
        }
        Transfer t = earliest.get();
        return t.getPrevPartQuantity();
    }

    public static Integer getOpeningBalByPart(Part part) {
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        Optional<Transfer> earliest = matches.filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                || t.getTransferDateTime().toLocalDate().isAfter(from)).findFirst();
        if (!earliest.isPresent()) {
            return -1;
        }
        Transfer t = earliest.get();
        return t.getPrevPartQuantity();
    }

    public static Integer getClosingBalByCustomer(Customer cust) {
        Stream<Transfer> matches = TransferDAO.getTransfersByCustomer(cust)
                .sorted((t1, t2) -> t2.getTransferDateTime()
                        .compareTo(t1.getTransferDateTime()));
        Optional<Transfer> latest = matches.filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                || t.getTransferDateTime().toLocalDate().isBefore(to)).findFirst();
        if (!latest.isPresent()) {
            return -1;
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

    public static Integer getClosingBalByProduct(Product prod) {
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod)
                .sorted((t1, t2) -> t2.getTransferDateTime()
                        .compareTo(t1.getTransferDateTime()));
        Optional<Transfer> latest = matches.filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                || t.getTransferDateTime().toLocalDate().isBefore(to)).findFirst();
        if (!latest.isPresent()) {
            return -1;
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

    public static Integer getClosingBalByPart(Part part) {
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part)
                .sorted((t1, t2) -> t2.getTransferDateTime()
                        .compareTo(t1.getTransferDateTime()));
        Optional<Transfer> latest = matches.filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                || t.getTransferDateTime().toLocalDate().isBefore(to)).findFirst();
        if (!latest.isPresent()) {
            return -1;
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
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        return matches
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                        || t.getTransferDateTime().toLocalDate().isAfter(from))
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                        || t.getTransferDateTime().toLocalDate().isBefore(to))
                .filter(t -> t.getTransferType() == Transfer.Action.DAILY)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getDailyTransferSumByPart(Part part) {
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        return matches
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                        || t.getTransferDateTime().toLocalDate().isAfter(from))
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                        || t.getTransferDateTime().toLocalDate().isBefore(to))
                .filter(t -> t.getTransferType() == Transfer.Action.DAILY)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getRenewalTransferSumByProduct(Product prod) {
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        return matches
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                        || t.getTransferDateTime().toLocalDate().isAfter(from))
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                        || t.getTransferDateTime().toLocalDate().isBefore(to))
                .filter(t -> t.getTransferType() == Transfer.Action.RENEWAL)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getRenewalTransferSumByPart(Part part) {
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        return matches
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                        || t.getTransferDateTime().toLocalDate().isAfter(from))
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                        || t.getTransferDateTime().toLocalDate().isBefore(to))
                .filter(t -> t.getTransferType() == Transfer.Action.RENEWAL)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

//     public static Integer getRejectTransferSumByProduct(Product prod) {
//         Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod)
//                 .sorted((t1, t2) -> t1.getTransferDateTime()
//                         .compareTo(t2.getTransferDateTime()));
//         return matches
//                 .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
//                         || t.getTransferDateTime().toLocalDate().isAfter(from))
//                 .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
//                         || t.getTransferDateTime().toLocalDate().isBefore(to))
//                 .filter(t -> t.getTransferType() == Transfer.Action.REJECT_DAILY
//                         || t.getTransferType() == Transfer.Action.REJECT_PROJECT
//                         || t.getTransferType() == Transfer.Action.REJECT_RENEWAL)
//                 .mapToInt(t -> t.getTransferQuantity()).sum();
//     }

//     public static Integer getRejectTransferSumByPart(Part part) {
//         Stream<Transfer> matches = TransferDAO.getTransfersByPart(part)
//                 .sorted((t1, t2) -> t1.getTransferDateTime()
//                         .compareTo(t2.getTransferDateTime()));
//         return matches
//                 .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
//                         || t.getTransferDateTime().toLocalDate().isAfter(from))
//                 .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
//                         || t.getTransferDateTime().toLocalDate().isBefore(to))
//                 .filter(t -> t.getTransferType() == Transfer.Action.REJECT_DAILY
//                         || t.getTransferType() == Transfer.Action.REJECT_PROJECT
//                         || t.getTransferType() == Transfer.Action.REJECT_RENEWAL)
//                 .mapToInt(t -> t.getTransferQuantity()).sum();
//     }

    public static Integer getRejectDailyTransferSumByProduct(Product prod) {
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        return matches
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                        || t.getTransferDateTime().toLocalDate().isAfter(from))
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                        || t.getTransferDateTime().toLocalDate().isBefore(to))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_DAILY)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getRejectDailyTransferSumByPart(Part part) {
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        return matches
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                        || t.getTransferDateTime().toLocalDate().isAfter(from))
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                        || t.getTransferDateTime().toLocalDate().isBefore(to))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_DAILY)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getRejectRenewalTransferSumByProduct(Product prod) {
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        return matches
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                        || t.getTransferDateTime().toLocalDate().isAfter(from))
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                        || t.getTransferDateTime().toLocalDate().isBefore(to))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_RENEWAL)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getRejectRenewalTransferSumByPart(Part part) {
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        return matches
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                        || t.getTransferDateTime().toLocalDate().isAfter(from))
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                        || t.getTransferDateTime().toLocalDate().isBefore(to))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_RENEWAL)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getRejectProjectTransferSumByProduct(Product prod) {
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        return matches
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                        || t.getTransferDateTime().toLocalDate().isAfter(from))
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                        || t.getTransferDateTime().toLocalDate().isBefore(to))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_DAILY)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getRejectProjectTransferSumByPart(Part part) {
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        return matches
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                        || t.getTransferDateTime().toLocalDate().isAfter(from))
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                        || t.getTransferDateTime().toLocalDate().isBefore(to))
                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_PROJECT)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getSampleTransferSumByProduct(Product prod) {
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        return matches
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                        || t.getTransferDateTime().toLocalDate().isAfter(from))
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                        || t.getTransferDateTime().toLocalDate().isBefore(to))
                .filter(t -> t.getTransferType() == Transfer.Action.SAMPLE)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getSampleTransferSumByPart(Part part) {
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        return matches
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                        || t.getTransferDateTime().toLocalDate().isAfter(from))
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                        || t.getTransferDateTime().toLocalDate().isBefore(to))
                .filter(t -> t.getTransferType() == Transfer.Action.SAMPLE)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getReceivedTransferSumByProduct(Product prod) {
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        return matches
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                        || t.getTransferDateTime().toLocalDate().isAfter(from))
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                        || t.getTransferDateTime().toLocalDate().isBefore(to))
                .filter(t -> t.getTransferType() == Transfer.Action.RECEIVED)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }

    public static Integer getReceivedTransferSumByPart(Part part) {
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part)
                .sorted((t1, t2) -> t1.getTransferDateTime()
                        .compareTo(t2.getTransferDateTime()));
        return matches
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from)
                        || t.getTransferDateTime().toLocalDate().isAfter(from))
                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to)
                        || t.getTransferDateTime().toLocalDate().isBefore(to))
                .filter(t -> t.getTransferType() == Transfer.Action.RECEIVED)
                .mapToInt(t -> t.getTransferQuantity()).sum();
    }
}