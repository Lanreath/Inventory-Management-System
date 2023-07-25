package com.ils.logic.management;

import com.ils.logic.Filters;
import com.ils.logic.Logic;
import com.ils.logic.DAO.PartDAO;
import com.ils.logic.DAO.ProductDAO;
import com.ils.logic.DAO.TransferDAO;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.collections.transformation.FilteredList;

public class PartManagement {
    private Filters filters;
    private FilteredList<Part> partFilteredList;

    public PartManagement(Filters filters) {
        this.partFilteredList = PartDAO.getParts();
        this.filters = filters;
    }

    public FilteredList<Part> getParts() {
        return this.partFilteredList;
    }

    public Stream<Part> getProductParts(Product product) {
        return this.partFilteredList.stream()
                .filter(part -> part.getProduct().equals(product));
    }

    public Integer getProductQuantity(Product product) {
        Integer sum = this.partFilteredList.stream()
                .filter(part -> part.getProduct().equals(product))
                .mapToInt(part -> part.getPartQuantity())
                .sum();
        return sum;
    }

    public void addPart(String name, int quantity, Product product) {
        int id = PartDAO.insertPart(name, quantity, product);
        Optional<Part> newPart = PartDAO.getPart(id);
        if (!newPart.isPresent()) {
            throw new RuntimeException("Part not found after insertion");
        }
        // Update next part of the last part of the product
        Part curr = product.getDefaultPart();
        if (curr == null) {
            Logic.getProductManagement().updateDefaultPart(newPart.get());
            return;
        }
        while (curr.getNextPart() != null) {
            curr = curr.getNextPart();
        }
        curr.setNextPart(newPart.get());
        PartDAO.updatePart(curr);
    }

    public void updatePartName(Part part, String name) {
        PartDAO.updatePart(new Part(name, part.getCreationDateTime(), part.getPartQuantity(), part.getProduct(),
                part.getNextPart(), part.getPartNotes(), part.getId()));
        Optional<Part> newPart = PartDAO.getPart(part.getId());
        if (!newPart.isPresent()) {
            throw new RuntimeException("Part not found after update");
        }
        // Check for parts that are pointing to this part
        PartDAO.getPartsByProduct(part.getProduct())
                .filter(p -> p.getNextPart() != null && p.getNextPart().equals(part))
                .forEach(p -> {
                    p.setNextPart(newPart.get());
                    PartDAO.updatePart(p);
                });
        // Check if product's default part is this part
        if (part.getProduct().getDefaultPart() != null && part.getProduct().getDefaultPart().getId() == part.getId()) {
            part.getProduct().setDefaultPart(newPart.get());
            ProductDAO.updateProduct(part.getProduct());
        }
    }

    public void deletePart(Part part) {
        // Delete all transfers associated with the part
        List<Transfer> list = TransferDAO.getTransfersByPart(part).collect(Collectors.toList());
        for (Transfer transfer : list) {
            Logic.getTransferManagement().deleteTransfer(transfer);
        }
        ;
        Part curr = part.getProduct().getDefaultPart();
        // Check if the part to be deleted is the default part
        if (curr.equals(part)) {
            if (curr.getNextPart() != null) {
                List<Part> affectedParts = PartDAO.getPartsByProduct(curr.getProduct()).collect(Collectors.toList());
                // Update the next part of the part to be deleted to be the new default part
                Logic.getProductManagement().updateDefaultPart(curr.getNextPart());
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

    public void selectPart(Part part) {
        if (part == null) {
            filters.clearTransferPartFilter();
            return;
        }
        filters.filterTransferByPart(part);
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
            Logic.getProductManagement().updateDefaultPart(newPart.get());
        }
        List<Part> list = PartDAO.getPartsByProduct(part.getProduct())
                .filter(p -> p.getNextPart() != null && p.getNextPart().equals(part))
                .collect(Collectors.toList());
        for (Part p : list) {
            p.setNextPart(newPart.get());
            PartDAO.updatePart(p);
        }
    }
}
