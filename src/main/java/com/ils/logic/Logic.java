package com.ils.logic;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.time.LocalDate;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

public class Logic {
    private FilteredList<Customer> customerFilteredList;
    private FilteredList<Product> productFilteredList;
    private FilteredList<Part> partFilteredList;
    private FilteredList<Transfer> transferFilteredList;
    private SortedList<Customer> customerSortedList;
    private SortedList<Product> productSortedList;
    private SortedList<Part> partSortedList;
    private SortedList<Transfer> transferSortedList;
    private ObjectProperty<Customer> selectedCustomer;
    private ObjectProperty<Product> selectedProduct;
    // private ObjectProperty<Part> selectedPart;
    private DataSync sync;
    private Filters filters;

    public Logic() {
        this.filters = new Filters();
        this.customerFilteredList = CustomerDAO.getCustomers();
        this.productFilteredList = ProductDAO.getProducts();
        this.partFilteredList = PartDAO.getParts();
        this.transferFilteredList = TransferDAO.getTransfers();
        this.customerSortedList = new SortedList<>(customerFilteredList);
        this.productSortedList = new SortedList<>(productFilteredList);
        this.partSortedList = new SortedList<>(partFilteredList);
        this.transferSortedList = new SortedList<>(transferFilteredList);
        this.selectedCustomer = new SimpleObjectProperty<>(null);
        this.selectedProduct = new SimpleObjectProperty<>(null);
        initFilters();
    }

    private void initFilters() {
        customerFilteredList.predicateProperty().bind(filters.getCustomerNameFilter());
        productFilteredList.predicateProperty()
                .bind(Bindings.createObjectBinding(
                        () -> filters.getProductNameFilter().get().and(filters.getProductCustomerFilter().get()),
                        filters.getProductNameFilter(), filters.getProductCustomerFilter()));
        transferFilteredList.predicateProperty().bind(Bindings.createObjectBinding(
                () -> filters.getTransferDateFilter().get().and(filters.getTransferActionFilter().get())
                        .and(filters.getTransferCustomerFilter().get()).and(filters.getTransferProductFilter().get())
                        .and(filters.getTransferPartFilter().get()),
                filters.getTransferDateFilter(), filters.getTransferActionFilter(), filters.getTransferCustomerFilter(),
                filters.getTransferProductFilter(), filters.getTransferPartFilter()));
    }

    public SortedList<Customer> getCustomers() {
        return this.customerSortedList;
    }

    public SortedList<Product> getProducts() {
        return this.productSortedList;
    }

    public SortedList<Part> getParts() {
        return this.partSortedList;
    }

    public SortedList<Transfer> getTransfers() {
        return this.transferSortedList;
    }

    public Integer getProductQuantity(Product product) {
        // Sum all the transfers of every part of the product
        return PartDAO.getParts().stream()
                .filter(part -> part.getProduct().equals(product))
                .mapToInt(part -> part.getPartQuantity())
                .sum();
    }

    public Stream<Part> getProductParts(Product product) {
        return PartDAO.getParts().stream()
                .filter(part -> part.getProduct().equals(product));
    }

    public ObjectProperty<Predicate<Product>> getProductCustomerFilter() {
        return filters.getProductCustomerFilter();
    }

    public ObjectProperty<Predicate<Product>> getProductNameFilter() {
        return filters.getProductNameFilter();
    }

    public ObjectProperty<Customer> getSelectedCustomer() {
        return selectedCustomer;
    }

    public ObjectProperty<Product> getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedCustomer(Customer customer) {
        selectedCustomer.set(customer);
    }

    public void setSelectedProduct(Product product) {
        selectedProduct.set(product);
    }

    public void addCustomer(String name) {
        CustomerDAO.insertCustomer(name);
    }

    public void addProduct(String name, Customer customer) {
        ProductDAO.insertProduct(name, customer);
    }

    public void addPart(String name, int quantity, Product product) {
        PartDAO.insertPart(name, quantity, product);
    }

    public void addTransfer(Part part, int quantity, Transfer.Action action) {
        TransferDAO.insertTransfer(part, quantity, action);
    }

    public void selectCustomer(Customer customer) {
        if (customer == null) {
            filters.clearProductCustomerFilter();
            filters.clearTransferCustomerFilter();
            return;
        }
        filters.filterProductByCustomer(customer);
        filters.filterTransferByCustomer(customer);
    }

    public void selectProduct(Product product) {
        if (product == null) {
            filters.clearTransferProductFilter();
            return;
        }
        filters.filterTransferByProduct(product);
    }

    public void selectPart(Part part) {
        if (part == null) {
            filters.clearTransferPartFilter();
            return;
        }
        filters.filterTransferByPart(part);
    }

    public void selectTransfer(Transfer transfer) {
        if (transfer == null) {
            return;
        }
        Part part = transfer.getPart();
        Product product = part.getProduct();
        Customer customer = product.getCustomer();
        if (!getProducts().contains(product)) {
            filters.clearProductCustomerFilter();
            filters.clearProductNameFilter();
        }
        selectedProduct.set(product);
        if (!getCustomers().contains(customer)) {
            filters.clearCustomerNameFilter();
        }
        selectedCustomer.set(customer);
    }

    public void setCustomerNameFilter(String name) {
        if (name == null) {
            filters.clearCustomerNameFilter();
            return;
        }
        filters.filterCustomerByName(name);
    }

    public void setProductNameFilter(String name) {
        if (name == null) {
            filters.clearProductNameFilter();
            return;
        }
        filters.filterProductByName(name);
    }

    public void setTransferDateFilter(LocalDate date) {
        if (date == null) {
            filters.clearTransferDateFilter();
            return;
        }
        filters.filterTransferByDate(date);
    }

    public void setTransferActionFilter(Transfer.Action type) {
        if (type == null) {
            filters.clearTransferActionFilter();
            return;
        }
        filters.filterTransferByAction(type);
    }

    public void syncData(String username, String password, LocalDate date) throws IllegalArgumentException {
        sync = new DataSync(username, password);
        if (this.customerFilteredList.isEmpty()) {
            Logger.getAnonymousLogger().info("Customers empty, syncing all data");
            sync.syncCustomers();
        }
        sync.syncTransfers(date);
    }
}
