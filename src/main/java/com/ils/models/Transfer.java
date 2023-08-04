package com.ils.models;

import java.time.LocalDateTime;

public class Transfer {
    // Transfer actions
    public enum Action {
        RECEIVED,
        DAILY,
        DESTRUCT,
        RENEWAL,
        PROJECT,
        SAMPLE,
        REJECT_DAILY,
        REJECT_RENEWAL,
        REJECT_PROJECT,
    }

    private final LocalDateTime transferDateTime;
    private final Part part;
    private final Integer prevPartQuantity;
    private final Integer transferQuantity;
    private final Action transferType;
    private final Integer transferId;

    /**
     * Create a new Transfer object.
     * @param transferDateTime
     * @param part
     * @param prevPartQuantity
     * @param quantity
     * @param transferType
     * @param id
     */
    public Transfer(LocalDateTime transferDateTime, Part part, int prevPartQuantity, int quantity, Action transferType, int id) {
        this.transferDateTime = transferDateTime;
        this.part = part;
        this.prevPartQuantity = prevPartQuantity;
        this.transferQuantity = quantity;
        this.transferType = transferType;
        this.transferId = id;
    }

    /**
     * Get the transfer id.
     * @return Integer
     */
    public int getId() {
        return transferId;
    }

    /**
     * Get the transfer date and time.
     * @return LocalDateTime
     */
    public LocalDateTime getTransferDateTime() {
        return transferDateTime;
    }

    /**
     * Get the part.
     * @return Part
     */
    public Part getPart() {
        return part;
    }

    /**
     * Get the previous part quantity.
     * @return Integer
     */
    public Integer getPrevPartQuantity() {
        return prevPartQuantity;
    }

    /**
     * Get the transfer quantity.
     * @return Integer
     */
    public Integer getTransferQuantity() {
        return transferQuantity;
    }

    /**
     * Get the transfer type.
     * @return Action
     */
    public Action getTransferType() {
        return transferType;
    }

    /**
     * Check if the transfer is equal to another object.
     * @param obj
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Transfer) {
            Transfer other = (Transfer) obj;
            return this.transferDateTime.equals(other.transferDateTime) && this.part.equals(other.part)
                    && this.prevPartQuantity.equals(other.prevPartQuantity) && this.transferQuantity.equals(other.transferQuantity) && this.transferType.equals(other.transferType) && this.transferId.equals(other.transferId);
        }
        return false;
    }

    /**
     * Get the string representation of the transfer.
     * @return String
     */
    @Override
    public String toString() {
        return "Transfer [" + transferDateTime + ", " + part + ", " + transferQuantity + ", " + transferType.name() + "]";
    }
}
