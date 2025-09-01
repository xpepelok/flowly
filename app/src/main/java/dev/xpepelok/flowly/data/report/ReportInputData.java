package dev.xpepelok.flowly.data.report;

import dev.xpepelok.bank.data.model.BankLegalUser;
import dev.xpepelok.bank.data.model.BankUser;
import dev.xpepelok.bank.transaction.model.Transaction;

import java.util.List;

public record ReportInputData(
        List<Transaction> transactions,
        List<BankUser> retailUsers,
        List<BankLegalUser> corporateUsers
) {}
