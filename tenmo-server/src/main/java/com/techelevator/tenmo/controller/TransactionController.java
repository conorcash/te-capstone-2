package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.Exceptions.AccountNotFound;
import com.techelevator.tenmo.Exceptions.InsufficientBalance;
import com.techelevator.tenmo.Exceptions.InvalidAmount;
import com.techelevator.tenmo.dao.JdbcTransactionDao;
import com.techelevator.tenmo.dao.TransactionsDao;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

public class TransactionController {
    private TransactionsDao transactionsDao;

    public TransactionController () {
        transactionsDao = new JdbcTransactionDao();
    }

    @RequestMapping(path = "/accounts/{id}/transactions", method = RequestMethod.GET)
    public List<Transaction> listAccounts(@PathVariable int accountId) {
        return transactionsDao.listAccountTransactions(accountId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/accounts/{id}/transactions/", method = RequestMethod.POST)
    public int add(@Valid @RequestBody Transaction transaction,@PathVariable int accountId)
            throws InsufficientBalance, InvalidAmount, AccountNotFound {
        return transactionsDao.create(transaction,accountId);
    }
}
