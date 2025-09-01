package dev.xpepelok.flowly.repository.user.retail;

import dev.xpepelok.flowly.data.dto.BankUser;

import java.util.List;
import java.util.Optional;

public interface RetailUserCacheRepository {
    Optional<List<BankUser>> findRetailUsers();

    Optional<List<BankUser>> saveRetailUsers(List<BankUser> users);
}
