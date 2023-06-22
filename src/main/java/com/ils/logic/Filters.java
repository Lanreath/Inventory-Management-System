package com.ils.logic;

import java.time.LocalDate;
import java.util.function.Predicate;

import com.ils.models.Customer;
import com.ils.models.Product;
import com.ils.models.Transfer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Filters {
    private ObjectProperty<Predicate<Customer>> customerNameFilter;
    private ObjectProperty<Predicate<Product>> productNameFilter;
    private ObjectProperty<Predicate<Product>> productCustomerFilter;
    private ObjectProperty<Predicate<Transfer>> transferDateFilter;
    private ObjectProperty<Predicate<Transfer>> transferMonthFilter;
    private ObjectProperty<Predicate<Transfer>> transferActionFilter;
    private ObjectProperty<Predicate<Transfer>> transferCustomerFilter;
    private ObjectProperty<Predicate<Transfer>> transferProductFilter;

    public Filters() {
        this.customerNameFilter = new SimpleObjectProperty<>(c -> true);
        this.productNameFilter = new SimpleObjectProperty<>(p -> true);
        this.productCustomerFilter = new SimpleObjectProperty<>(p -> true);
        this.transferDateFilter = new SimpleObjectProperty<>(t -> true);
        this.transferActionFilter = new SimpleObjectProperty<>(t -> true);
        this.transferCustomerFilter = new SimpleObjectProperty<>(t -> true);
        this.transferProductFilter = new SimpleObjectProperty<>(t -> true);
    }

    protected ObjectProperty<Predicate<Customer>> getCustomerNameFilter() {
        return customerNameFilter;
    }

    protected ObjectProperty<Predicate<Product>> getProductNameFilter() {
        return productNameFilter;
    }

    protected ObjectProperty<Predicate<Product>> getProductCustomerFilter() {
        return productCustomerFilter;
    }

    protected ObjectProperty<Predicate<Transfer>> getTransferDateFilter() {
        return transferDateFilter;
    }

    protected ObjectProperty<Predicate<Transfer>> getTransferActionFilter() {
        return transferActionFilter;
    }

    protected ObjectProperty<Predicate<Transfer>> getTransferCustomerFilter() {
        return transferCustomerFilter;
    }

    protected ObjectProperty<Predicate<Transfer>> getTransferProductFilter() {
        return transferProductFilter;
    }

    protected void clearCustomerNameFilter() {
        customerNameFilter.set(c -> true);
    }

    protected void clearProductNameFilter() {
        productNameFilter.set(p -> true);
    }

    protected void clearProductCustomerFilter() {
        productCustomerFilter.set(p -> true);
    }

    protected void clearTransferDateFilter() {
        transferDateFilter.set(t -> true);
    }

    protected void clearTransferActionFilter() {
        transferActionFilter.set(t -> true);
    }

    protected void clearTransferCustomerFilter() {
        transferCustomerFilter.set(t -> true);
    }

    protected void clearTransferProductFilter() {
        transferProductFilter.set(t -> true);
    }

    protected void filterCustomerByName(String name) {
        customerNameFilter.set(customer -> customer.getCustomerName().toLowerCase().contains(name.toLowerCase()));
    }

    protected void filterProductByName(String name) {
        productNameFilter.set(product -> product.getProductName().toLowerCase().contains(name.toLowerCase()));
    }

    protected void filterProductByCustomer(Customer c) {
        productCustomerFilter.set(product -> product.getCustomer().equals(c));
    }

    protected void filterTransferByDate(LocalDate date) {
        transferDateFilter.set(transfer -> transfer.getTransferDateTime().toLocalDate().equals(date));
    }

    protected void filterTransferByAction(Transfer.Action action) {
        transferActionFilter.set(transfer -> transfer.getTransferType().equals(action));
    }

    protected void filterTransferByCustomer(Customer c) {
        transferCustomerFilter.set(transfer -> transfer.getProduct().getCustomer().equals(c));
    }

    protected void filterTransferByProduct(Product p) {
        transferProductFilter.set(transfer -> transfer.getProduct().equals(p));
    }
}
