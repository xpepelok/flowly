package dev.xpepelok.flowly.service.report;

import dev.xpepelok.flowly.data.report.ReportInputData;
import dev.xpepelok.flowly.repository.transaction.TransactionCacheRepository;
import dev.xpepelok.bank.service.transaction.TransactionService;
import dev.xpepelok.bank.transaction.model.Transaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

import static dev.xpepelok.flowly.util.CollectionUtil.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportServiceImpl implements ReportService {
    TransactionCacheRepository transactionCacheRepository;
    TransactionService transactionService;

    @Override
    public ReportInputData buildForIban(String iban) {
        var sender = transactionCacheRepository.findSenderTransactions(iban).orElseGet(() -> fetchAndCacheSender(iban));
        var recipient = transactionCacheRepository.findRecipientTransactions(iban).orElseGet(() -> fetchAndCacheRecipient(iban));
        var both = transactionCacheRepository.findBothTransactions(iban).orElseGet(() -> combineAndCacheBoth(iban, sender, recipient));

        return new ReportInputData(both, List.of(), List.of());
    }

    @Override
    public ReportInputData refreshForIban(String iban) {
        var sender = fetchAndCacheSender(iban);
        var recipient = fetchAndCacheRecipient(iban);
        var both = combineAndCacheBoth(iban, sender, recipient);

        return new ReportInputData(both, List.of(), List.of());
    }

    private List<Transaction> fetchAndCacheSender(String iban) {
        var list = asSafeList(transactionService.getTransactionsBySender(iban, 0, 0));
        transactionCacheRepository.saveSenderTransactions(iban, list);
        return list;
    }

    private List<Transaction> fetchAndCacheRecipient(String iban) {
        var list = asSafeList(transactionService.getTransactionsByRecipient(iban, 0, 0));
        transactionCacheRepository.saveRecipientTransactions(iban, list);
        return list;
    }

    private List<Transaction> combineAndCacheBoth(
            String iban,
            List<Transaction> sender,
            List<Transaction> recipient) {
        var both = dedupeConcat(sender, recipient);
        transactionCacheRepository.saveBothTransactions(iban, both);
        return both;
    }
}
