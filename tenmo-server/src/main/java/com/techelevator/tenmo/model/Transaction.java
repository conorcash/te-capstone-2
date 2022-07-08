package com.techelevator.tenmo.model;


import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class Transaction {
    private int transactionId;
    @NotBlank
    private int senderId;
    @NotBlank
    private int recipientId;
    private boolean isRequest;
    private BigDecimal amount;
    private String status;

    public Transaction () {

    }

    public Transaction (int senderId, int recipientId, BigDecimal amount, boolean isRequest) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.isRequest = isRequest;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public void setIsRequest(boolean isRequest) {
        this.isRequest = isRequest;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public void setAmount(BigDecimal amount) {
//        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new InvalidAmount();
//        }
        this.amount = amount;
    }

//    public void approve () {
//        status = "Approved";
//    }
//
//    public void reject () {
//        status = "Rejected";
//    }

}
