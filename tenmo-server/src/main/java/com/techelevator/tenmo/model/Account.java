package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {
    private int accountId;
    private int userId;
    private BigDecimal balance;

    public Account() {
    }

    public Account(int userId,BigDecimal beginningBalance) {
        this.userId = userId;
        this.balance = beginningBalance;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
//
//    public BigDecimal withdraw (BigDecimal amount) throws InsufficientBalance, InvalidAmount {
//        if (amount.compareTo(balance) > 0) {
//            throw new InsufficientBalance();
//        } else if (amount.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new InvalidAmount();
//        }
//        return balance.subtract(amount);
//    }
//
//    public BigDecimal deposit (BigDecimal amount) throws InvalidAmount {
//        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new InvalidAmount();
//        }
//        return balance.add(amount);
//    }


}
