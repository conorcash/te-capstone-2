package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.Exceptions.AccountNotFound;
import com.techelevator.tenmo.Exceptions.InsufficientBalance;
import com.techelevator.tenmo.Exceptions.InvalidAmount;
import com.techelevator.tenmo.Exceptions.InvalidEntry;
import com.techelevator.tenmo.model.Transaction;

import java.util.List;

public interface TransactionsDao {

    List<Transaction> listTransactions();

    List<Transaction> listAccountTransactions(int accountId);

    Transaction create(Transaction transaction, int creatorId) throws InsufficientBalance, InvalidAmount, AccountNotFound, InvalidEntry;

    Transaction findTransactionById(int id);

    void accept(int transactionId) throws InsufficientBalance, AccountNotFound, InvalidAmount;

    void reject(int transactionId);
}
