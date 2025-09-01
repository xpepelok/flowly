package dev.xpepelok.flowly.data.report;

import dev.xpepelok.bank.data.model.BalanceData;
import dev.xpepelok.bank.transaction.model.Transaction;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Objects;
import java.util.function.Function;

public record ReportOptionData(
        ZoneId zone,
        Function<Transaction, BigDecimal> txSumExtractor,
        Function<BalanceData, BigDecimal> balanceExtractor,
        Function<BalanceData, BigDecimal> holdExtractor
) {

    public ReportOptionData {
        Objects.requireNonNull(zone);
        Objects.requireNonNull(txSumExtractor);
        Objects.requireNonNull(balanceExtractor);
        Objects.requireNonNull(holdExtractor);
    }

    public static ReportOptionData of(ZoneId zone) {
        return new ReportOptionData(
                zone,
                t -> BigDecimal.valueOf(t.getSum()),
                b -> BigDecimal.valueOf(b == null ? 0L : b.getBalance()),
                b -> BigDecimal.valueOf(b == null ? 0L : b.getHoldBalance())
        );
    }
}
