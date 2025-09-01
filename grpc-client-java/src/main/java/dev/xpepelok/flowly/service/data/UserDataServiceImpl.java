package dev.xpepelok.flowly.service.data;

import com.google.protobuf.ByteString;
import dev.xpepelok.bank.data.grpc.*;
import dev.xpepelok.flowly.data.grpc.*;
import dev.xpepelok.flowly.data.model.BankLegalUser;
import dev.xpepelok.flowly.data.model.BankUser;
import dev.xpepelok.flowly.data.model.PersonalOwnerData;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserDataServiceImpl implements UserDataService {
    BankDataServiceGrpc.BankDataServiceBlockingStub stub;

    public UserDataServiceImpl(ManagedChannel managedChannel) {
        this.stub = BankDataServiceGrpc.newBlockingStub(managedChannel);
    }

    @Override
    public Optional<BankUser> getBankUser(byte[] registrationId) {
        try {
            var resp = stub.getBankUser(GetBankUserRequest.newBuilder()
                    .setUserID(ByteString.copyFrom(registrationId)).build());
            return resp.hasUser() ? Optional.of(resp.getUser()) : Optional.empty();
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) return Optional.empty();
            throw e;
        }
    }

    @Override
    public Optional<BankLegalUser> getBankLegalUser(byte[] registrationId) {
        try {
            var resp = stub.getBankLegalUser(GetBankLegalUserRequest.newBuilder()
                    .setUserID(ByteString.copyFrom(registrationId)).build());
            return resp.hasUser() ? Optional.of(resp.getUser()) : Optional.empty();
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) return Optional.empty();
            throw e;
        }
    }

    @Override
    public BankUser createBankUser(byte[] registrationId, PersonalOwnerData ownerData) {
        return stub.createBankUser(CreateBankUserRequest.newBuilder()
                .setUserID(ByteString.copyFrom(registrationId))
                .setOwnerData(ownerData)
                .build()).getUser();
    }

    @Override
    public BankLegalUser createBankLegalUser(byte[] registrationId, PersonalOwnerData ownerData, int companyId, String companyName) {
        return stub.createBankLegalUser(CreateBankLegalUserRequest.newBuilder()
                .setUserID(ByteString.copyFrom(registrationId))
                .setOwnerData(ownerData)
                .setCompanyID(companyId)
                .setCompanyName(companyName)
                .build()).getUser();
    }

    @Override
    public BankUser getOrCreateBankUser(byte[] registrationId, PersonalOwnerData ownerData) {
        var id = ByteString.copyFrom(registrationId);
        try {
            var resp = stub.getBankUser(GetBankUserRequest.newBuilder().setUserID(id).build());
            if (!resp.hasUser()) throw Status.NOT_FOUND.asRuntimeException();
            return resp.getUser();
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                return createBankUser(registrationId, ownerData);
            }
            throw e;
        }
    }

    @Override
    public BankLegalUser getOrCreateBankLegalUser(byte[] registrationId, PersonalOwnerData ownerData, int companyId, String companyName) {
        var id = ByteString.copyFrom(registrationId);
        try {
            var resp = stub.getBankLegalUser(GetBankLegalUserRequest.newBuilder().setUserID(id).build());
            if (!resp.hasUser()) throw Status.NOT_FOUND.asRuntimeException();
            return resp.getUser();
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                return createBankLegalUser(registrationId, ownerData, companyId, companyName);
            }
            throw e;
        }
    }

    @Override
    public List<BankUser> getUsersByIban(String query, int offset, int limit) {
        var resp = stub.getBankUsersByIban(GetBankUsersByIbanRequest.newBuilder()
                .setQuery(query == null ? "" : query)
                .setOffset(Math.max(0, offset))
                .setLimit(Math.max(0, limit))
                .build());
        return resp.getUsersList();
    }

    @Override
    public List<BankUser> getUsersByLastName(String query, int offset, int limit) {
        var resp = stub.getBankUsersByLastName(GetBankUsersByLastNameRequest.newBuilder()
                .setQuery(query == null ? "" : query)
                .setOffset(Math.max(0, offset))
                .setLimit(Math.max(0, limit))
                .build());
        return resp.getUsersList();
    }

    @Override
    public int getUsersAmount() {
        return stub.getUsersAmount(GetUsersAmountRequest.newBuilder().build()).getAmount();
    }
}
