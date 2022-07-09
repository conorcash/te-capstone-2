package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.Exceptions.AccountNotFound;
import com.techelevator.tenmo.Exceptions.InsufficientBalance;
import com.techelevator.tenmo.Exceptions.InvalidAmount;
import com.techelevator.tenmo.Exceptions.InvalidEntry;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionsDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Responses;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class TransactionController {
    private final TransactionsDao transactionsDao;
    private final UserDao userDao;
    private final AccountDao accountDao;

    public TransactionController (TransactionsDao transactionsDao, UserDao userDao, AccountDao accountDao) {
        this.transactionsDao = transactionsDao;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/accounts/{id}/transactions", method = RequestMethod.GET)
    public List<Transaction> listAccountTransactions(@PathVariable ("id") int accountId, Principal principal)
            throws InvalidEntry {
        int currentUserAccountId;
        try {
            currentUserAccountId = accountDao.findByUsername(principal.getName()).getAccountId();
        } catch (AccountNotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (currentUserAccountId == accountId) {
            return transactionsDao.listAccountTransactions(accountId);
        }
        throw new InvalidEntry();
        }

    @RequestMapping(path = "/accounts/{id}/pending_transactions", method = RequestMethod.GET)
    public List<Transaction> listPendingAccountTransactions(@PathVariable ("id") int accountId, Principal principal)
            throws ResponseStatusException {
        int currentUserAccountId;
        try {
            currentUserAccountId = accountDao.findByUsername(principal.getName()).getAccountId();
        } catch (AccountNotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (currentUserAccountId == accountId) {
            List<Transaction> pendingTransactions = new ArrayList<>();
            for (Transaction transaction : transactionsDao.listAccountTransactions(accountId)) {
                if (transaction.getStatus().equals("Pending")) {
                    pendingTransactions.add(transaction);
                }
            }
            return pendingTransactions;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(path = "/transactions/{id}", method = RequestMethod.GET)
    public Transaction getTransaction(@PathVariable ("id") int transactionId, Principal principal)
            throws ResponseStatusException, AccountNotFound {
        int currentUserAccountId = accountDao.findByUsername(principal.getName()).getAccountId();
        if (transactionsDao.findTransactionById(transactionId).getRecipientId() == currentUserAccountId ||
                transactionsDao.findTransactionById(transactionId).getSenderId() == currentUserAccountId) {
            return transactionsDao.findTransactionById(transactionId);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/transactions")
    public Transaction add(@RequestBody @Valid Transaction transaction, Principal principal)
            throws InsufficientBalance, InvalidAmount, AccountNotFound, InvalidEntry {
        int accountId = accountDao.findByUsername(principal.getName()).getAccountId();
        if (transaction.getSenderId() == transaction.getRecipientId()) {
            throw new InvalidEntry();
        }
        transaction.setIsRequest(accountId == transaction.getRecipientId());
        Account sender = accountDao.findByAccountId(transaction.getSenderId());
        Account recipient = accountDao.findByAccountId(transaction.getRecipientId());
        if (transaction.isRequest()) {
            transaction.setStatus("Pending");
        } else if (transaction.getAmount().compareTo(sender.getBalance()) > 0) {
            throw new InsufficientBalance();
        } else {
            transaction.setStatus("Approved");
            accountDao.withdraw(transaction.getAmount(),sender.getAccountId());
            accountDao.deposit(transaction.getAmount(),recipient.getAccountId());
        }
        return transactionsDao.create(transaction,accountId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/transactions/{id}")
    public Transaction transactionApproval (@RequestBody Responses.TransferApproval response,
                                                @PathVariable ("id") int transactionId, Principal principal)
            throws InvalidAmount, InsufficientBalance, InvalidEntry, AccountNotFound {
        int currentUserAccountId =
                accountDao.findByUsername(principal.getName()).getAccountId();
        if (currentUserAccountId == transactionsDao.findTransactionById(transactionId).getSenderId()
                && transactionsDao.findTransactionById(transactionId).getStatus().equals("Pending")) {
            if (response.isApproved()) {
                return approve(transactionId);
            } else {
                return reject(transactionId);
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    public Transaction approve(int transactionId)
            throws InsufficientBalance, AccountNotFound, InvalidAmount {
        Transaction transaction = transactionsDao.findTransactionById(transactionId);
            accountDao.deposit(transaction.getAmount(),transaction.getRecipientId());
            accountDao.withdraw(transaction.getAmount(),transaction.getSenderId());
            return transactionsDao.accept(transactionId);
    }

    public Transaction reject(int transactionId) {
        return transactionsDao.reject(transactionId);
    }
}
