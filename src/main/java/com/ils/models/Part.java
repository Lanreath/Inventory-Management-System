package com.ils.models;

import java.time.LocalDateTime;

public class Part {
    private final String partName;
    private final LocalDateTime creationDateTime;
    private final Integer partQuantity;
    private final Product product;
    private final Integer partId;

    public Part(String partName, LocalDateTime creationDateTime, int quantity, Product product, int id) {
        this.partName = partName;
        this.creationDateTime = creationDateTime;
        this.partQuantity = quantity;
        this.product = product;
        this.partId = id;
    }

    public int getId() {
        return partId;
    }

    public String getPartName() {
        return partName;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public int getPartQuantity() {
        return partQuantity;
    }

    public Product getProduct() {
        return product;
    }

    @Override
    public String toString() {
        return "Part [" + partName + ", " + creationDateTime + ", " + partQuantity + ", " + product + "]";
    }
}
