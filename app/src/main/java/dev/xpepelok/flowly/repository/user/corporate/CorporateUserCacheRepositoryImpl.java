package dev.xpepelok.flowly.repository.user.corporate;

import dev.xpepelok.flowly.data.dto.BankLegalUser;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CorporateUserCacheRepositoryImpl implements CorporateUserCacheRepository {

    @Override
    @Cacheable(value = "corpUsers", key = "'all'")
    public Optional<List<BankLegalUser>> findCorporateUsers() {
        return Optional.empty();
    }

    @Override
    @CachePut(value = "corpUsers", key = "'all'")
    public Optional<List<BankLegalUser>> saveCorporateUsers(List<BankLegalUser> users) {
        return Optional.of(List.copyOf(users));
    }
}
