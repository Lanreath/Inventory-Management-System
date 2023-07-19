package com.ils.logic.management;

import com.ils.logic.DAO.PartDAO;
import com.ils.logic.DAO.ProductDAO;
import com.ils.logic.DAO.TransferDAO;
import com.ils.logic.Filters;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

import java.util.Optional;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.time.LocalDate;

public class TransferManagement {
    private Filters filters;
    private FilteredList<Transfer> transferFilteredList;
    private SortedList<Transfer> transferSortedList;

    public TransferManagement(Filters filters) {
        this.filters = filters;
        this.transferFilteredList = TransferDAO.getTransfers();
        this.transferSortedList = new SortedList<>(transferFilteredList);
        transferFilteredList.predicateProperty().bind(Bindings.createObjectBinding(
                () -> filters.getTransferDateFilter().get().and(filters.getTransferActionFilter().get())
                        .and(filters.getTransferCustomerFilter().get()).and(filters.getTransferProductFilter().get())
                        .and(filters.getTransferPartFilter().get()),
                filters.getTransferDateFilter(), filters.getTransferActionFilter(), filters.getTransferCustomerFilter(),
                filters.getTransferProductFilter(), filters.getTransferPartFilter()));
    }

    public SortedList<Transfer> getTransfers() {
        return this.transferSortedList;
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

    public void addTransfer(Part part, int quantity, Transfer.Action action) {
        TransferDAO.insertTransfer(part, quantity, action);
        switch (action) {
            case DAILY:
            case DESTRUCT:
            case PROJECT:
            case REJECT_DAILY:
            case REJECT_PROJECT:
            case REJECT_RENEWAL:
            case RENEWAL:
            case SAMPLE:
                PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(),
                        part.getPartQuantity() - quantity, part.getProduct(), part.getId()));
                break;
            case RECEIVED:
                PartDAO.updatePart(new Part(part.getPartName(), part.getCreationDateTime(),
                        part.getPartQuantity() + quantity, part.getProduct(), part.getId()));
                break;
        }
        // Check for default part change
        if (part.getProduct().getDefaultPart().equals(part)) {
            Optional<Part> newDefault = PartDAO.getPart(part.getId());
            if (!newDefault.isPresent()) {
                throw new RuntimeException("Part not found after insertion");
            }
            ProductDAO.updateProduct(new Product(part.getProduct().getDBName(), part.getProduct().getCreationDateTime(),
                    part.getProduct().getCustomer(), newDefault.get(), part.getProduct().getId()));
        }
    }

    public void deleteTransfer(Transfer transfer) {
        TransferDAO.deleteTransfer(transfer.getId());
    }
}
