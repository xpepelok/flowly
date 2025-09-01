package dev.xpepelok.flowly.api;

import dev.xpepelok.flowly.data.dto.Transaction;
import dev.xpepelok.bank.service.transaction.TransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionController {
    TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Boolean> createTransaction(@RequestBody Transaction body) {
        body.setTransactionDate(System.currentTimeMillis());
        var result = transactionService.createTransaction(body.asModel());
        if (result) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping("/{iban}")
    public ResponseEntity<List<Transaction>> getTransactionsByIban(
            @PathVariable String iban,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "100") int limit
    ) {
        var list = transactionService.getTransactions(iban, offset, limit)
                .stream().map(Transaction::new).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/sender/{iban}")
    public ResponseEntity<List<Transaction>> getTransactionsBySender(
            @PathVariable String iban,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "100") int limit
    ) {
        var list = transactionService.getTransactionsBySender(iban, offset, limit)
                .stream().map(Transaction::new).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/recipient/{iban}")
    public ResponseEntity<List<Transaction>> getTransactionsByRecipient(
            @PathVariable String iban,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "100") int limit
    ) {
        var list = transactionService.getTransactionsByRecipient(iban, offset, limit)
                .stream()
                .map(Transaction::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(@RequestParam int offset, @RequestParam int limit) {
        var list = transactionService.getTransactions(offset, limit)
                .stream()
                .map(Transaction::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> count() {
        return ResponseEntity.ok(transactionService.getTransactionsAmount());
    }
}
