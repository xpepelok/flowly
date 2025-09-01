package dev.xpepelok.flowly.data.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {
    @NotBlank
    String sender;      // IBAN
    @NotBlank
    String recipient;   // IBAN
    @Min(10)
    long sum;           // > 0
    long transactionDate;

    public Transaction(dev.xpepelok.bank.transaction.model.Transaction src) {
        this.sender = src.getSender();
        this.recipient = src.getRecipient();
        this.sum = src.getSum();
        this.transactionDate = src.getTransactionDate();
    }

    public dev.xpepelok.bank.transaction.model.Transaction asModel() {
        var builder = dev.xpepelok.bank.transaction.model.Transaction.newBuilder()
                .setSender(sender)
                .setRecipient(recipient)
                .setSum(sum);

        if (transactionDate > 0) {
            builder.setTransactionDate(transactionDate);
        }
        return builder.build();
    }
}