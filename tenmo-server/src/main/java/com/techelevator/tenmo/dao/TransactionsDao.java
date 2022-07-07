package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.util.List;

public interface TransactionsDao {

    List<Transaction> listTransactions();

    int create(Transaction transaction);

    Transaction findTransactionById(int id);

    void accept();

    void reject();
}
