package dev.xpepelok.flowly.service.transaction;

import dev.xpepelok.flowly.transaction.grpc.*;
import dev.xpepelok.bank.transaction.grpc.*;
import dev.xpepelok.flowly.transaction.model.Transaction;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TransactionServiceImpl implements TransactionService {
    BankTransactionServiceGrpc.BankTransactionServiceBlockingStub stub;

    public TransactionServiceImpl(ManagedChannel managedChannel) {
        this.stub = BankTransactionServiceGrpc.newBlockingStub(managedChannel);
    }

    @Override
    public boolean createTransaction(Transaction transaction) {
        try {
            return stub.createTransaction(
                    CreateTransactionRequest.newBuilder()
                            .setTransaction(transaction)
                            .build()
            ).getResult();
        } catch (StatusRuntimeException e) {
            throw new RuntimeException(
                    String.format(
                            "Unable to create transaction from %s to %s",
                            transaction.getSender(),
                            transaction.getRecipient()
                    ), e
            );
        }
    }

    @Override
    public List<Transaction> getTransactionsBySender(String iban, int offset, int limit) {
        try {
            return stub.getTransactionsBySender(
                    GetTransactionBySenderRequest.newBuilder()
                            .setIban(iban)
                            .setOffset(Math.max(0, offset))
                            .setLimit(Math.max(0, limit))
                            .build()
            ).getTransactionsList();
        } catch (StatusRuntimeException e) {
            throw new RuntimeException(
                    String.format("Unable to get transactions by sender for IBAN %s", iban), e);
        }
    }

    @Override
    public List<Transaction> getTransactionsByRecipient(String iban, int offset, int limit) {
        try {
            return stub.getTransactionsByRecipient(
                    GetTransactionByRecipientRequest.newBuilder()
                            .setIban(iban)
                            .setOffset(Math.max(0, offset))
                            .setLimit(Math.max(0, limit))
                            .build()
            ).getTransactionsList();
        } catch (StatusRuntimeException e) {
            throw new RuntimeException(
                    String.format("Unable to get transactions by recipient for IBAN %s", iban), e);
        }
    }

    @Override
    public List<Transaction> getTransactions(String iban, int offset, int limit) {
        try {
            return stub.getTransactionsByIban(
                    GetTransactionsByIbanRequest.newBuilder()
                            .setIban(iban)
                            .setOffset(Math.max(0, offset))
                            .setLimit(Math.max(0, limit))
                            .build()
            ).getTransactionsList();
        } catch (StatusRuntimeException e) {
            throw new RuntimeException(
                    String.format("Unable to get transactions for IBAN %s", iban), e);
        }
    }

    @Override
    public List<Transaction> getTransactions(int offset, int limit) {
        try {
            return stub.getTransactions(
                    GetTransactionsRequest.newBuilder()
                            .setOffset(Math.max(0, offset))
                            .setLimit(Math.max(0, limit))
                            .build()
            ).getTransactionsList();
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Unable to fetch transactions", e);
        }
    }

    @Override
    public int getTransactionsAmount() {
        try {
            return stub.getTransactionsAmount(
                    GetTransactionsAmountRequest.newBuilder().build()
            ).getAmount();
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Unable to get transactions amount", e);
        }
    }
}
