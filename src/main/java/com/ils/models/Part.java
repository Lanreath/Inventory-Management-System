package com.ils.models;

import java.time.LocalDateTime;

public class Part {
    private final String partName;
    private final LocalDateTime creationDateTime;
    private final Integer partQuantity;
    private final Product product;
    private final Part nextPart;
    private final Integer partId;

    public Part(String partName, LocalDateTime creationDateTime, int quantity, Product product, int id) {
        this.partName = partName;
        this.creationDateTime = creationDateTime;
        this.partQuantity = quantity;
        this.product = product;
        this.nextPart = null;
        this.partId = id;
    }

    public Part(String partName, LocalDateTime creationDateTime, int quantity, Product product, Part nextPart, int id) {
        this.partName = partName;
        this.creationDateTime = creationDateTime;
        this.partQuantity = quantity;
        this.product = product;
        this.nextPart = nextPart;
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

    public Part getNextPart() {
        return nextPart;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Part) {
            Part other = (Part) obj;
            return this.partName.equals(other.partName) && this.creationDateTime.equals(other.creationDateTime)
                    && this.partQuantity.equals(other.partQuantity) && this.product.equals(other.product) && this.partId.equals(other.partId);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Part [" + partName + ", " + creationDateTime + ", " + partQuantity + ", " + product + "]";
    }
}
