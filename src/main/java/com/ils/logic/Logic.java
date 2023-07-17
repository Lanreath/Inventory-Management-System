package com.ils.logic;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ils.logic.DAO.CustomerDAO;
import com.ils.logic.DAO.PartDAO;
import com.ils.logic.DAO.ProductDAO;
import com.ils.logic.DAO.TransferDAO;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

public class Logic {
    public Integer getOpeningBalByCustomer(Customer cust, LocalDate from) {
        Stream<Transfer> matches = TransferDAO.getTransfersByCustomer(cust).sorted((t1, t2) -> t1.getTransferDateTime()
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
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod).sorted((t1, t2) -> t1.getTransferDateTime()
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
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part).sorted((t1, t2) -> t1.getTransferDateTime()
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
        Stream<Transfer> matches = TransferDAO.getTransfersByCustomer(cust).sorted((t1, t2) -> t2.getTransferDateTime()
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
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod).sorted((t1, t2) -> t2.getTransferDateTime()
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
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part).sorted((t1, t2) -> t2.getTransferDateTime()
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
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod).sorted((t1, t2) -> t1.getTransferDateTime()
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
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part).sorted((t1, t2) -> t1.getTransferDateTime()
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
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod).sorted((t1, t2) -> t1.getTransferDateTime()
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
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part).sorted((t1, t2) -> t1.getTransferDateTime()
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
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod).sorted((t1, t2) -> t1.getTransferDateTime()
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
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part).sorted((t1, t2) -> t1.getTransferDateTime()
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

    public void updateProductNotes(Product product, String notes) {
        Product newProd = new Product(product.getDBName(), product.getCreationDateTime(), product.getCustomer(),
                product.getDefaultPart(), product.getProductName(), notes, product.getId());
        // Update product of parts
        for (Part part : PartDAO.getPartsByProduct(product).collect(Collectors.toList())) {
            PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity(), newProd,
                    part.getNextPart(), part.getPartNotes(), part.getId()));
        }
        // Update nextPart of parts
        for (Part part : PartDAO.getPartsByProduct(product).collect(Collectors.toList())) {
            if (part.getNextPart() != null) {
                PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity(),
                        part.getProduct(), PartDAO.getPart(part.getNextPart().getId()).get(), part.getPartNotes(),
                        part.getId()));
            }
        }
        // Update default part
        if (product.getDefaultPart() != null) {
            Part newDefault = PartDAO.getPart(product.getDefaultPart().getId()).get();
            newProd.setDefaultPart(newDefault);
        }
        // Update product
        ProductDAO.updateProduct(newProd);
    }

    public void updateDefaultPart(Part newDefault) {
        if (newDefault.getProduct().getDefaultPart().equals(newDefault)) {
            return;
        }
        // Update next part of the part that is pointing to the new default part
        Optional<Part> prev = PartDAO.getPartsByProduct(newDefault.getProduct())
                .filter(p -> p.getNextPart() != null && p.getNextPart().equals(newDefault)).findFirst();
        prev.ifPresent(p -> {
            p.setNextPart(newDefault.getNextPart());
            PartDAO.updatePart(p);
        });
        // Update next part of the new default part to be the old default part
        Part oldDefault = newDefault.getProduct().getDefaultPart();
        if (oldDefault != null) {
            newDefault.setNextPart(oldDefault);
            PartDAO.updatePart(newDefault);
        }
        // Update default part of the product
        newDefault.getProduct().setDefaultPart(newDefault);
        ProductDAO.updateProduct(newDefault.getProduct());
    }

    public void updatePartQuantity(Part part, int quantity) {
        PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(), quantity, part.getProduct(),
                part.getNextPart(), part.getPartNotes(), part.getId()));
    }

    public void updatePartNotes(Part part, String notes) {
        PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity(),
                part.getProduct(), part.getNextPart(), notes, part.getId()));
        Optional<Part> newPart = PartDAO.getPart(part.getId());
        if (!newPart.isPresent()) {
            throw new RuntimeException("Part not found after update");
        }
        // Check if product's default part is this part
        if (part.getProduct().getDefaultPart() != null && part.getProduct().getDefaultPart().equals(part)) {
            part.getProduct().setDefaultPart(newPart.get());
            ProductDAO.updateProduct(part.getProduct());
        }
        List<Part> list = PartDAO.getPartsByProduct(part.getProduct())
                .filter(p -> p.getNextPart() != null && p.getNextPart().equals(part)).collect(Collectors.toList());
        for (Part p : list) {
            p.setNextPart(newPart.get());
            PartDAO.updatePart(p);
        }
    }

}
