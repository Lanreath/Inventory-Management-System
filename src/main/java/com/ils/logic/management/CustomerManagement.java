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

    public CustomerManagement(Filters filters) {
        this.filters = filters;
        this.customerFilteredList = CustomerDAO.getCustomers();
        this.customerSortedList = new SortedList<>(customerFilteredList);
        customerFilteredList.predicateProperty().bind(filters.getCustomerNameFilter());
    }

    public SortedList<Customer> getCustomers() {
        return this.customerSortedList;
    }

    public void addCustomer(String name) {
        CustomerDAO.insertCustomer(name);
    }

    public void updateCustomer(Customer customer, String name) {
        CustomerDAO.updateCustomer(new Customer(name, customer.getCreationDateTime(), customer.getId()));
        Customer cust = CustomerDAO.getCustomer(customer.getId()).get();
        for (Product product : ProductDAO.getProductsByCustomer(customer).collect(Collectors.toList())) {
            ProductDAO.updateProduct(new Product(product.getDBName(), product.getCreationDateTime(), cust,
                    product.getDefaultPart(), product.getProductName(), product.getProductNotes(), product.getId()));
        }
    }

    public void deleteCustomer(Customer customer) {
        List<Product> list = ProductDAO.getProducts().stream().filter(p -> p.getCustomer().equals(customer))
                .collect(Collectors.toList());
        for (Product product : list) {
            Logic.getProductManagement().deleteProduct(product);
        }
        CustomerDAO.deleteCustomer(customer.getId());
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
