package com.ils.logic.management;

import com.ils.logic.Filters;
import com.ils.logic.DAO.CustomerDAO;
import com.ils.logic.DAO.ProductDAO;
import com.ils.models.Customer;
import com.ils.models.Product;

import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class CustomerManagement {
    private Filters filters;
    private FilteredList<Customer> customerFilteredList;
    private SortedList<Customer> customerSortedList;
    private ObjectProperty<Customer> selectedCustomer;

    public CustomerManagement(Filters filters) {
        this.customerFilteredList = CustomerDAO.getCustomers();
        this.customerSortedList = new SortedList<>(customerFilteredList);
        this.selectedCustomer = new SimpleObjectProperty<>(null);
        customerFilteredList.predicateProperty().bind(filters.getCustomerNameFilter());
    }

    public SortedList<Customer> getCustomers() {
        return this.customerSortedList;
    }

    public ObjectProperty<Customer> getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer customer) {
        selectedCustomer.set(customer);
    }

    public void updateCustomer(Customer customer, String name) {
        CustomerDAO.updateCustomer(new Customer(name, customer.getCreationDateTime(), customer.getId()));
        Customer cust = CustomerDAO.getCustomer(customer.getId()).get();
        for (Product product : ProductDAO.getProductsByCustomer(customer).collect(Collectors.toList())) {
            ProductDAO.updateProduct(new Product(product.getDBName(), product.getCreationDateTime(), cust,
                    product.getDefaultPart(), product.getProductName(), product.getProductNotes(), product.getId()));
        }
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

    public void setCustomerNameFilter(String name) {
        if (name == null) {
            filters.clearCustomerNameFilter();
            return;
        }
        filters.filterCustomerByName(name);
    }
}
