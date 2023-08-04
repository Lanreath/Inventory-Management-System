package com.ils.models;

import java.time.LocalDateTime;

public class Product {
    private final String dbName;
    private final LocalDateTime creationDateTime;
    private final Customer customer;
    private final Integer productId;
    private Part defaultPart;
    private String productName;
    private String productNotes;

    /**
     * Create a new Product object.
     * @param dbName
     * @param creationDateTime
     * @param customer
     * @param defaultPart
     * @param id
     */
    public Product(String dbName, LocalDateTime creationDateTime, Customer customer, Part defaultPart, int id) {
        this.dbName = dbName;
        this.creationDateTime = creationDateTime;
        this.customer = customer;
        this.defaultPart = defaultPart;
        this.productId = id;
    }

    /**
     * Create a new Product object.
     * @param dbName
     * @param creationDateTime
     * @param customer
     * @param defaultPart
     * @param productName
     * @param productNotes
     * @param id
     */
    public Product(String dbName, LocalDateTime creationDateTime, Customer customer, Part defaultPart, String productName, String productNotes, int id) {
        this.dbName = dbName;
        this.creationDateTime = creationDateTime;
        this.customer = customer;
        this.defaultPart = defaultPart;
        this.productName = productName;
        this.productNotes = productNotes;
        this.productId = id;
    }

    /**
     * Create a new Product object.
     * @param dbName
     * @param creationDateTime
     * @param customer
     * @param id
     */
    public Product(String dbName, LocalDateTime creationDateTime, Customer customer, int id) {
        this.dbName = dbName;
        this.creationDateTime = creationDateTime;
        this.customer = customer;
        this.productId = id;
    }

    /**
     * Get the product id.
     * @return Integer
     */
    public int getId() {
        return productId;
    }

    /**
     * Get the product name.
     * @return String
     */
    public String getDBName() {
        return dbName;
    }

    /**
     * Get the product creation date and time.
     * @return LocalDateTime
     */
    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    /**
     * Get the product customer.
     * @return Customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Get the product name.
     * @return String
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Get the product default part.
     * @return Part
     */
    public Part getDefaultPart() {
        return defaultPart;
    }   

    /**
     * Get the product notes.
     * @return String
     */
    public String getProductNotes() {
        return productNotes;
    }
    
    /**
     * Set the default part of the product.
     * @param defaultPart
     */
    public void setDefaultPart(Part defaultPart) {
        this.defaultPart = defaultPart;
    }

    /**
     * Check if the product is equal to another product.
     * @param obj
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Product) {
            Product other = (Product) obj;
            return this.dbName.equals(other.dbName) && this.creationDateTime.equals(other.creationDateTime)
                    && this.customer.equals(other.customer) && this.productId.equals(other.productId);
        }
        return false;
    }

    /**
     * Get the string representation of the product.
     * @return String
     */
    @Override
    public String toString() {
        return "Product [" + dbName + ", " + creationDateTime + ", " + defaultPart.getPartName() + ", " + customer + "]";
    }
}
