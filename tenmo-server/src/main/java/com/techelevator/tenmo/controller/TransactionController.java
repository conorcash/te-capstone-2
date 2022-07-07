package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.Exceptions.AccountNotFound;
import com.techelevator.tenmo.Exceptions.InsufficientBalance;
import com.techelevator.tenmo.Exceptions.InvalidAmount;
import com.techelevator.tenmo.Exceptions.InvalidEntry;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcTransactionDao;
import com.techelevator.tenmo.dao.TransactionsDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class TransactionController {
    private TransactionsDao transactionsDao;
    private UserDao userDao;
    private AccountDao accountDao;

    public TransactionController (TransactionsDao transactionsDao, UserDao userDao, AccountDao accountDao) {
        this.transactionsDao = transactionsDao;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/accounts/{id}/transactions", method = RequestMethod.GET)
    public List<Transaction> listAccountTransactions(@PathVariable int accountId) {
        return transactionsDao.listAccountTransactions(accountId);
    }

    @RequestMapping(path = "/transactions/{id}", method = RequestMethod.GET)
    public Transaction getTransaction(@PathVariable ("id") int transactionId, Principal principal)
            throws AccountNotFound, InvalidEntry {
        int currentUserAccountId =
                accountDao.findByUserId(userDao.findIdByUsername(principal.getName())).getAccountId();
        if (transactionsDao.findTransactionById(transactionId).getRecipientId() == currentUserAccountId ||
                transactionsDao.findTransactionById(transactionId).getSenderId() == currentUserAccountId) {
            return transactionsDao.findTransactionById(transactionId);
        }
        throw new InvalidEntry();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    public Transaction add(@RequestBody Transaction transaction,Principal principal)
            throws InsufficientBalance, InvalidAmount, AccountNotFound {
        int accountId = accountDao.findByUserId(userDao.findIdByUsername(principal.getName())).getAccountId();
        return transactionsDao.create(transaction,accountId);
    }

//    @RequestMapping(path = "/send/{amount}/to/{id}", method = RequestMethod.PUT)
//    public BigDecimal sendMoney(@PathVariable ("amount") BigDecimal amount, @PathVariable ("id") int accountId, Principal principal) throws AccountNotFound, InsufficientBalance, InvalidAmount, InvalidEntry {
//        int currentUserAccountId = accountDao.findAccountByUserId(userDao.findIdByUsername(principal.getName())).getAccountId();
//
//        if (accountId == currentUserAccountId) {
//            throw new InvalidEntry();
//        }
//        accountDao.withdraw(amount, currentUserAccountId);
//        return accountDao.deposit(amount, accountId);
//    }
}
