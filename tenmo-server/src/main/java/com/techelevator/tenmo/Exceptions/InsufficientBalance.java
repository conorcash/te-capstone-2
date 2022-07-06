package com.techelevator.tenmo.Exceptions;

public class InsufficientBalance extends Exception {
    public InsufficientBalance () {
        super ("Insufficient funds.");
    }
}
