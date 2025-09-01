package dev.xpepelok.flowly.data.dto;

import com.google.protobuf.ByteString;
import dev.xpepelok.flowly.util.SerializationUtil;
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
public class CompanyData {
    @NotNull
    UUID registrationID;
    @Positive
    int companyID;
    @NotBlank
    String companyName;

    public CompanyData(dev.xpepelok.bank.data.model.CompanyData src) {
        this.registrationID = SerializationUtil.getUUID(src.getRegistrationID().toByteArray());
        this.companyID = src.getCompanyID();
        this.companyName = src.getCompanyName();
    }

    public dev.xpepelok.bank.data.model.CompanyData asModel() {
        return dev.xpepelok.bank.data.model.CompanyData.newBuilder()
                .setRegistrationID(ByteString.copyFrom(SerializationUtil.getUUID(registrationID)))
                .setCompanyID(companyID)
                .setCompanyName(companyName)
                .build();
    }
}