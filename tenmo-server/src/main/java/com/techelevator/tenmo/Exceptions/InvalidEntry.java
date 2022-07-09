package com.techelevator.tenmo.Exceptions;

public class InvalidEntry extends Exception {
    public InvalidEntry () {
        super("Invalid entry.");
    }

    public InvalidEntry (String message) {
        super(message);
    }
}