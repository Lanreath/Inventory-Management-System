package com.ils.models;

import java.time.LocalDateTime;

public class Product {
    private final String productName;
    private final LocalDateTime creationDateTime;
    private final Customer customer;
    private final Integer productId;
    private Part defaultPart;

    public Product(String productName, LocalDateTime creationDateTime, Customer customer, Part defaultPart, int id) {
        this.productName = productName;
        this.creationDateTime = creationDateTime;
        this.customer = customer;
        this.defaultPart = defaultPart;
        this.productId = id;
    }

    public Product(String productName, LocalDateTime creationDateTime, Customer customer, int id) {
        this.productName = productName;
        this.creationDateTime = creationDateTime;
        this.customer = customer;
        this.defaultPart = null;
        this.productId = id;
    }

    public int getId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Part getDefaultPart() {
        return defaultPart;
    }

    @Override
    public String toString() {
        return "Product [" + productName + ", " + creationDateTime + ", " + customer + "]";
    }
}
