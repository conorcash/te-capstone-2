package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransactionDao;
import com.techelevator.tenmo.dao.TransactionsDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class TransactionController {
    private TransactionsDao transactionsDao;

    public TransactionController () {
        transactionsDao = new JdbcTransactionDao();
    }

    @RequestMapping(path = "/accounts", method = RequestMethod.GET)
    public List<Account> listAccounts() {
        return transactionsDao.listTransactions();
    }
}
