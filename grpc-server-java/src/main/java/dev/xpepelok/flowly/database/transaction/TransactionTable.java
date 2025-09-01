package dev.xpepelok.flowly.database.transaction;

import dev.xpepelok.flowly.database.CloseableTable;
import dev.xpepelok.flowly.transaction.model.Transaction;

import java.util.List;

public interface TransactionTable extends CloseableTable {
    boolean addTransaction(Transaction transaction);

    long getIncomingSum(String iban);

    long getOutgoingSum(String iban);

    List<Transaction> getTransactionsBySender(String iban, int offset, int limit);

    List<Transaction> getTransactionsByRecipient(String iban, int offset, int limit);

    List<Transaction> getTransactionsForIban(String iban, int offset, int limit);

    List<Transaction> getTransactions(int offset, int limit);

    int getTransactionsAmount();
}
