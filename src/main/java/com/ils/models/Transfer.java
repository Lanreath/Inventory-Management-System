package com.ils.models;

import java.time.LocalDateTime;

public class Transfer {
    public enum Action {
        RECEIVE,
        WITHDRAW,
        SAMPLE,
        REJECT
    }

    private final LocalDateTime transferDateTime;
    private final Part part;
    private final Integer prevPartQuantity;
    private final Integer transferQuantity;
    private final Action transferType;
    private final Integer transferId;

    public Transfer(LocalDateTime transferDateTime, Part part, int prevPartQuantity, int quantity, Action transferType, int id) {
        this.transferDateTime = transferDateTime;
        this.part = part;
        this.prevPartQuantity = prevPartQuantity;
        this.transferQuantity = quantity;
        this.transferType = transferType;
        this.transferId = id;
    }

    public int getId() {
        return transferId;
    }

    public LocalDateTime getTransferDateTime() {
        return transferDateTime;
    }

    public Part getPart() {
        return part;
    }

    public Integer getPrevPartQuantity() {
        return prevPartQuantity;
    }

    public Integer getTransferQuantity() {
        return transferQuantity;
    }

    public Action getTransferType() {
        return transferType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Transfer) {
            Transfer other = (Transfer) obj;
            return this.transferDateTime.equals(other.transferDateTime) && this.part.equals(other.part)
                    && this.prevPartQuantity.equals(other.prevPartQuantity) && this.transferQuantity.equals(other.transferQuantity) && this.transferType.equals(other.transferType) && this.transferId.equals(other.transferId);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Transfer [" + transferDateTime + ", " + part + ", " + transferQuantity + ", " + transferType.name() + "]";
    }
}
