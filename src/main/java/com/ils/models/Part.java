package com.ils.models;

import java.time.LocalDateTime;

public class Part {
    private final String partName;
    private final LocalDateTime creationDateTime;
    private final Integer partQuantity;
    private final Product product;
    private final Integer partId;
    private Part nextPart;
    private String partNotes;

    /**
     * Create a new Part object.
     * @param partName
     * @param creationDateTime
     * @param quantity
     * @param product
     * @param id
     */
    public Part(String partName, LocalDateTime creationDateTime, int quantity, Product product, int id) {
        this.partName = partName;
        this.creationDateTime = creationDateTime;
        this.partQuantity = quantity;
        this.product = product;
        this.partId = id;
    }

    /**
     * Create a new Part object.
     * @param partName
     * @param creationDateTime
     * @param quantity
     * @param product
     * @param nextPart
     * @param partNotes
     * @param id
     */
    public Part(String partName, LocalDateTime creationDateTime, int quantity, Product product, Part nextPart, String partNotes, int id) {
        this.partName = partName;
        this.creationDateTime = creationDateTime;
        this.partQuantity = quantity;
        this.product = product;
        this.nextPart = nextPart;
        this.partNotes = partNotes;
        this.partId = id;
    }

    /**
     * Get the part id.
     * @return Integer
     */
    public int getId() {
        return partId;
    }

    /**
     * Get the part name.
     * @return String
     */
    public String getPartName() {
        return partName;
    }

    /**
     * Get the part creation date and time.
     * @return LocalDateTime
     */
    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    /**
     * Get the part quantity.
     * @return Integer
     */
    public int getPartQuantity() {
        return partQuantity;
    }

    /**
     * Get the part product.
     * @return Product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Get the next part.
     * @return Part
     */
    public Part getNextPart() {
        return nextPart;
    }
    
    /**
     * Get the part notes.
     * @return
     */
    public String getPartNotes() {
        return partNotes;
    }

    /**
     * Set the next part.
     * @param nextPart
     */
    public void setNextPart(Part nextPart) {
        this.nextPart = nextPart;
    }

    /**
     * Check if the part is equal to another part.
     * @param obj
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Part) {
            Part other = (Part) obj;
            return this.partName.equals(other.partName) && this.creationDateTime.equals(other.creationDateTime)
                    && this.product.equals(other.product) && this.partId.equals(other.partId);
        }
        return false;
    }

    /**
     * Get the string representation of the part.
     * @return String
     */
    @Override
    public String toString() {
        return "Part [" + partName + ", " + creationDateTime + ", " + partQuantity + ", " + product + "]";
    }
}
