package com.ils.logic.management;

import com.ils.logic.DAO.CustomerDAO;
import com.ils.logic.DAO.ProductDAO;
import com.ils.logic.Filters;
import com.ils.logic.Logic;
import com.ils.models.Customer;
import com.ils.models.Product;

import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class CustomerManagement {
    private Filters filters;
    private FilteredList<Customer> customerFilteredList;
    private SortedList<Customer> customerSortedList;
    private Customer selectedCustomer;

    /**
     * Create a new CustomerManagement object.
     * @param filters
     */
    public CustomerManagement(Filters filters) {
        this.filters = filters;
        this.customerFilteredList = CustomerDAO.getCustomers();
        this.customerSortedList = new SortedList<>(customerFilteredList);
        customerFilteredList.predicateProperty().bind(filters.getCustomerNameFilter());
    }

    /**
     * Get a sorted list of customers.
     * @return SortedList<Customer> using customerFilteredList as source
     */
    public SortedList<Customer> getCustomers() {
        return this.customerSortedList;
    }

    /**
     * Add a new customer to the database.
     * @param name
     */
    public void addCustomer(String name) {
        CustomerDAO.insertCustomer(name);
    }

    /**
     * Update a customer in the database.
     * @param customer
     * @param name
     */
    public void updateCustomer(Customer customer, String name) {
        CustomerDAO.updateCustomer(new Customer(name, customer.getCreationDateTime(), customer.getId()));
        Customer cust = CustomerDAO.getCustomer(customer.getId()).get();
        for (Product product : ProductDAO.getProductsByCustomer(customer).collect(Collectors.toList())) {
            ProductDAO.updateProduct(new Product(product.getDBName(), product.getCreationDateTime(), cust,
                    product.getDefaultPart(), product.getProductName(), product.getProductNotes(), product.getId()));
        }
    }

    /**
     * Delete a customer from the database.
     * @param customer
     */
    public void deleteCustomer(Customer customer) {
        List<Product> list = ProductDAO.getProducts().stream().filter(p -> p.getCustomer().equals(customer))
                .collect(Collectors.toList());
        for (Product product : list) {
            Logic.getProductManagement().deleteProduct(product);
        }
        CustomerDAO.deleteCustomer(customer.getId());
    }

    /**
     * Select a customer for filtering.
     * @param customer
     */
    public void selectCustomer(Customer customer) {
        if (customer == null) {
            this.selectedCustomer = null;
            filters.clearProductCustomerFilter();
            filters.clearTransferCustomerFilter();
            return;
        }
        this.selectedCustomer = customer;
        filters.filterProductByCustomer(customer);
        filters.filterTransferByCustomer(customer);
    }

    /**
     * Set the customer name filter.
     * @param name
     */
    public void setCustomerNameFilter(String name) {
        if (name == null) {
            filters.clearCustomerNameFilter();
            return;
        }
        filters.filterCustomerByName(name);
    }

    /**
     * Get the selected customer.
     * @return Customer
     */
    public Customer getSelectedCustomer() {
        return this.selectedCustomer;
    }
}
