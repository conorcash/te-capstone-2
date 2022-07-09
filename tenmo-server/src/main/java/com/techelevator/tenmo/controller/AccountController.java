package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.Exceptions.AccountNotFound;
import com.techelevator.tenmo.Exceptions.InvalidEntry;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping(path = "/accounts")
public class AccountController {
    private AccountDao accountDao;
    private UserDao userDao;

    public AccountController(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<Account> listAccounts() {
        return accountDao.listAccounts();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public Account getAccountById(@PathVariable("id") int accountId, Principal principal) throws AccountNotFound, InvalidEntry{
        if (accountDao.findByUserId(userDao.findIdByUsername(principal.getName())).getAccountId() == accountId) {
            return accountDao.findByAccountId(accountId);
        }
        throw new InvalidEntry();
    }
}
