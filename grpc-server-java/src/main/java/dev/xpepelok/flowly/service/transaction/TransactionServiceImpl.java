package dev.xpepelok.flowly.service.transaction;

import dev.xpepelok.flowly.database.transaction.TransactionTable;
import dev.xpepelok.flowly.database.user.UserDataTable;
import dev.xpepelok.bank.transaction.grpc.*;
import dev.xpepelok.flowly.transaction.grpc.*;
import dev.xpepelok.flowly.transaction.model.Transaction;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionServiceImpl extends BankTransactionServiceGrpc.BankTransactionServiceImplBase {
    TransactionTable transactionTable;
    UserDataTable userDataTable;

    @Override
    public void createTransaction(CreateTransactionRequest request, StreamObserver<CreateTransactionResponse> rsp) {
        var tx = request.getTransaction();

        var recipientOpt = userDataTable.getUser(tx.getRecipient());
        if (recipientOpt.isEmpty()) {
            sendCreateResult(rsp, false);
            return;
        }

        var senderIBAN = tx.getSender();
        var sender = userDataTable.getUser(senderIBAN).orElseThrow(() ->
                new NullPointerException(String.format("User data doesn't founded in database for sender %s", senderIBAN))
        );

        long incoming = transactionTable.getIncomingSum(sender.getIban());
        long outgoing = transactionTable.getOutgoingSum(sender.getIban());
        long available = incoming - outgoing;

        if (available < tx.getSum()) {
            sendCreateResult(rsp, false);
            return;
        }

        boolean ok = transactionTable.addTransaction(
                Transaction.newBuilder()
                        .setRecipient(tx.getRecipient())
                        .setSender(sender.getIban())
                        .setSum(tx.getSum())
                        .setTransactionDate(tx.getTransactionDate())
                        .build()
        );
        sendCreateResult(rsp, ok);
    }

    @Override
    public void getTransactionsBySender(GetTransactionBySenderRequest req, StreamObserver<GetTransactionBySenderResponse> rsp) {
        var list = transactionTable.getTransactionsBySender(req.getIban(), req.getOffset(), req.getLimit());
        var response = GetTransactionBySenderResponse.newBuilder().addAllTransactions(list).build();
        rsp.onNext(response);
        rsp.onCompleted();
    }

    @Override
    public void getTransactionsByRecipient(GetTransactionByRecipientRequest req, StreamObserver<GetTransactionByRecipientResponse> rsp) {
        var list = transactionTable.getTransactionsByRecipient(req.getIban(), req.getOffset(), req.getLimit());
        var response = GetTransactionByRecipientResponse.newBuilder().addAllTransactions(list).build();
        rsp.onNext(response);
        rsp.onCompleted();
    }

    @Override
    public void getTransactionsByIban(GetTransactionsByIbanRequest req, StreamObserver<GetTransactionsByIbanResponse> rsp) {
        var list = transactionTable.getTransactionsForIban(req.getIban(), req.getOffset(), req.getLimit());
        var response = GetTransactionsByIbanResponse.newBuilder().addAllTransactions(list).build();
        rsp.onNext(response);
        rsp.onCompleted();
    }

    @Override
    public void getTransactions(GetTransactionsRequest req, StreamObserver<GetTransactionsResponse> rsp) {
        var list = transactionTable.getTransactions(req.getOffset(), req.getLimit());
        var response = GetTransactionsResponse.newBuilder().addAllTransactions(list).build();
        rsp.onNext(response);
        rsp.onCompleted();
    }

    @Override
    public void getTransactionsAmount(GetTransactionsAmountRequest req, StreamObserver<GetTransactionsAmountResponse> rsp) {
        int amount = transactionTable.getTransactionsAmount();
        var response = GetTransactionsAmountResponse.newBuilder().setAmount(amount).build();
        rsp.onNext(response);
        rsp.onCompleted();
    }

    private void sendCreateResult(StreamObserver<CreateTransactionResponse> observer, boolean result) {
        observer.onNext(CreateTransactionResponse.newBuilder().setResult(result).build());
        observer.onCompleted();
    }
}
