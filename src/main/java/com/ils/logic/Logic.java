package com.ils.logic;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.time.LocalDate;
import java.util.Optional;
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
                        () -> filters.getDBNameFilter().get().and(filters.getProductCustomerFilter().get()),
                        filters.getDBNameFilter(), filters.getProductCustomerFilter()));
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

    public ObjectProperty<Predicate<Product>> getDBNameFilter() {
        return filters.getDBNameFilter();
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
        Optional<Product> prod = ProductDAO.getProductByName(name);
        PartDAO.insertPart("Default", 0, prod.get());
        Optional<Part> part = PartDAO.getPartByNameAndProduct("Default", prod.get());
        ProductDAO.updateProduct(new Product(prod.get().getDBName(), prod.get().getCreationDateTime(), prod.get().getCustomer(), part.get(), prod.get().getId()));
    }

    public void addPart(String name, int quantity, Product product) {
        PartDAO.insertPart(name, quantity, product);
    }

    public void addTransfer(Part part, int quantity, Transfer.Action action) {
        TransferDAO.insertTransfer(part, quantity, action);
        switch (action) {
            case WITHDRAW:
            case REJECT:
            case SAMPLE:
                PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity() - quantity, part.getProduct(), part.getId()));
                break;
            case INCOMING:
                PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity() + quantity, part.getProduct(), part.getId()));
                break;
        }
    }

    public void updateCustomer(Customer customer, String name) {
        CustomerDAO.updateCustomer(new Customer(name, customer.getCreationDateTime(), customer.getId()));
        Customer cust = CustomerDAO.getCustomer(customer.getId()).get();
        ProductDAO.getProducts().stream().filter(product -> product.getCustomer().equals(cust))
                .forEach(product -> ProductDAO.updateProduct(new Product(product.getDBName(), product.getCreationDateTime(), cust, product.getId())));
    }

    // public void updateProduct(Product product, Part defaultPart) {
    //     ProductDAO.updateProduct(new Product(product.getDBName(), product.getCreationDateTime(), product.getCustomer(), product.getId()));
    // }

    public void updatePartName(Part part, String name) {
        PartDAO.updatePart(new Part(name, part.getCreationDateTime(), part.getPartQuantity(), part.getProduct(), part.getId()));
    }

    public void updatePartQuantity(Part part, int quantity) {
        PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(), quantity, part.getProduct(), part.getId()));
    }

    public void deleteCustomer(Customer customer) {
        productSortedList.forEach(product -> {
            partSortedList.forEach(part -> {
                transferSortedList.forEach(transfer -> {
                    if (transfer.getPart().equals(part)) {
                        deleteTransfer(transfer);
                    }
                });
                if (part.getProduct().equals(product)) {
                    deletePart(part);
                }
            });
            if (product.getCustomer().equals(customer)) {
                deleteProduct(product);
            }
        });
        CustomerDAO.deleteCustomer(customer.getId());
    }

    public void deleteProduct(Product product) {
        partSortedList.forEach(part -> {
            transferSortedList.forEach(transfer -> {
                if (transfer.getPart().equals(part)) {
                    deleteTransfer(transfer);
                }
            });
            if (part.getProduct().equals(product)) {
                deletePart(part);
            }
        });
        ProductDAO.deleteProduct(product.getId());
    }

    public void deletePart(Part part) {
        transferSortedList.forEach(transfer -> {
            if (transfer.getPart().equals(part)) {
                deleteTransfer(transfer);
            }
        });
        PartDAO.deletePart(part.getId());
    }

    public void deleteTransfer(Transfer transfer) {
        TransferDAO.deleteTransfer(transfer.getId());
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
        // Check if the filters are already set to the correct values
        if (!getProducts().contains(product)) {
            // If not, clear the filters and assert that the correct values are now present
            filters.clearProductCustomerFilter();
            filters.clearDBNameFilter();
            assert getProducts().contains(product);
        }
        // Set the filters to the correct values
        selectedProduct.set(product);
        // Do the same for the part
        if (!getCustomers().contains(customer)) {
            filters.clearCustomerNameFilter();
            assert getCustomers().contains(customer);
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
            filters.clearDBNameFilter();
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
