package com.ils.models;

import java.time.LocalDateTime;

public class Customer {
    private final String customerName;
    private final LocalDateTime creationDateTime;
    private final Integer customerId;

    /**
     * Create a new Customer object.
     * @param customerName
     * @param creationDateTime
     * @param id
     */
    public Customer(String customerName, LocalDateTime creationDateTime, int id) {
        this.customerName = customerName;
        this.creationDateTime = creationDateTime;
        this.customerId = id;
    }

    /**
     * Get the customer id.
     * @return Integer
     */
    public int getId() {
        return customerId;
    }

    /**
     * Get the customer name.
     * @return String
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Get the customer creation date and time.
     * @return LocalDateTime
     */
    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    /**
     * Check if the customer is equal to another customer.
     * @param obj
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Customer) {
            Customer other = (Customer) obj;
            return this.customerName.equals(other.customerName) && this.creationDateTime.equals(other.creationDateTime) && this.customerId.equals(other.customerId);
        }
        return false;
    }

    /**
     * Get the string representation of the customer.
     * @return String
     */
    @Override
    public String toString() {
        return "Customer [" + customerName + "]";
    }
}
