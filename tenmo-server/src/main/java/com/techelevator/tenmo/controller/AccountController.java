package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.Exceptions.AccountNotFound;
import com.techelevator.tenmo.Exceptions.InvalidEntry;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {
    private AccountDao accountDao;
    private UserDao userDao;

    public AccountController(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @RequestMapping(path = "/accounts", method = RequestMethod.GET)
    public List<Account> listAccounts() {
        return accountDao.listAccounts();
    }

    @RequestMapping(path = "accounts/{id}", method = RequestMethod.GET)
    public Account listAccountById(@PathVariable("id") int accountId, Principal principal) throws AccountNotFound, InvalidEntry{
        if (accountDao.findByUserId(userDao.findIdByUsername(principal.getName())).getAccountId() == accountId) {
            return accountDao.findByAccountId(accountId);
        }
        throw new InvalidEntry();
    }

    @RequestMapping(path = "/accounts/{id}/balance", method = RequestMethod.GET)
    public BigDecimal showAccountBalance(@PathVariable ("id") int accountId, Principal principal)
            throws AccountNotFound, InvalidEntry {
        if (accountDao.findByUserId(userDao.findIdByUsername(principal.getName())).getAccountId() == accountId) {
            return accountDao.findByAccountId(accountId).getBalance();
        }
        throw new InvalidEntry();
    }
}
