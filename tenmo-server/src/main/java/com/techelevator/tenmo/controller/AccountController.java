package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.Exceptions.AccountNotFound;
import com.techelevator.tenmo.Exceptions.InvalidEntry;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;
import java.util.List;

public class AccountController {
    private AccountDao accountDao;

    public AccountController() {
        this.accountDao = new JdbcAccountDao(new JdbcTemplate());
    }

    @RequestMapping(path = "/accounts", method = RequestMethod.GET)
    public List<Account> listAccounts() {
        return accountDao.listAccounts();
    }

    @RequestMapping(path = "accounts/{id}", method = RequestMethod.GET)
    public Account listAccountById(@PathVariable("id") int accountId) throws AccountNotFound {
        return accountDao.findByAccountId(accountId);
    }

    @RequestMapping(path = "/accounts", method = RequestMethod.POST)
    public int addAccount(@RequestBody Account account) throws InvalidEntry {
        return accountDao.create(account);
    }

//    @RequestMapping(path = "/transactions/{id}", method = RequestMethod.POST)
//    public (@RequestBody Account account) {
//        return accountDao.withdraw()
//    }
}
