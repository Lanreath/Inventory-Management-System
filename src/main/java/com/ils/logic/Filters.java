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
    private ObjectProperty<Predicate<Customer>> customerNameFilter;
    private ObjectProperty<Predicate<Product>> dbNameFilter;
    private ObjectProperty<Predicate<Product>> productCustomerFilter;
    private ObjectProperty<Predicate<Transfer>> transferFromFilter;
    private ObjectProperty<Predicate<Transfer>> transferToFilter;
    private ObjectProperty<Predicate<Transfer>> transferActionFilter;
    private ObjectProperty<Predicate<Transfer>> transferCustomerFilter;
    private ObjectProperty<Predicate<Transfer>> transferProductFilter;
    private ObjectProperty<Predicate<Transfer>> transferPartFilter;

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

    public ObjectProperty<Predicate<Customer>> getCustomerNameFilter() {
        return customerNameFilter;
    }

    public ObjectProperty<Predicate<Product>> getDBNameFilter() {
        return dbNameFilter;
    }

    public ObjectProperty<Predicate<Product>> getProductCustomerFilter() {
        return productCustomerFilter;
    }

    public ObjectProperty<Predicate<Transfer>> getTransferFromFilter() {
        return transferFromFilter;
    }

    public ObjectProperty<Predicate<Transfer>> getTransferToFilter() {
        return transferToFilter;
    }

    public ObjectProperty<Predicate<Transfer>> getTransferActionFilter() {
        return transferActionFilter;
    }

    public ObjectProperty<Predicate<Transfer>> getTransferCustomerFilter() {
        return transferCustomerFilter;
    }

    public ObjectProperty<Predicate<Transfer>> getTransferProductFilter() {
        return transferProductFilter;
    }

    public ObjectProperty<Predicate<Transfer>> getTransferPartFilter() {
        return transferPartFilter;
    }

    public void clearCustomerNameFilter() {
        customerNameFilter.set(c -> true);
    }

    public void clearDBNameFilter() {
        dbNameFilter.set(p -> true);
    }

    public void clearProductCustomerFilter() {
        productCustomerFilter.set(p -> true);
    }

    public void clearTransferFromFilter() {
        transferFromFilter.set(t -> true);
    }

    public void clearTransferToFilter() {
        transferToFilter.set(t -> true);
    }

    public void clearTransferActionFilter() {
        transferActionFilter.set(t -> true);
    }

    public void clearTransferCustomerFilter() {
        transferCustomerFilter.set(t -> true);
    }

    public void clearTransferProductFilter() {
        transferProductFilter.set(t -> true);
    }

    public void clearTransferPartFilter() {
        transferPartFilter.set(t -> true);
    }

    public void filterCustomerByName(String name) {
        customerNameFilter.set(customer -> customer.getCustomerName().toLowerCase().contains(name.toLowerCase()));
    }

    public void filterProductByName(String name) {
        dbNameFilter.set(product -> product.getDBName().toLowerCase().contains(name.toLowerCase()));
    }

    public void filterProductByCustomer(Customer c) {
        productCustomerFilter.set(product -> product.getCustomer().equals(c));
    }

    public void filterTransferByFromDate(LocalDate from) {
        transferFromFilter.set(transfer -> transfer.getTransferDateTime().toLocalDate().isEqual(from) || transfer.getTransferDateTime().toLocalDate().isAfter(from));
    }

    public void filterTransferByToDate(LocalDate to) {
        transferToFilter.set(transfer -> transfer.getTransferDateTime().toLocalDate().isEqual(to) || transfer.getTransferDateTime().toLocalDate().isBefore(to));
    }

    public void filterTransferByAction(Transfer.Action action) {
        transferActionFilter.set(transfer -> transfer.getTransferType().equals(action));
    }

    public void filterTransferByCustomer(Customer c) {
        transferCustomerFilter.set(transfer -> transfer.getPart().getProduct().getCustomer().equals(c));
    }

    public void filterTransferByProduct(Product p) {
        transferProductFilter.set(transfer -> transfer.getPart().getProduct().equals(p));
    }

    public void filterTransferByPart(Part p) {
        transferPartFilter.set(transfer -> transfer.getPart().equals(p));
    }
}
