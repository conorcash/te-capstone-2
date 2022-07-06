package com.techelevator.tenmo.model;

import com.techelevator.tenmo.Exceptions.InvalidAmount;

import java.math.BigDecimal;

public class Transaction {
    private int transactionId;
    private int senderId;
    private int recipientId;
    private BigDecimal amount;
    private enum Status {PENDING,APPROVED,REJECTED};
    private Status status = Status.PENDING;

    Account sender = new Account(senderId);
    Account recipient = new Account(recipientId);

    public int getTransactionId() {
        return transactionId;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public void setAmount(BigDecimal amount) throws InvalidAmount {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmount();
        }
        this.amount = amount;
    }

    public void approve () {
        status = Status.APPROVED;
    }

    public void reject () {
        status = Status.REJECTED;
    }
}
