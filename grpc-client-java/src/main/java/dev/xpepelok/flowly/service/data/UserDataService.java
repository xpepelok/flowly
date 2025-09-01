package dev.xpepelok.flowly.service.data;

import dev.xpepelok.flowly.data.model.BankLegalUser;
import dev.xpepelok.flowly.data.model.BankUser;
import dev.xpepelok.flowly.data.model.PersonalOwnerData;

import java.util.List;
import java.util.Optional;

public interface UserDataService {
    BankUser createBankUser(byte[] registrationId, PersonalOwnerData ownerData);

    Optional<BankUser> getBankUser(byte[] registrationId);

    BankUser getOrCreateBankUser(byte[] registrationId, PersonalOwnerData ownerData);

    BankLegalUser createBankLegalUser(byte[] registrationId, PersonalOwnerData ownerData, int companyId, String companyName);

    Optional<BankLegalUser> getBankLegalUser(byte[] registrationId);

    BankLegalUser getOrCreateBankLegalUser(byte[] registrationId, PersonalOwnerData ownerData, int companyId, String companyName);

    List<BankUser> getUsersByIban(String query, int offset, int limit);

    List<BankUser> getUsersByLastName(String query, int offset, int limit);

    int getUsersAmount();
}
