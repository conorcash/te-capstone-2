package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.Exceptions.AccountNotFound;
import com.techelevator.tenmo.Exceptions.InsufficientBalance;
import com.techelevator.tenmo.Exceptions.InvalidAmount;
import com.techelevator.tenmo.model.Account;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal withdraw(BigDecimal amount, int accountId) throws InsufficientBalance,
            AccountNotFound, InvalidAmount {
        if (findByAccountId(accountId).getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalance();
        } else if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmount();
        }
        String sql = "UPDATE account " +
                "SET balance = balance - ? " +
                "WHERE account_id = ? " +
                "RETURNING balance;";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, amount, accountId);
    }

    @Override
    public BigDecimal deposit(BigDecimal amount, int accountId) {
        String sql = "UPDATE account " +
                "SET balance = balance + ? " +
                "WHERE account_id = ? " +
                "RETURNING balance;";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, amount, accountId);
    }

    @Override
    public int create(Account account) throws DataAccessException {
        String sql = "INSERT INTO account " +
                "(user_id,balance) " +
                "VALUES(?,?) " +
                "RETURNING account_id;";
        return jdbcTemplate.update(sql, account.getUserId(), account.getBalance());
    }

    @Override
    public List<Account> listAccounts() {
        List<Account> accountList = new ArrayList<>();
        String sql = "SELECT account_id, user_id, balance FROM account;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()) {
            accountList.add(mapRowToAccount(rowSet));
        }
        return accountList;
    }

    @Override
    public Account findByAccountId(int id) throws AccountNotFound {
        String sql = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (rowSet.next()){
            return mapRowToAccount(rowSet);
        }
        throw new AccountNotFound();
    }

    private Account mapRowToAccount (SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}