package com.ils.logic.management;

import com.ils.logic.DAO.PartDAO;
import com.ils.logic.DAO.TransferDAO;
import com.ils.logic.Filters;
import com.ils.logic.Logic;
import com.ils.models.Part;
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
                Logic.getPartManagement().updatePartQuantity(part, part.getPartQuantity() - quantity);
                break;
            case RECEIVED:
                Logic.getPartManagement().updatePartQuantity(part, part.getPartQuantity() + quantity);
                break;
        }
        // Check for default part change
        if (part.getProduct().getDefaultPart().equals(part)) {
            Optional<Part> newDefault = PartDAO.getPart(part.getId());
            if (!newDefault.isPresent()) {
                throw new RuntimeException("Part not found after insertion");
            }
            Logic.getProductManagement().updateDefaultPart(newDefault.get()); 
        }
    }

    public void deleteTransfer(Transfer transfer) {
        switch (transfer.getTransferType()) {
            case DAILY:
            case DESTRUCT:
            case PROJECT:
            case REJECT_DAILY:
            case REJECT_PROJECT:
            case REJECT_RENEWAL:
            case RENEWAL:
            case SAMPLE:
                for (Transfer t : TransferDAO.getTransfersByPart(transfer.getPart()).filter(t -> t.getTransferDateTime().isAfter(transfer.getTransferDateTime())).toArray(Transfer[]::new)) {
                        TransferDAO.updateTransfer(new Transfer(t.getTransferDateTime(), t.getPart(),
                                t.getPrevPartQuantity() + transfer.getTransferQuantity(), t.getTransferQuantity(), t.getTransferType(),
                                t.getId()));
                }
                Logic.getPartManagement().updatePartQuantity(transfer.getPart(), transfer.getPart().getPartQuantity() + transfer.getTransferQuantity());
                break;
            case RECEIVED:
                for (Transfer t : TransferDAO.getTransfersByPart(transfer.getPart()).filter(t -> t.getTransferDateTime().isAfter(transfer.getTransferDateTime())).toArray(Transfer[]::new)) {
                        TransferDAO.updateTransfer(new Transfer(t.getTransferDateTime(), t.getPart(),
                                t.getPrevPartQuantity() - transfer.getTransferQuantity(), t.getTransferQuantity(), t.getTransferType(),
                                t.getId()));
                }
                Logic.getPartManagement().updatePartQuantity(transfer.getPart(), transfer.getPart().getPartQuantity() - transfer.getTransferQuantity());
                break;
        }
    }
}
