package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.Exceptions.AccountNotFound;
import com.techelevator.tenmo.Exceptions.InsufficientBalance;
import com.techelevator.tenmo.Exceptions.InvalidAmount;
import com.techelevator.tenmo.model.Account;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
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
    public void create(Account account) throws DataAccessException {
        String sql = "INSERT INTO account " +
                "(user_id,balance) " +
                "VALUES(?,?);";
        jdbcTemplate.update(sql, account.getUserId(), account.getBalance());
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

    @Override
    public Account findByUserId(int userId) throws AccountNotFound {
        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        if (rowSet.next()){
            return mapRowToAccount(rowSet);
        }
        throw new AccountNotFound();
    }

    @Override
    public Account findByUsername(String username) throws AccountNotFound {
        String sql = "SELECT a.account_id, a.user_id, a.balance FROM account AS a " +
                "JOIN tenmo_user AS u ON u.user_id = a.user_id " +
                "WHERE u.username = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return mapRowToAccount(rowSet);
        }
        throw new AccountNotFound();
    }

    private Account mapRowToAccount (SqlRowSet rs) {
        Account account = new Account();
        account.setUserId(rs.getInt("user_id"));
        account.setAccountId(rs.getInt("account_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}
