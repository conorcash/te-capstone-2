package com.techelevator.tenmo.Exceptions;

public class AccountNotFound extends Exception {
    public AccountNotFound () {
        super("Account not found.");
    }
}
