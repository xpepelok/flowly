package dev.xpepelok.flowly.util;

import dev.xpepelok.bank.data.model.BalanceData;
import dev.xpepelok.bank.data.model.BankLegalUser;
import dev.xpepelok.bank.data.model.BankUser;
import dev.xpepelok.flowly.data.report.Aggregates;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@UtilityClass
public final class BalanceUtil {

    public static Aggregates.TotalAggregator sumBalances(
            List<BankUser> users,
            Function<BalanceData, BigDecimal> balance,
            Function<BalanceData, BigDecimal> holdBalance
    ) {
        return sumBalancesInternal(
                users.stream()
                        .map(BankUser::getBalanceData)
                        .toList(),
                balance,
                holdBalance
        );
    }

    public static Aggregates.TotalAggregator sumBalancesCorporate(
            List<BankLegalUser> legalUsers,
            Function<BalanceData, BigDecimal> balance,
            Function<BalanceData, BigDecimal> holdBalance
    ) {
        return sumBalancesInternal(
                legalUsers.stream()
                        .map(legalUser -> legalUser.getMainData().getBalanceData())
                        .toList(),
                balance,
                holdBalance
        );
    }

    private static Aggregates.TotalAggregator sumBalancesInternal(
            List<BalanceData> balances,
            Function<BalanceData, BigDecimal> accountBalance,
            Function<BalanceData, BigDecimal> holdAccountBalance
    ) {
        BigDecimal balance = BigDecimal.ZERO, holdBalance = BigDecimal.ZERO;

        for (var bd : balances) {
            balance = balance.add(Objects.requireNonNullElse(accountBalance.apply(bd), BigDecimal.ZERO));
            holdBalance = holdBalance.add(Objects.requireNonNullElse(holdAccountBalance.apply(bd), BigDecimal.ZERO));
        }

        return new Aggregates.TotalAggregator(balance, holdBalance);
    }
}
