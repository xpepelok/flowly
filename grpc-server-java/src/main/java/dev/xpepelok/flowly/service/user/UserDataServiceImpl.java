package dev.xpepelok.flowly.service.user;

import com.google.protobuf.ByteString;
import dev.xpepelok.bank.data.grpc.*;
import dev.xpepelok.bank.data.model.*;
import dev.xpepelok.flowly.data.grpc.*;
import dev.xpepelok.flowly.data.model.*;
import dev.xpepelok.flowly.database.user.UserDataTable;
import dev.xpepelok.flowly.database.user.legal.LegalUserDataTable;
import dev.xpepelok.flowly.util.IbanGeneration;
import dev.xpepelok.flowly.util.SerializationUtil;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserDataServiceImpl extends BankDataServiceGrpc.BankDataServiceImplBase {
    LegalUserDataTable legalUserDataTable;
    UserDataTable userDataTable;

    @Override
    public void createBankUser(CreateBankUserRequest request, StreamObserver<CreateBankUserResponse> rsp) {
        var user = formNewBankUser(request.getUserID(), request.getOwnerData());

        this.userDataTable.saveUser(user);

        rsp.onNext(CreateBankUserResponse.newBuilder().setUser(user).build());
        rsp.onCompleted();
    }

    private BankUser formNewBankUser(ByteString registrationID, PersonalOwnerData ownerData) {
        return BankUser.newBuilder()
                .setRegistrationID(registrationID)
                .setOwnerData(ownerData)
                .setBalanceData(BalanceData.newBuilder().setBalance(0).setHoldBalance(0).build())
                .setIban(IbanGeneration.generateIBANFromUUID(
                        SerializationUtil.getUUID(registrationID.toByteArray())))
                .build();
    }

    @Override
    public void createBankLegalUser(CreateBankLegalUserRequest request, StreamObserver<CreateBankLegalUserResponse> rsp) {
        var main = formNewBankUser(request.getUserID(), request.getOwnerData());
        var legal = BankLegalUser.newBuilder()
                .setMainData(main)
                .setCompanyData(CompanyData.newBuilder()
                        .setRegistrationID(request.getUserID())
                        .setCompanyID(request.getCompanyID())
                        .setCompanyName(request.getCompanyName())
                        .build())
                .build();

        this.legalUserDataTable.saveCompanyData(legal.getCompanyData());

        rsp.onNext(CreateBankLegalUserResponse.newBuilder().setUser(legal).build());
        rsp.onCompleted();
    }

    @Override
    public void getBankUser(GetBankUserRequest request, StreamObserver<GetBankUserResponse> rsp) {
        try {
            var userOpt = userDataTable.getUser(request.getUserID().toByteArray());
            var builder = GetBankUserResponse.newBuilder();
            userOpt.ifPresent(builder::setUser);
            rsp.onNext(builder.build());
            rsp.onCompleted();
        } catch (Exception e) {
            rsp.onError(Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e).asException());
        }
    }

    @Override
    public void getBankLegalUser(GetBankLegalUserRequest request, StreamObserver<GetBankLegalUserResponse> rsp) {
        try {
            var key = request.getUserID().toByteArray();
            var companyOpt = legalUserDataTable.getCompanyData(key);
            var userOpt = userDataTable.getUser(key);

            if (companyOpt.isEmpty() || userOpt.isEmpty()) {
                rsp.onNext(GetBankLegalUserResponse.newBuilder().build()); // optional user not set
                rsp.onCompleted();
                return;
            }

            var user = BankLegalUser.newBuilder()
                    .setMainData(userOpt.get())
                    .setCompanyData(companyOpt.get())
                    .build();

            rsp.onNext(GetBankLegalUserResponse.newBuilder().setUser(user).build());
            rsp.onCompleted();
        } catch (Exception e) {
            rsp.onError(Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e).asException());
        }
    }

    @Override
    public void saveBankUser(SaveBankUserRequest request, StreamObserver<SaveBankUserResponse> rsp) {
        var ok = userDataTable.saveUser(request.getUser());
        rsp.onNext(SaveBankUserResponse.newBuilder().setResult(ok).build());
        rsp.onCompleted();
    }

    @Override
    public void saveBankLegalUser(SaveBankLegalUserRequest request, StreamObserver<SaveBankLegalUserResponse> rsp) {
        var u = request.getUser();
        var ok = legalUserDataTable.saveCompanyData(u.getCompanyData()) && userDataTable.saveUser(u.getMainData());
        rsp.onNext(SaveBankLegalUserResponse.newBuilder().setResult(ok).build());
        rsp.onCompleted();
    }

    @Override
    public void getBankUsersByIban(GetBankUsersByIbanRequest req, StreamObserver<GetBankUsersByIbanResponse> rsp) {
        try {
            int offset = Math.max(0, req.getOffset());
            int limit = Math.max(0, req.getLimit());
            var list = userDataTable.getUsersByIban(req.getQuery(), offset, limit);
            rsp.onNext(GetBankUsersByIbanResponse.newBuilder().addAllUsers(list).build());
            rsp.onCompleted();
        } catch (Exception e) {
            rsp.onError(Status.INTERNAL.withDescription(e.getMessage()).withCause(e).asException());
        }
    }

    @Override
    public void getBankUsersByLastName(GetBankUsersByLastNameRequest req, StreamObserver<GetBankUsersByLastNameResponse> rsp) {
        try {
            int offset = Math.max(0, req.getOffset());
            int limit = Math.max(0, req.getLimit());
            var list = userDataTable.getUsersByLastName(req.getQuery(), offset, limit);
            rsp.onNext(GetBankUsersByLastNameResponse.newBuilder().addAllUsers(list).build());
            rsp.onCompleted();
        } catch (Exception e) {
            rsp.onError(Status.INTERNAL.withDescription(e.getMessage()).withCause(e).asException());
        }
    }

    @Override
    public void getUsersAmount(GetUsersAmountRequest req, StreamObserver<GetUsersAmountResponse> rsp) {
        try {
            int amount = userDataTable.getUsersAmount();
            rsp.onNext(GetUsersAmountResponse.newBuilder().setAmount(amount).build());
            rsp.onCompleted();
        } catch (Exception e) {
            rsp.onError(Status.INTERNAL.withDescription(e.getMessage()).withCause(e).asException());
        }
    }
}
