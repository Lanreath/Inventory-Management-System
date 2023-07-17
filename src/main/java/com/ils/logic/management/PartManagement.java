package com.ils.logic.management;

import com.ils.logic.Filters;
import com.ils.logic.DAO.PartDAO;
import com.ils.logic.DAO.ProductDAO;
import com.ils.models.Part;
import com.ils.models.Product;

import java.util.Optional;
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
        if (part.getProduct().getDefaultPart() != null && part.getProduct().getDefaultPart().equals(part)) {
            part.getProduct().setDefaultPart(newPart.get());
            ProductDAO.updateProduct(part.getProduct());
        }
    }

    public void selectPart(Part part) {
        if (part == null) {
            filters.clearTransferPartFilter();
            return;
        }
        filters.filterTransferByPart(part);
    }
}
