package com.ils.models;

import java.time.LocalDateTime;

public class Transfer {
    public enum Action {
        INCOMING,
        WITHDRAW,
        SAMPLE,
        REJECT
    }

    private final LocalDateTime transferDateTime;
    // private final Part part;
    private final Product product;
    private final Integer transferQuantity;
    private final Action transferType;
    private final Integer transferId;

    public Transfer(LocalDateTime transferDateTime, Product product, int quantity, Action transferType, int id) {
        this.transferDateTime = transferDateTime;
        // this.part = part;
        this.product = product;
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

    // public Part getPart() {
    //     return part;
    // }

    public Product getProduct() {
        return product;
    }

    public Integer getTransferQuantity() {
        return transferQuantity;
    }

    public Action getTransferType() {
        return transferType;
    }

    @Override
    public String toString() {
        // return "Transfer [" + transferDateTime + ", " + part + ", " + transferQuantity + ", " + transferType.name() + "]";
        return "Transfer [" + transferDateTime + ", " + product + ", " + transferQuantity + ", " + transferType.name() + "]";
    }
}
