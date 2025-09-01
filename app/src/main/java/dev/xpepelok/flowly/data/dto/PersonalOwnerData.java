package dev.xpepelok.flowly.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonalOwnerData {
    @NotBlank
    String firstName;
    @NotBlank
    String middleName;
    @NotBlank
    String lastName;
    @Positive
    long bornDate;
    @NotBlank
    String address;

    public PersonalOwnerData(dev.xpepelok.bank.data.model.PersonalOwnerData src) {
        this.firstName = src.getFirstName();
        this.middleName = src.getMiddleName();
        this.lastName = src.getLastName();
        this.bornDate = src.getBornDate();
        this.address = src.getAddress();
    }

    public dev.xpepelok.bank.data.model.PersonalOwnerData asModel() {
        return dev.xpepelok.bank.data.model.PersonalOwnerData.newBuilder()
                .setFirstName(firstName)
                .setMiddleName(middleName)
                .setLastName(lastName)
                .setBornDate(bornDate)
                .setAddress(address)
                .build();
    }
}