package com.ils.logic;

import com.ils.logic.DAO.TransferDAO;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;


public class Quantities{
        public Integer getOpeningBalByCustomer(Customer cust, LocalDate from) {
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

        public Integer getOpeningBalByProduct(Product prod, LocalDate from) {
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

        public Integer getOpeningBalByPart(Part part, LocalDate from) {
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

        public Integer getClosingBalByCustomer(Customer cust, LocalDate to) {
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

        public Integer getClosingBalByProduct(Product prod, LocalDate to) {
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

        public Integer getClosingBalByPart(Part part, LocalDate to) {
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

        public Integer getDailyTransferSumByProduct(Product prod, LocalDate start, LocalDate end) {
                Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod)
                                .sorted((t1, t2) -> t1.getTransferDateTime()
                                                .compareTo(t2.getTransferDateTime()));
                return matches
                                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(start)
                                                || t.getTransferDateTime().toLocalDate().isAfter(start))
                                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(end)
                                                || t.getTransferDateTime().toLocalDate().isBefore(end))
                                .filter(t -> t.getTransferType() == Transfer.Action.DAILY)
                                .mapToInt(t -> t.getTransferQuantity()).sum();
        }

        public Integer getDailyTransferSumByPart(Part part, LocalDate start, LocalDate end) {
                Stream<Transfer> matches = TransferDAO.getTransfersByPart(part)
                                .sorted((t1, t2) -> t1.getTransferDateTime()
                                                .compareTo(t2.getTransferDateTime()));
                return matches
                                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(start)
                                                || t.getTransferDateTime().toLocalDate().isAfter(start))
                                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(end)
                                                || t.getTransferDateTime().toLocalDate().isBefore(end))
                                .filter(t -> t.getTransferType() == Transfer.Action.DAILY)
                                .mapToInt(t -> t.getTransferQuantity()).sum();
        }

        public Integer getRenewalTransferSumByProduct(Product prod, LocalDate start, LocalDate end) {
                Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod)
                                .sorted((t1, t2) -> t1.getTransferDateTime()
                                                .compareTo(t2.getTransferDateTime()));
                return matches
                                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(start)
                                                || t.getTransferDateTime().toLocalDate().isAfter(start))
                                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(end)
                                                || t.getTransferDateTime().toLocalDate().isBefore(end))
                                .filter(t -> t.getTransferType() == Transfer.Action.RENEWAL)
                                .mapToInt(t -> t.getTransferQuantity()).sum();
        }

        public Integer getRenewalTransferSumByPart(Part part, LocalDate start, LocalDate end) {
                Stream<Transfer> matches = TransferDAO.getTransfersByPart(part)
                                .sorted((t1, t2) -> t1.getTransferDateTime()
                                                .compareTo(t2.getTransferDateTime()));
                return matches
                                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(start)
                                                || t.getTransferDateTime().toLocalDate().isAfter(start))
                                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(end)
                                                || t.getTransferDateTime().toLocalDate().isBefore(end))
                                .filter(t -> t.getTransferType() == Transfer.Action.RENEWAL)
                                .mapToInt(t -> t.getTransferQuantity()).sum();
        }

        public Integer getRejectTransferSumByProduct(Product prod, LocalDate start, LocalDate end) {
                Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod)
                                .sorted((t1, t2) -> t1.getTransferDateTime()
                                                .compareTo(t2.getTransferDateTime()));
                return matches
                                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(start)
                                                || t.getTransferDateTime().toLocalDate().isAfter(start))
                                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(end)
                                                || t.getTransferDateTime().toLocalDate().isBefore(end))
                                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_DAILY
                                                || t.getTransferType() == Transfer.Action.REJECT_PROJECT
                                                || t.getTransferType() == Transfer.Action.REJECT_RENEWAL)
                                .mapToInt(t -> t.getTransferQuantity()).sum();
        }

        public Integer getRejectTransferSumByPart(Part part, LocalDate start, LocalDate end) {
                Stream<Transfer> matches = TransferDAO.getTransfersByPart(part)
                                .sorted((t1, t2) -> t1.getTransferDateTime()
                                                .compareTo(t2.getTransferDateTime()));
                return matches
                                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(start)
                                                || t.getTransferDateTime().toLocalDate().isAfter(start))
                                .filter(t -> t.getTransferDateTime().toLocalDate().isEqual(end)
                                                || t.getTransferDateTime().toLocalDate().isBefore(end))
                                .filter(t -> t.getTransferType() == Transfer.Action.REJECT_DAILY
                                                || t.getTransferType() == Transfer.Action.REJECT_PROJECT
                                                || t.getTransferType() == Transfer.Action.REJECT_RENEWAL)
                                .mapToInt(t -> t.getTransferQuantity()).sum();
        }
}