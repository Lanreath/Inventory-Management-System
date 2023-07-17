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

    public void addCustomer(String name) {
        CustomerDAO.insertCustomer(name);
    }

    public void addProduct(String name, Customer customer) {
        ProductDAO.insertProduct(name, customer);
        Optional<Product> prod = ProductDAO.getProductByDBName(name);
        PartDAO.insertPart("Default", 0, prod.get());
        Optional<Part> part = PartDAO.getPartByNameAndProduct("Default", prod.get());
        Product updatedProd = new Product(prod.get().getDBName(), prod.get().getCreationDateTime(),
                prod.get().getCustomer(), part.get(), prod.get().getId());
        ProductDAO.updateProduct(updatedProd);
        part.get().getProduct().setDefaultPart(part.get());
    }

    public void addPart(String name, int quantity, Product product) {
        int id = PartDAO.insertPart(name, quantity, product);
        Optional<Part> newPart = PartDAO.getPart(id);
        if (!newPart.isPresent()) {
            throw new RuntimeException("Part not found after insertion");
        }
        // Update next part of the last part of the product
        Part curr = product.getDefaultPart();
        while (curr.getNextPart() != null) {
            curr = curr.getNextPart();
        }
        curr.setNextPart(newPart.get());
        PartDAO.updatePart(curr);
    }

    public void addTransfer(Part part, int quantity, Transfer.Action action) {
        TransferDAO.insertTransfer(part, quantity, action);
        switch (action) {
            case DAILY:
            case DESTRUCT:
            case PROJECT:
            case REJECT_DAILY:
            case REJECT_PROJECT:
            case REJECT_RENEWAL:
            case RENEWAL:
            case SAMPLE:
                PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(),
                        part.getPartQuantity() - quantity, part.getProduct(), part.getId()));
                break;
            case RECEIVED:
                PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(),
                        part.getPartQuantity() + quantity, part.getProduct(), part.getId()));
                break;
        }
        // Check for default part change
        if (part.getProduct().getDefaultPart().equals(part)) {
            Optional<Part> newDefault = PartDAO.getPart(part.getId());
            if (!newDefault.isPresent()) {
                throw new RuntimeException("Part not found after insertion");
            }
            ProductDAO.updateProduct(new Product(part.getProduct().getDBName(), part.getProduct().getCreationDateTime(),
                    part.getProduct().getCustomer(), newDefault.get(), part.getProduct().getId()));
        }
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

    public void deleteCustomer(Customer customer) {
        List<Product> list = ProductDAO.getProducts().stream().filter(p -> p.getCustomer().equals(customer))
                .collect(Collectors.toList());
        for (Product product : list) {
            deleteProduct(product);
        }
        ;
        CustomerDAO.deleteCustomer(customer.getId());
    }

    public void deleteProduct(Product product) {
        List<Part> list = PartDAO.getPartsByProduct(product).collect(Collectors.toList());
        for (Part part : list) {
            deletePart(part);
        }
        ;
        ProductDAO.deleteProduct(product.getId());
    }

    public void deletePart(Part part) {
        // Delete all transfers associated with the part
        List<Transfer> list = TransferDAO.getTransfersByPart(part).collect(Collectors.toList());
        for (Transfer transfer : list) {
            deleteTransfer(transfer);
        }
        ;
        Part curr = part.getProduct().getDefaultPart();
        // Check if the part to be deleted is the default part
        if (curr.equals(part)) {
            if (curr.getNextPart() != null) {
                List<Part> affectedParts = PartDAO.getPartsByProduct(curr.getProduct()).collect(Collectors.toList());
                // Update the next part of the part to be deleted to be the new default part
                curr.getProduct().setDefaultPart(curr.getNextPart());
                ProductDAO.updateProduct(curr.getProduct());
                Optional<Product> updated = ProductDAO.getProduct(curr.getProduct().getId());
                if (!updated.isPresent()) {
                    throw new RuntimeException("Product not found after default part update");
                }
                // Update the current part to be the new default part
                affectedParts.forEach(p -> PartDAO.updatePart(new Part(p.getPartName(), p.getCreationDateTime(),
                        p.getPartQuantity(), updated.get(), p.getNextPart(), p.getPartNotes(), p.getId())));
            }
        } else {
            // Find the previous part of the part to be deleted
            while (curr.getNextPart() != null && !curr.getNextPart().equals(part)) {
                curr = curr.getNextPart();
            }
            curr.setNextPart(part.getNextPart());
            PartDAO.updatePart(curr);
        }
        PartDAO.deletePart(part.getId());
    }

    public void deleteTransfer(Transfer transfer) {
        TransferDAO.deleteTransfer(transfer.getId());
    }

}
