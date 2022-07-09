package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.Exceptions.AccountNotFound;
import com.techelevator.tenmo.Exceptions.InsufficientBalance;
import com.techelevator.tenmo.Exceptions.InvalidAmount;
import com.techelevator.tenmo.Exceptions.InvalidEntry;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransactionDao implements TransactionsDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcTransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transaction> listTransactions() {
        List<Transaction> transactionList = new ArrayList<>();
        String sql = "SELECT transaction_id,sender_id,recipient_id,amount,status " +
                "FROM transaction;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()) {
            transactionList.add(mapRowToTransaction(rowSet));
        }
        return transactionList;
    }

    @Override
    public List<Transaction> listAccountTransactions (int accountId) {
        List<Transaction> accountTransactions = new ArrayList<>();
        String sql = "SELECT transaction_id,sender_id,recipient_id,amount,status,is_request " +
                "FROM transaction " +
                "WHERE sender_id = ? OR recipient_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql,accountId,accountId);
        while (rowSet.next()) {
            accountTransactions.add(mapRowToTransaction(rowSet));
        }
        return accountTransactions;
    }

    @Override
    public Transaction create(Transaction transaction, int creatorId) throws
            InsufficientBalance, InvalidAmount, AccountNotFound, InvalidEntry {
        String sql = "INSERT INTO transaction " +
                "(sender_id,recipient_id,amount,status,is_request) " +
                "VALUES (?,?,?,?,?) " +
                "RETURNING transaction_id;";
        int transactionId = jdbcTemplate.queryForObject(sql,int.class,transaction.getSenderId(),transaction.getRecipientId(),
                transaction.getAmount(),transaction.getStatus(),transaction.isRequest());

        transaction.setTransactionId(transactionId);
        return transaction;
    }

    @Override
    public Transaction findTransactionById(int transactionId) {
        String sql = "SELECT transaction_id,sender_id,recipient_id,amount,status,is_request " +
                "FROM transaction " +
                "WHERE transaction_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql,transactionId);
        if (rowSet.next()) {
            return mapRowToTransaction(rowSet);
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @Override
    public Transaction accept(int transactionId) throws InsufficientBalance, AccountNotFound, InvalidAmount {
        Transaction transaction = findTransactionById(transactionId);
        transaction.setStatus("Approved");
        String sql = "UPDATE transaction " +
                "SET status = ? " +
                "WHERE transaction_id = ? ";
        jdbcTemplate.update(sql,transaction.getStatus(),transaction.getTransactionId());
        return transaction;
    }

    @Override
    public Transaction reject(int transactionId) {
        Transaction transaction = findTransactionById(transactionId);
        transaction.setStatus("Rejected");
        String sql = "UPDATE transaction " +
                "SET status = ? " +
                "WHERE transaction_id = ? ";
        jdbcTemplate.update(sql,transaction.getStatus(),transaction.getTransactionId());
        return transaction;
    }

    private Transaction mapRowToTransaction (SqlRowSet rowSet) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rowSet.getInt("transaction_id"));
        transaction.setSenderId(rowSet.getInt("sender_id"));
        transaction.setRecipientId(rowSet.getInt("recipient_id"));
        transaction.setAmount(rowSet.getBigDecimal("amount"));
        transaction.setStatus(rowSet.getString("status"));
        transaction.setIsRequest(rowSet.getBoolean("is_request"));
        return transaction;
    }
}
