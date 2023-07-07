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

    public Product(String dbName, LocalDateTime creationDateTime, Customer customer, Part defaultPart, int id) {
        this.dbName = dbName;
        this.creationDateTime = creationDateTime;
        this.customer = customer;
        this.defaultPart = defaultPart;
        this.productId = id;
    }

    public Product(String dbName, LocalDateTime creationDateTime, Customer customer, Part defaultPart, String productName, String productNotes, int id) {
        this.dbName = dbName;
        this.creationDateTime = creationDateTime;
        this.customer = customer;
        this.defaultPart = defaultPart;
        this.productName = productName;
        this.productNotes = productNotes;
        this.productId = id;
    }

    public Product(String dbName, LocalDateTime creationDateTime, Customer customer, int id) {
        this.dbName = dbName;
        this.creationDateTime = creationDateTime;
        this.customer = customer;
        this.productId = id;
    }

    public int getId() {
        return productId;
    }

    public String getDBName() {
        return dbName;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getProductName() {
        return productName;
    }

    public Part getDefaultPart() {
        return defaultPart;
    }   

    public String getProductNotes() {
        return productNotes;
    }
    
    public void setDefaultPart(Part defaultPart) {
        this.defaultPart = defaultPart;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Product) {
            Product other = (Product) obj;
            return this.dbName.equals(other.dbName) && this.creationDateTime.equals(other.creationDateTime)
                    && this.customer.equals(other.customer) && this.productId.equals(other.productId);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Product [" + dbName + ", " + creationDateTime + ", " + defaultPart.getPartName() + ", " + customer + "]";
    }
}
