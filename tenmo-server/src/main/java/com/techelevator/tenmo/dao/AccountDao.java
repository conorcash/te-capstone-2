package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.Exceptions.AccountNotFound;
import com.techelevator.tenmo.Exceptions.InsufficientBalance;
import com.techelevator.tenmo.Exceptions.InvalidAmount;
import com.techelevator.tenmo.Exceptions.InvalidEntry;
import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    BigDecimal withdraw(BigDecimal amount, int accountId) throws InsufficientBalance, AccountNotFound, InvalidAmount;

    BigDecimal deposit(BigDecimal amount, int accountId);

    void create(Account account) throws InvalidEntry;

    List<Account> listAccounts();

    Account findByAccountId (int id) throws AccountNotFound;

    Account findByUserId (int userId) throws AccountNotFound;

    Account findByUsername(String username) throws AccountNotFound;
}
