package com.ils.logic;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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

    private DataSync sync;
    private Filters filters;

    public Logic() {
        this.filters = new Filters();
        initLists();
        this.selectedCustomer = new SimpleObjectProperty<>(null);
        this.selectedProduct = new SimpleObjectProperty<>(null);
        initFilters();
    }

    private void initLists() {
        this.customerFilteredList = CustomerDAO.getCustomers();
        this.productFilteredList = ProductDAO.getProducts();
        this.partFilteredList = PartDAO.getParts();
        this.transferFilteredList = TransferDAO.getTransfers();
        this.customerSortedList = new SortedList<>(customerFilteredList);
        this.productSortedList = new SortedList<>(productFilteredList);
        this.partSortedList = new SortedList<>(partFilteredList);
        this.transferSortedList = new SortedList<>(transferFilteredList);
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
        Integer sum = this.partFilteredList.stream()
                .filter(part -> part.getProduct().equals(product))
                .mapToInt(part -> part.getPartQuantity())
                .sum();
        return sum;
    }

    public Stream<Part> getProductParts(Product product) {
        return this.partFilteredList.stream()
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

    public Integer getMonthlyOpeningBalByCustomer(Customer cust, LocalDate from) {
        Stream<Transfer> matches = TransferDAO.getTransfersByCustomer(cust).sorted((t1, t2) -> t1.getTransferDateTime()
                .compareTo(t2.getTransferDateTime()));
        Optional<Transfer> earliest = matches.filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from) || t.getTransferDateTime().toLocalDate().isAfter(from)).findFirst();
        if (!earliest.isPresent()) {
            return -1;
        }
        Transfer t = earliest.get();
        return t.getPrevPartQuantity();
    }

    public Integer getMonthlyOpeningBalByProduct(Product prod, LocalDate from) {
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod).sorted((t1, t2) -> t1.getTransferDateTime()
                .compareTo(t2.getTransferDateTime()));
        Optional<Transfer> earliest = matches.filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from) || t.getTransferDateTime().toLocalDate().isAfter(from)).findFirst();
        if (!earliest.isPresent()) {
            return -1;
        }
        Transfer t = earliest.get();
        return t.getPrevPartQuantity();
    }

    public Integer getMonthlyOpeningBalByPart(Part part, LocalDate from) {
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part).sorted((t1, t2) -> t1.getTransferDateTime()
                .compareTo(t2.getTransferDateTime()));
        Optional<Transfer> earliest = matches.filter(t -> t.getTransferDateTime().toLocalDate().isEqual(from) || t.getTransferDateTime().toLocalDate().isAfter(from)).findFirst();
        if (!earliest.isPresent()) {
            return -1;
        }
        Transfer t = earliest.get();
        return t.getPrevPartQuantity();
    }

    public Integer getMonthlyClosingBalByCustomer(Customer cust, LocalDate to) {
        Stream<Transfer> matches = TransferDAO.getTransfersByCustomer(cust).sorted((t1, t2) -> t2.getTransferDateTime()
                .compareTo(t1.getTransferDateTime()));
        Optional<Transfer> latest = matches.filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to) || t.getTransferDateTime().toLocalDate().isBefore(to)).findFirst();
        if (!latest.isPresent()) {
            return -1;
        }
        Transfer t = latest.get();
        switch (t.getTransferType()) {
            case WITHDRAW:
            case REJECT:
            case SAMPLE:
                return t.getPrevPartQuantity() - t.getTransferQuantity();
            case RECEIVE:
                return t.getPrevPartQuantity() + t.getTransferQuantity();
            default:
                return t.getPrevPartQuantity();
        }
    }

    public Integer getMonthlyClosingBalByProduct(Product prod, LocalDate to) {
        Stream<Transfer> matches = TransferDAO.getTransfersByProduct(prod).sorted((t1, t2) -> t2.getTransferDateTime()
                .compareTo(t1.getTransferDateTime()));
        Optional<Transfer> latest = matches.filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to) || t.getTransferDateTime().toLocalDate().isBefore(to)).findFirst();
        if (!latest.isPresent()) {
            return -1;
        }
        Transfer t = latest.get();
        switch (t.getTransferType()) {
            case WITHDRAW:
            case REJECT:
            case SAMPLE:
                return t.getPrevPartQuantity() - t.getTransferQuantity();
            case RECEIVE:
                return t.getPrevPartQuantity() + t.getTransferQuantity();
            default:
                return t.getPrevPartQuantity();
        }
    }

    public Integer getMonthlyClosingBalByPart(Part part, LocalDate to) {
        Stream<Transfer> matches = TransferDAO.getTransfersByPart(part).sorted((t1, t2) -> t2.getTransferDateTime()
                .compareTo(t1.getTransferDateTime()));
        Optional<Transfer> latest = matches.filter(t -> t.getTransferDateTime().toLocalDate().isEqual(to) || t.getTransferDateTime().toLocalDate().isBefore(to)).findFirst();
        if (!latest.isPresent()) {
            return -1;
        }
        Transfer t = latest.get();
        switch (t.getTransferType()) {
            case WITHDRAW:
            case REJECT:
            case SAMPLE:
                return t.getPrevPartQuantity() - t.getTransferQuantity();
            case RECEIVE:
                return t.getPrevPartQuantity() + t.getTransferQuantity();
            default:
                return t.getPrevPartQuantity();
        }
    }

    public void addCustomer(String name) {
        CustomerDAO.insertCustomer(name);
    }

    public void addProduct(String name, Customer customer) {
        ProductDAO.insertProduct(name, customer);
        Optional<Product> prod = ProductDAO.getProductByName(name);
        PartDAO.insertPart("Default", 0, prod.get());
        Optional<Part> part = PartDAO.getPartByNameAndProduct("Default", prod.get());
        Product updatedProd = new Product(prod.get().getDBName(), prod.get().getCreationDateTime(), prod.get().getCustomer(), part.get(), prod.get().getId());
        ProductDAO.updateProduct(updatedProd);
        part.get().getProduct().setDefaultPart(part.get());
    }

    public void addPart(String name, int quantity, Product product) {
        PartDAO.insertPart(name, quantity, product);
        Optional<Part> newPart = PartDAO.getPartByNameAndProduct(name, product);
        if (!newPart.isPresent()) {
            throw new RuntimeException("Part not found after insertion");
        }
        // Update next part of the last part of the product
        Part curr = product.getDefaultPart();
        while (curr.getNextPart() != null) {
            curr = curr.getNextPart();
        }
        curr.setNextPart(newPart.get());
        PartDAO.updatePart(curr);
    }

    public void addTransfer(Part part, int quantity, Transfer.Action action) {
        TransferDAO.insertTransfer(part, quantity, action);
        switch (action) {
            case WITHDRAW:
            case REJECT:
            case SAMPLE:
                PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity() - quantity, part.getProduct(), part.getId()));
                break;
            case RECEIVE:
                PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity() + quantity, part.getProduct(), part.getId()));
                break;
        }
        // Check for default part change
        if (part.getProduct().getDefaultPart().equals(part)) {
            Optional<Part> newDefault = PartDAO.getPart(part.getId());
            if (!newDefault.isPresent()) {
                throw new RuntimeException("Part not found after insertion");
            }
            ProductDAO.updateProduct(new Product(part.getProduct().getDBName(), part.getProduct().getCreationDateTime(), part.getProduct().getCustomer(), newDefault.get(), part.getProduct().getId()));
        }
    }

    public void updateCustomer(Customer customer, String name) {
        CustomerDAO.updateCustomer(new Customer(name, customer.getCreationDateTime(), customer.getId()));
        Customer cust = CustomerDAO.getCustomer(customer.getId()).get();
        ProductDAO.getProductsByCustomer(customer)
                .forEach(product -> ProductDAO.updateProduct(new Product(product.getDBName(), product.getCreationDateTime(), cust, product.getId())));
    }

    public void updateDefaultPart(Part newDefault) {
        // Update next part of the part that is pointing to the new default part
        Optional<Part> prev = PartDAO.getPartsByProduct(newDefault.getProduct())
                .filter(p -> p.getNextPart() != null && p.getNextPart().equals(newDefault)).findFirst();
        prev.ifPresent(p -> {
            p.setNextPart(newDefault.getNextPart());
            PartDAO.updatePart(p);
        });
        // Update next part of the new default part to be the old default part
        Part oldDefault = newDefault.getProduct().getDefaultPart();
        if (oldDefault != null) {
            newDefault.setNextPart(oldDefault);
            PartDAO.updatePart(newDefault);
        }
        // Update default part of the product
        newDefault.getProduct().setDefaultPart(newDefault);
        ProductDAO.updateProduct(newDefault.getProduct());
    }

    public void updatePartName(Part part, String name) {
        PartDAO.updatePart(new Part(name, part.getCreationDateTime(), part.getPartQuantity(), part.getProduct(), part.getNextPart(), part.getPartNotes(), part.getId()));
        Optional<Part> newPart = PartDAO.getPartByNameAndProduct(name, part.getProduct());
        if (!newPart.isPresent()) {
            throw new RuntimeException("Part not found after insertion");
        }
        // Check for parts that are pointing to this part
        PartDAO.getPartsByProduct(part.getProduct()).filter(p -> p.getNextPart() != null && p.getNextPart().equals(part))
                .forEach(p -> {
                    p.setNextPart(newPart.get());
                    PartDAO.updatePart(p);
                });
        // Check if product's default part is this part
        if (part.getProduct().getDefaultPart() != null && part.getProduct().getDefaultPart().equals(part)) {
            part.getProduct().setDefaultPart(newPart.get());
            ProductDAO.updateProduct(part.getProduct());
        }
    }

    public void updatePartQuantity(Part part, int quantity) {
        PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(), quantity, part.getProduct(), part.getNextPart(), part.getPartNotes(), part.getId()));
    }

    public void deleteCustomer(Customer customer) {
        List<Product> list = ProductDAO.getProducts().stream().filter(p -> p.getCustomer().equals(customer)).collect(Collectors.toList());
        for (Product product : list) {
            deleteProduct(product);
        };
        CustomerDAO.deleteCustomer(customer.getId());
    }

    public void deleteProduct(Product product) {
        List<Part> list = PartDAO.getPartsByProduct(product).collect(Collectors.toList());
        for (Part part : list) {
            deletePart(part);
        };
        ProductDAO.deleteProduct(product.getId());
    }

    public void deletePart(Part part) {
        // Delete all transfers associated with the part
        List<Transfer> list = TransferDAO.getTransfersByPart(part).collect(Collectors.toList());
        for (Transfer transfer : list) {
            deleteTransfer(transfer);
        };
        Part curr = part.getProduct().getDefaultPart();
        // Check if the part to be deleted is the default part
        if (curr.equals(part) && curr.getNextPart() != null) {
            // Update the next part of the part to be deleted to be the new default part
            updateDefaultPart(part.getNextPart());
            // Update the current part to be the new default part
            curr = part.getProduct().getDefaultPart();
        }
        // Find the previous part of the part to be deleted
        while (curr.getNextPart() != null && !curr.getNextPart().equals(part)) {
            curr = curr.getNextPart();
        }
        if (curr.getNextPart() == null) {
            Logger.getAnonymousLogger().log(Level.FINE, "Part to be deleted is not linked to default part");
        } else {
            curr.setNextPart(part.getNextPart());
            PartDAO.updatePart(curr);
        }
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
            sync.syncCustomers();
        }
        sync.syncTransfers(date);
    }
}
