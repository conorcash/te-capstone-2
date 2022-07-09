package com.techelevator.tenmo.Exceptions;

import com.techelevator.tenmo.model.Account;

public class AccountNotFound extends Exception {
    public AccountNotFound () {
        super("Account not found.");
    }

    public AccountNotFound (String message) {
        super(message);
    }
}
