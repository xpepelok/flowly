package dev.xpepelok.flowly.repository.user.corporate;

import dev.xpepelok.flowly.data.dto.BankLegalUser;

import java.util.List;
import java.util.Optional;

public interface CorporateUserCacheRepository {
    Optional<List<BankLegalUser>> findCorporateUsers();

    Optional<List<BankLegalUser>> saveCorporateUsers(List<BankLegalUser> users);
}
