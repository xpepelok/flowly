package dev.xpepelok.flowly.api;

import dev.xpepelok.flowly.data.dto.BankLegalUser;
import dev.xpepelok.flowly.data.dto.BankUser;
import dev.xpepelok.flowly.data.dto.PersonalOwnerData;
import dev.xpepelok.flowly.data.search.SearchType;
import dev.xpepelok.bank.service.data.UserDataService;
import dev.xpepelok.flowly.service.report.export.ReportExportService;
import dev.xpepelok.flowly.util.SerializationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    ReportExportService reportExportService;
    UserDataService userDataService;

    @GetMapping("/{registrationId}")
    public ResponseEntity<BankUser> getBankUser(@PathVariable String registrationId) {
        byte[] id = SerializationUtil.getUUID(UUID.fromString(registrationId));
        var optionalBankUser = userDataService.getBankUser(id);
        return optionalBankUser.map(bankUser -> {
            return ResponseEntity.ok(new BankUser(bankUser));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BankUser> createBankUser(@RequestBody PersonalOwnerData ownerData) {
        byte[] id = SerializationUtil.getUUID(UUID.randomUUID());
        var responseUser = new BankUser(userDataService.createBankUser(id, ownerData.asModel()));
        return ResponseEntity.ok(responseUser);
    }

    @GetMapping("/legal/{registrationId}")
    public ResponseEntity<BankLegalUser> getBankLegalUser(@PathVariable String registrationId) {
        byte[] id = SerializationUtil.getUUID(UUID.fromString(registrationId));
        var optionalBankLegalUser = userDataService.getBankLegalUser(id);
        return optionalBankLegalUser.map(bankLegalUser -> {
            return ResponseEntity.ok(new BankLegalUser(bankLegalUser));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/legal")
    public ResponseEntity<BankLegalUser> createBankLegalUser(
            @RequestParam int companyId,
            @RequestParam String companyName,
            @RequestBody PersonalOwnerData ownerData
    ) {
        byte[] id = SerializationUtil.getUUID(UUID.randomUUID());
        var bankLegalUser = userDataService.createBankLegalUser(id, ownerData.asModel(), companyId, companyName);
        return ResponseEntity.ok(new BankLegalUser(bankLegalUser));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BankUser>> searchBankUsers(
            @RequestParam SearchType type,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "100") int limit
    ) {
        var users = type == SearchType.IBAN ? userDataService.getUsersByIban(query, offset, limit) :
                userDataService.getUsersByLastName(query, offset, limit);
        return ResponseEntity.ok(users.stream().map(BankUser::new).toList());
    }

    @GetMapping("/report")
    public ResponseEntity<byte[]> exportUsersReport(@RequestParam String iban) {
        try {
            byte[] data = reportExportService.exportToBytes(iban);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report-" + iban + ".xls")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getUsersCount() {
        return ResponseEntity.ok(userDataService.getUsersAmount());
    }
}
