package com.ils.models;

import java.time.LocalDateTime;

public class Customer {
    private final String customerName;
    private final LocalDateTime creationDateTime;
    private final Integer customerId;

    public Customer(String customerName, LocalDateTime creationDateTime, int id) {
        this.customerName = customerName;
        this.creationDateTime = creationDateTime;
        this.customerId = id;
    }

    public int getId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Customer) {
            Customer other = (Customer) obj;
            return this.customerName.equals(other.customerName) && this.creationDateTime.equals(other.creationDateTime) && this.customerId.equals(other.customerId);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Customer [" + customerName + "]";
    }
}
