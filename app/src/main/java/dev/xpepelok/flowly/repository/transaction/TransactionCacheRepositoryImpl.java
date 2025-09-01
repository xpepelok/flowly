package dev.xpepelok.flowly.repository.transaction;

import dev.xpepelok.bank.transaction.model.Transaction;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TransactionCacheRepositoryImpl implements TransactionCacheRepository {

    @Override
    @Cacheable(value = "senderTx", key = "#iban")
    public Optional<List<Transaction>> findSenderTransactions(String iban) {
        return Optional.empty();
    }

    @Override
    @CachePut(value = "senderTx", key = "#iban")
    public Optional<List<Transaction>> saveSenderTransactions(String iban, List<Transaction> txs) {
        return Optional.of(List.copyOf(txs));
    }

    @Override
    @Cacheable(value = "recipientTx", key = "#iban")
    public Optional<List<Transaction>> findRecipientTransactions(String iban) {
        return Optional.empty();
    }

    @Override
    @CachePut(value = "recipientTx", key = "#iban")
    public Optional<List<Transaction>> saveRecipientTransactions(String iban, List<Transaction> txs) {
        return Optional.of(List.copyOf(txs));
    }

    @Override
    @Cacheable(value = "bothTx", key = "#iban")
    public Optional<List<Transaction>> findBothTransactions(String iban) {
        return Optional.empty();
    }

    @Override
    @CachePut(value = "bothTx", key = "#iban")
    public Optional<List<Transaction>> saveBothTransactions(String iban, List<Transaction> txs) {
        return Optional.of(List.copyOf(txs));
    }
}
