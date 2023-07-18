package com.ils.logic.management;

import com.ils.logic.Filters;
import com.ils.logic.Logic;
import com.ils.logic.DAO.PartDAO;
import com.ils.logic.DAO.ProductDAO;
import com.ils.logic.DAO.TransferDAO;
import com.ils.models.Customer;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class ProductManagement {
    private Filters filters;
    private ObjectProperty<Product> selectedProduct;
    private FilteredList<Product> productFilteredList;
    private SortedList<Product> productSortedList;

    public ProductManagement(Filters filters) {
        this.filters = filters;
        this.selectedProduct = new SimpleObjectProperty<>(null);
        this.productFilteredList = ProductDAO.getProducts();
        this.productSortedList = new SortedList<>(productFilteredList);
        productFilteredList.predicateProperty()
                .bind(Bindings.createObjectBinding(
                        () -> filters.getDBNameFilter().get().and(filters.getProductCustomerFilter().get()),
                        filters.getDBNameFilter(), filters.getProductCustomerFilter()));
    }

    public SortedList<Product> getProducts() {
        return this.productSortedList;
    }

    public ObjectProperty<Predicate<Product>> getDBNameFilter() {
        return filters.getDBNameFilter();
    }

    public ObjectProperty<Predicate<Product>> getProductCustomerFilter() {
        return filters.getProductCustomerFilter();
    }

    public void setProductNameFilter(String name) {
        if (name == null) {
            filters.clearDBNameFilter();
            return;
        }
        filters.filterProductByName(name);
    }

    public ObjectProperty<Product> getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product product) {
        selectedProduct.set(product);
    }

    public void addProduct(String name, Customer customer) {
        ProductDAO.insertProduct(name, customer);
        Optional<Product> prod = ProductDAO.getProductByDBName(name);
        PartDAO.insertPart("Default", 0, prod.get());
        Optional<Part> part = PartDAO.getPartByNameAndProduct("Default", prod.get());
        Product updatedProd = new Product(prod.get().getDBName(), prod.get().getCreationDateTime(),
                prod.get().getCustomer(), part.get(), prod.get().getId());
        ProductDAO.updateProduct(updatedProd);
        part.get().getProduct().setDefaultPart(part.get());
    }

    public void updateProductName(Product product, String name) {
        Product newProd = new Product(product.getDBName(), product.getCreationDateTime(), product.getCustomer(),
                product.getDefaultPart(), name, product.getProductNotes(), product.getId());
        // Update product of parts
        for (Part part : PartDAO.getPartsByProduct(product).collect(Collectors.toList())) {
            Part newPart = new Part(part.getPartName(), part.getCreationDateTime(), part.getPartQuantity(), newProd,
                    part.getNextPart(), part.getPartNotes(), part.getId());
            PartDAO.updatePart(newPart);
            // Check for default part change
            if (part.getProduct().getDefaultPart() != null && part.getProduct().getDefaultPart().equals(part)) {
                newProd.setDefaultPart(newPart);
            }
            // Check for parts that have this part as next part
            for (Part p : PartDAO.getPartsByProduct(part.getProduct()).collect(Collectors.toList())) {
                if (p.getNextPart() != null && p.getNextPart().equals(part)) {
                    p.setNextPart(newPart);
                    PartDAO.updatePart(p);
                }
            }
            // Update transfers of parts
            for (Transfer transfer : TransferDAO.getTransfersByPart(part).collect(Collectors.toList())) {
                Transfer newTransfer = new Transfer(transfer.getTransferDateTime(), newPart,
                        transfer.getPrevPartQuantity(), transfer.getTransferQuantity(), transfer.getTransferType(),
                        transfer.getId());
                TransferDAO.updateTransfer(newTransfer);
            }
        }
        ProductDAO.updateProduct(newProd);
    }

    public void deleteProduct(Product product) {
        List<Part> list = PartDAO.getPartsByProduct(product).collect(Collectors.toList());
        for (Part part : list) {
            Logic.getPartManagement().deletePart(part);
        }
        ;
        ProductDAO.deleteProduct(product.getId());
    }

    public void selectProduct(Product product) {
        if (product == null) {
            filters.clearTransferProductFilter();
            return;
        }
        filters.filterTransferByProduct(product);
    }

    public void updateProductNotes(Product product, String notes) {
        Product newProd = new Product(product.getDBName(), product.getCreationDateTime(), product.getCustomer(),
                product.getDefaultPart(), product.getProductName(), notes, product.getId());
        // Update product of parts
        for (Part part : PartDAO.getPartsByProduct(product).collect(Collectors.toList())) {
            PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(),
                    part.getPartQuantity(), newProd,
                    part.getNextPart(), part.getPartNotes(), part.getId()));
        }
        // Update nextPart of parts
        for (Part part : PartDAO.getPartsByProduct(product).collect(Collectors.toList())) {
            if (part.getNextPart() != null) {
                PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(),
                        part.getPartQuantity(),
                        part.getProduct(), PartDAO.getPart(part.getNextPart().getId()).get(),
                        part.getPartNotes(),
                        part.getId()));
            }
        }
        // Update default part
        if (product.getDefaultPart() != null) {
            Part newDefault = PartDAO.getPart(product.getDefaultPart().getId()).get();
            newProd.setDefaultPart(newDefault);
        }
        // Update product
        ProductDAO.updateProduct(newProd);
    }

    public void updateDefaultPart(Part newDefault) {
        if (newDefault.getProduct().getDefaultPart().equals(newDefault)) {
            return;
        }
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
}
