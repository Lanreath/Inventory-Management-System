package com.ils.models;

import java.time.LocalDateTime;
import java.util.Optional;

public class Product {
    private final String productName;
    private final LocalDateTime creationDateTime;
    private final Customer customer;
    private final Integer productId;
    private final Optional<Part> defaultPart;

    public Product(String productName, LocalDateTime creationDateTime, Customer customer, Optional<Part> defaultPart, int id) {
        this.productName = productName;
        this.creationDateTime = creationDateTime;
        this.customer = customer;
        this.defaultPart = defaultPart;
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
        return defaultPart.orElse(null);
    }

    @Override
    public String toString() {
        return "Product [" + productName + ", " + creationDateTime + ", " + customer + "]";
    }
}
