package dev.xpepelok.flowly.database.user;

import dev.xpepelok.flowly.data.model.BankUser;
import dev.xpepelok.flowly.database.CloseableTable;

import java.util.List;
import java.util.Optional;

public interface UserDataTable extends CloseableTable {
    boolean saveUser(BankUser bankUser);

    Optional<BankUser> getUser(byte[] uuid);

    Optional<BankUser> getUser(String iban);

    List<BankUser> getUsers(int offset, int limit);

    List<BankUser> getUsersByIban(String query, int offset, int limit);

    List<BankUser> getUsersByLastName(String query, int offset, int limit);

    int getUsersAmount();

}
