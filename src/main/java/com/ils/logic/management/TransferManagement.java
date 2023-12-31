package com.ils.logic.management;

import com.ils.logic.DAO.PartDAO;
import com.ils.logic.DAO.ProductDAO;
import com.ils.logic.DAO.TransferDAO;
import com.ils.logic.Filters;
import com.ils.logic.Logic;
import com.ils.models.Part;
import com.ils.models.Product;
import com.ils.models.Transfer;

import java.time.LocalDate;
import java.util.Optional;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class TransferManagement {
    private Filters filters;
    private FilteredList<Transfer> transferFilteredList;
    private SortedList<Transfer> transferSortedList;

    /**
     * Create a new TransferManagement object.
     * @param filters
     */
    public TransferManagement(Filters filters) {
        this.filters = filters;
        this.transferFilteredList = TransferDAO.getTransfers();
        this.transferSortedList = new SortedList<>(transferFilteredList);
        transferFilteredList.predicateProperty().bind(Bindings.createObjectBinding(
                () -> filters.getTransferFromFilter().get().and(filters.getTransferToFilter().get()).and(filters.getTransferActionFilter().get())
                        .and(filters.getTransferCustomerFilter().get()).and(filters.getTransferProductFilter().get())
                        .and(filters.getTransferPartFilter().get()),
                filters.getTransferFromFilter(), filters.getTransferToFilter(), filters.getTransferActionFilter(), filters.getTransferCustomerFilter(),
                filters.getTransferProductFilter(), filters.getTransferPartFilter()));
    }

    /**
     * Get a sorted list of transfers.
     * @return SortedList<Transfer> using transferFilteredList as source
     */
    public SortedList<Transfer> getTransfers() {
        return this.transferSortedList;
    }

    /**
     * Set the transfer type filter
     * @param type
     */
    public void setTransferActionFilter(Transfer.Action type) {
        if (type == null) {
            filters.clearTransferActionFilter();
            return;
        }
        filters.filterTransferByAction(type);
    }

    /**
     * Add a transfer to the database.
     * @param part
     * @param quantity
     * @param action
     */
    public void addTransfer(Part part, int quantity, Transfer.Action action) {
        // Insert transfer
        TransferDAO.insertTransfer(part, quantity, action);
        // Update part quantity
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
                throw new RuntimeException("Updated part not found");
            }
            // Update default
            ProductDAO.updateProduct(new Product(part.getProduct().getDBName(), part.getProduct().getCreationDateTime(),
                    part.getProduct().getCustomer(), newDefault.get(), part.getProduct().getProductName(), part.getProduct().getProductNotes(), part.getProduct().getId()));
        }
    }

    /**
     * Add a transfer to the database.
     * @param part
     * @param quantity
     * @param action
     * @param date
     */
    public void addTransfer(Part part, int quantity, Transfer.Action action, LocalDate date) {
        TransferDAO.insertTransfer(part, quantity, action, date);
        // Update part quantity
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
                throw new RuntimeException("Updated part not found");
            }
            // Update default
            ProductDAO.updateProduct(new Product(part.getProduct().getDBName(), part.getProduct().getCreationDateTime(),
                    part.getProduct().getCustomer(), newDefault.get(), part.getProduct().getProductName(), part.getProduct().getProductNotes(), part.getProduct().getId()));
        }
    }

    /**
     * Update a transfer in the database.
     * @param transfer
     */
    public void updateTransfer(Transfer transfer) {
        TransferDAO.updateTransfer(transfer);
        // Update part quantity and transfer previous quantity
        switch (transfer.getTransferType()) {
            case DAILY:
            case DESTRUCT:
            case PROJECT:
            case REJECT_DAILY:
            case REJECT_PROJECT:
            case REJECT_RENEWAL:
            case RENEWAL:
            case SAMPLE:
                for (Transfer t : TransferDAO.getTransfersByPart(transfer.getPart())
                        .filter(t -> t.getTransferDateTime().isAfter(transfer.getTransferDateTime()))
                        .toArray(Transfer[]::new)) {
                    TransferDAO.updateTransfer(new Transfer(t.getTransferDateTime(), t.getPart(),
                            t.getPrevPartQuantity() - transfer.getTransferQuantity(), t.getTransferQuantity(),
                            t.getTransferType(),
                            t.getId()));
                }
                Logic.getPartManagement().updatePartQuantity(transfer.getPart(),
                        transfer.getPart().getPartQuantity() - transfer.getTransferQuantity());
                break;
            case RECEIVED:
                for (Transfer t : TransferDAO.getTransfersByPart(transfer.getPart())
                        .filter(t -> t.getTransferDateTime().isAfter(transfer.getTransferDateTime()))
                        .toArray(Transfer[]::new)) {
                    TransferDAO.updateTransfer(new Transfer(t.getTransferDateTime(), t.getPart(),
                            t.getPrevPartQuantity() + transfer.getTransferQuantity(), t.getTransferQuantity(),
                            t.getTransferType(),
                            t.getId()));
                }
                Logic.getPartManagement().updatePartQuantity(transfer.getPart(),
                        transfer.getPart().getPartQuantity() + transfer.getTransferQuantity());
                break;
        }
        // Check for default part change
        if (transfer.getPart().getProduct().getDefaultPart().equals(transfer.getPart())) {
            Optional<Part> newDefault = PartDAO.getPart(transfer.getPart().getId());
            if (!newDefault.isPresent()) {
                throw new RuntimeException("Updated part not found");
            }
            ProductDAO.updateProduct(new Product(transfer.getPart().getProduct().getDBName(), transfer.getPart().getProduct().getCreationDateTime(),
                    transfer.getPart().getProduct().getCustomer(), newDefault.get(),transfer.getPart().getProduct().getProductName(), transfer.getPart().getProduct().getProductNotes(), transfer.getPart().getProduct().getId()));
        }
    }

    /**
     * Delete a transfer from the database.
     * @param transfer
     */
    public void deleteTransfer(Transfer transfer) {
        // Update part quantity and transfer previous quantity
        switch (transfer.getTransferType()) {
            case DAILY:
            case DESTRUCT:
            case PROJECT:
            case REJECT_DAILY:
            case REJECT_PROJECT:
            case REJECT_RENEWAL:
            case RENEWAL:
            case SAMPLE:
                for (Transfer t : TransferDAO.getTransfersByPart(transfer.getPart())
                        .filter(t -> t.getTransferDateTime().isAfter(transfer.getTransferDateTime()))
                        .toArray(Transfer[]::new)) {
                    TransferDAO.updateTransfer(new Transfer(t.getTransferDateTime(), t.getPart(),
                            t.getPrevPartQuantity() + transfer.getTransferQuantity(), t.getTransferQuantity(),
                            t.getTransferType(),
                            t.getId()));
                }
                Logic.getPartManagement().updatePartQuantity(transfer.getPart(),
                        transfer.getPart().getPartQuantity() + transfer.getTransferQuantity());
                break;
            case RECEIVED:
                for (Transfer t : TransferDAO.getTransfersByPart(transfer.getPart())
                        .filter(t -> t.getTransferDateTime().isAfter(transfer.getTransferDateTime()))
                        .toArray(Transfer[]::new)) {
                    TransferDAO.updateTransfer(new Transfer(t.getTransferDateTime(), t.getPart(),
                            t.getPrevPartQuantity() - transfer.getTransferQuantity(), t.getTransferQuantity(),
                            t.getTransferType(),
                            t.getId()));
                }
                Logic.getPartManagement().updatePartQuantity(transfer.getPart(),
                        transfer.getPart().getPartQuantity() - transfer.getTransferQuantity());
                break;
        }
        // Delete transfer
        TransferDAO.deleteTransfer(transfer.getId());
    }
}
