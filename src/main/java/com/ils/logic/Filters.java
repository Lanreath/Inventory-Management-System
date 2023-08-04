package com.ils.logic;

import java.time.LocalDate;
import java.util.function.Predicate;

import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Filters {
    // Customer filters
    private ObjectProperty<Predicate<Customer>> customerNameFilter;
    private ObjectProperty<Predicate<Product>> dbNameFilter;
    private ObjectProperty<Predicate<Product>> productCustomerFilter;
    // Transfer filters
    private ObjectProperty<Predicate<Transfer>> transferFromFilter;
    private ObjectProperty<Predicate<Transfer>> transferToFilter;
    private ObjectProperty<Predicate<Transfer>> transferActionFilter;
    private ObjectProperty<Predicate<Transfer>> transferCustomerFilter;
    private ObjectProperty<Predicate<Transfer>> transferProductFilter;
    private ObjectProperty<Predicate<Transfer>> transferPartFilter;

    /**
     * Create a new Filters object and initialize the filters.
     */
    public Filters() {
        this.customerNameFilter = new SimpleObjectProperty<>(c -> true);
        this.dbNameFilter = new SimpleObjectProperty<>(p -> true);
        this.productCustomerFilter = new SimpleObjectProperty<>(p -> true);
        this.transferFromFilter = new SimpleObjectProperty<>(p -> true);
        this.transferToFilter = new SimpleObjectProperty<>(p -> true);
        this.transferActionFilter = new SimpleObjectProperty<>(t -> true);
        this.transferCustomerFilter = new SimpleObjectProperty<>(t -> true);
        this.transferProductFilter = new SimpleObjectProperty<>(t -> true);
        this.transferPartFilter = new SimpleObjectProperty<>(t -> true);
    }

    /**
     * Get the customer name filter.
     * @return ObjectProperty<Predicate<Customer>>
     */
    public ObjectProperty<Predicate<Customer>> getCustomerNameFilter() {
        return customerNameFilter;
    }

    /**
     * Get the product database name filter.
     * @return ObjectProperty<Predicate<Product>>
     */
    public ObjectProperty<Predicate<Product>> getDBNameFilter() {
        return dbNameFilter;
    }

    /**
     * Get the product customer filter.
     * @return ObjectProperty<Predicate<Product>>
     */
    public ObjectProperty<Predicate<Product>> getProductCustomerFilter() {
        return productCustomerFilter;
    }

    /**
     * Get the transfer from date filter.
     * @return ObjectProperty<Predicate<Transfer>>
     */
    public ObjectProperty<Predicate<Transfer>> getTransferFromFilter() {
        return transferFromFilter;
    }

    /**
     * Get the transfer to date filter. 
     * @return ObjectProperty<Predicate<Transfer>>
     */
    public ObjectProperty<Predicate<Transfer>> getTransferToFilter() {
        return transferToFilter;
    }

    /**
     * Get the transfer action filter.
     * @return ObjectProperty<Predicate<Transfer>>
     */
    public ObjectProperty<Predicate<Transfer>> getTransferActionFilter() {
        return transferActionFilter;
    }

    /**
     * Get the transfer customer filter.
     * @return ObjectProperty<Predicate<Transfer>>
     */
    public ObjectProperty<Predicate<Transfer>> getTransferCustomerFilter() {
        return transferCustomerFilter;
    }

    /**
     * Get the transfer product filter.
     * @return ObjectProperty<Predicate<Transfer>>
     */
    public ObjectProperty<Predicate<Transfer>> getTransferProductFilter() {
        return transferProductFilter;
    }

    /**
     * Get the transfer part filter.
     * @return ObjectProperty<Predicate<Transfer>>
     */
    public ObjectProperty<Predicate<Transfer>> getTransferPartFilter() {
        return transferPartFilter;
    }

    /**
     * Clears the customer name filter.
     */
    public void clearCustomerNameFilter() {
        customerNameFilter.set(c -> true);
    }

    /**
     * Clears the product database name filter.
     */
    public void clearDBNameFilter() {
        dbNameFilter.set(p -> true);
    }

    /**
     * Clears the product customer filter.
     */
    public void clearProductCustomerFilter() {
        productCustomerFilter.set(p -> true);
    }

    /**
     * Clears the transfer from date filter.
     */
    public void clearTransferFromFilter() {
        transferFromFilter.set(t -> true);
    }

    /**
     * Clears the transfer to date filter.
     */
    public void clearTransferToFilter() {
        transferToFilter.set(t -> true);
    }

    /**
     * Clears the transfer action filter.
     */
    public void clearTransferActionFilter() {
        transferActionFilter.set(t -> true);
    }

    /**
     * Clears the transfer customer filter.
     */
    public void clearTransferCustomerFilter() {
        transferCustomerFilter.set(t -> true);
    }

    /**
     * Clears the transfer product filter.
     */
    public void clearTransferProductFilter() {
        transferProductFilter.set(t -> true);
    }

    /**
     * Clears the transfer part filter.
     */
    public void clearTransferPartFilter() {
        transferPartFilter.set(t -> true);
    }

    /**
     * Set the customer name filter given a substring.
     * @param name
     */
    public void filterCustomerByName(String name) {
        customerNameFilter.set(customer -> customer.getCustomerName().toLowerCase().contains(name.toLowerCase()));
    }

    /**
     * Set the product database name filter given a substring.
     * @param name
     */
    public void filterProductByName(String name) {
        dbNameFilter.set(product -> product.getDBName().toLowerCase().contains(name.toLowerCase()));
    }

    /**
     * Set the product customer filter given a customer.
     * @param c
     */
    public void filterProductByCustomer(Customer c) {
        productCustomerFilter.set(product -> product.getCustomer().equals(c));
    }

    /**
     * Set the transfer from date filter given a date.
     * @param from
     */
    public void filterTransferByFromDate(LocalDate from) {
        transferFromFilter.set(transfer -> transfer.getTransferDateTime().toLocalDate().isEqual(from) || transfer.getTransferDateTime().toLocalDate().isAfter(from));
    }

    /**
     * Set the transfer to date filter given a date.
     * @param to
     */
    public void filterTransferByToDate(LocalDate to) {
        transferToFilter.set(transfer -> transfer.getTransferDateTime().toLocalDate().isEqual(to) || transfer.getTransferDateTime().toLocalDate().isBefore(to));
    }

    /**
     * Set the transfer action filter given an action.
     * @param action
     */
    public void filterTransferByAction(Transfer.Action action) {
        transferActionFilter.set(transfer -> transfer.getTransferType().equals(action));
    }

    /**
     * Set the transfer customer filter given a customer.
     * @param c
     */
    public void filterTransferByCustomer(Customer c) {
        transferCustomerFilter.set(transfer -> transfer.getPart().getProduct().getCustomer().equals(c));
    }

    /**
     * Set the transfer product filter given a product.
     * @param p
     */
    public void filterTransferByProduct(Product p) {
        transferProductFilter.set(transfer -> transfer.getPart().getProduct().equals(p));
    }

    /**
     * Set the transfer part filter given a part.
     * @param p
     */
    public void filterTransferByPart(Part p) {
        transferPartFilter.set(transfer -> transfer.getPart().equals(p));
    }
}
