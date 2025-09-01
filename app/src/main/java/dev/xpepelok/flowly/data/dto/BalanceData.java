package dev.xpepelok.flowly.data.dto;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BalanceData {
    @Min(0)
    long balance;
    @Min(0)
    long holdBalance;

    public BalanceData(dev.xpepelok.bank.data.model.BalanceData src) {
        this.balance = src.getBalance();
        this.holdBalance = src.getHoldBalance();
    }

    public dev.xpepelok.bank.data.model.BalanceData asModel() {
        return dev.xpepelok.bank.data.model.BalanceData.newBuilder()
                .setBalance(balance)
                .setHoldBalance(holdBalance)
                .build();
    }
}