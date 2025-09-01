package dev.xpepelok.flowly.data.dto;

import com.google.protobuf.ByteString;
import dev.xpepelok.flowly.util.SerializationUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankLegalUser {
    @NotNull
    UUID registrationID;
    @Valid
    @NotNull
    PersonalOwnerData ownerData;
    @Positive
    int companyID;
    @NotBlank
    String companyName;

    public BankLegalUser(dev.xpepelok.bank.data.model.BankLegalUser src) {
        var main = src.getMainData();
        var company = src.getCompanyData();

        this.registrationID = SerializationUtil.getUUID(main.getRegistrationID().toByteArray());
        this.ownerData = new PersonalOwnerData(main.getOwnerData());
        this.companyID = company.getCompanyID();
        this.companyName = company.getCompanyName();
    }

    public dev.xpepelok.bank.data.model.BankLegalUser asModel() {
        var main = dev.xpepelok.bank.data.model.BankUser.newBuilder()
                .setRegistrationID(ByteString.copyFrom(SerializationUtil.getUUID(registrationID)))
                .setOwnerData(ownerData.asModel())
                .build();

        var company = dev.xpepelok.bank.data.model.CompanyData.newBuilder()
                .setRegistrationID(ByteString.copyFrom(SerializationUtil.getUUID(registrationID)))
                .setCompanyID(companyID)
                .setCompanyName(companyName)
                .build();

        return dev.xpepelok.bank.data.model.BankLegalUser.newBuilder()
                .setMainData(main)
                .setCompanyData(company)
                .build();
    }
}
