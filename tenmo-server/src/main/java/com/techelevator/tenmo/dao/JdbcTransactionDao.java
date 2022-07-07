package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.List;

public class JdbcTransactionDao implements TransactionsDao {

    JdbcTemplate jdbcTemplate = new JdbcTemplate();

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

    public List<Transaction> listUserTransactions (int accountId) {
        List<Transaction> userTransactions = new ArrayList<>();
        String sql = "SELECT transaction_id,sender_id,recipient_id,amount,status " +
                "FROM transaction " +
                "WHERE sender_id = ? OR recipient_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql,accountId,accountId);
        while (rowSet.next()) {
            userTransactions.add(mapRowToTransaction(rowSet));
        }
        return userTransactions;
    }

    @Override
    public int create(Transaction transaction) {
        String sql = "INSERT INTO transaction " +
                "(sender_id,recipient_id,amount,status) " +
                "VALUES (?,?,?,?) " +
                "RETURNING transaction_id;";
        return jdbcTemplate.update(sql,transaction.getSenderId(),transaction.getRecipientId(),
                transaction.getAmount(),transaction.getStatus());
    }

    @Override
    public Transaction findTransactionById(int id) {
        String sql = "SELECT transaction_id,sender_id,recipient_id,amount,status " +
                "FROM transaction " +
                "WHERE transaction_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql,id);
        return mapRowToTransaction(rowSet);
    }

    @Override
    public void accept() {
    }

    @Override
    public void reject() {
    }

    private Transaction mapRowToTransaction (SqlRowSet rowSet) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rowSet.getInt("transaction_id"));
        transaction.setSenderId(rowSet.getInt("sender_id"));
        transaction.setRecipientId(rowSet.getInt("recipient_id"));
        transaction.setAmount(rowSet.getBigDecimal("amount"));
        transaction.setStatus(rowSet.getObject("status",String.class));
        return transaction;
    }
}
