package dev.xpepelok.flowly.repository.user.retail;

import dev.xpepelok.flowly.data.dto.BankUser;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RetailUserCacheRepositoryImpl implements RetailUserCacheRepository {

    @Override
    @Cacheable(value = "retailUsers", key = "'all'")
    public Optional<List<BankUser>> findRetailUsers() {
        return Optional.empty();
    }

    @Override
    @CachePut(value = "retailUsers", key = "'all'")
    public Optional<List<BankUser>> saveRetailUsers(List<BankUser> users) {
        return Optional.of(List.copyOf(users));
    }
}
