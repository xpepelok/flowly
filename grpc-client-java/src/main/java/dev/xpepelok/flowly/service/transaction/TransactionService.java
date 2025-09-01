package dev.xpepelok.flowly.service.transaction;

import dev.xpepelok.flowly.transaction.model.Transaction;

import java.util.List;

public interface TransactionService {
    boolean createTransaction(Transaction transaction);

    List<Transaction> getTransactionsBySender(String iban, int offset, int limit);

    List<Transaction> getTransactionsByRecipient(String iban, int offset, int limit);

    List<Transaction> getTransactions(String iban, int offset, int limit);

    List<Transaction> getTransactions(int offset, int limit);

    int getTransactionsAmount();
}
