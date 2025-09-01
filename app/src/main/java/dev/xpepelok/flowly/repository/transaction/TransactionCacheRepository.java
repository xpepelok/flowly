package dev.xpepelok.flowly.repository.transaction;

import dev.xpepelok.bank.transaction.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionCacheRepository {
    Optional<List<Transaction>> findSenderTransactions(String iban);

    Optional<List<Transaction>> findRecipientTransactions(String iban);

    Optional<List<Transaction>> findBothTransactions(String iban);

    Optional<List<Transaction>> saveSenderTransactions(String iban, List<Transaction> txs);

    Optional<List<Transaction>> saveRecipientTransactions(String iban, List<Transaction> txs);

    Optional<List<Transaction>> saveBothTransactions(String iban, List<Transaction> txs);
}
