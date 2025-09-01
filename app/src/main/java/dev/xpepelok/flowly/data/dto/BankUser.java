package dev.xpepelok.flowly.data.dto;

import com.google.protobuf.ByteString;
import dev.xpepelok.flowly.util.SerializationUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class BankUser {
    @NotNull
    UUID registrationID;
    @Valid
    @NotNull
    PersonalOwnerData ownerData;
    @Valid
    @NotNull
    BalanceData balanceData;
    @NotBlank
    String iban;

    public BankUser(dev.xpepelok.bank.data.model.BankUser src) {
        this.registrationID = SerializationUtil.getUUID(src.getRegistrationID().toByteArray());
        this.ownerData = new PersonalOwnerData(src.getOwnerData());
        this.balanceData = new BalanceData(src.getBalanceData());
        this.iban = src.getIban();
    }

    public dev.xpepelok.bank.data.model.BankUser asModel() {
        return dev.xpepelok.bank.data.model.BankUser.newBuilder()
                .setRegistrationID(ByteString.copyFrom(SerializationUtil.getUUID(registrationID)))
                .setOwnerData(ownerData.asModel())
                .setBalanceData(balanceData.asModel())
                .setIban(iban)
                .build();
    }
}
