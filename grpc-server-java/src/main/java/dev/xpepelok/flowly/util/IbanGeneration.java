package dev.xpepelok.flowly.util;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public final class IbanGeneration {
    private static final String COUNTRY_CODE = "UA";
    private static final int BANK_CODE_LENGTH = 6;

    public static String generateIBANFromUUID(UUID userUUID) {
        String bankCode = generateNumericString();
        String accountNumber = convertUUIDToAccountNumber(userUUID);
        String checkString = bankCode + accountNumber + countryCodeToNumeric() + "00";
        int checkDigits = 98 - mod97(checkString);

        return COUNTRY_CODE + String.format("%02d", checkDigits) + bankCode + accountNumber;
    }

    private static String convertUUIDToAccountNumber(UUID uuid) {
        String hexUUID = uuid.toString().replace("-", "");
        StringBuilder numericUUID = new StringBuilder();
        for (int i = 0; i < hexUUID.length(); i++) {
            char c = hexUUID.charAt(i);
            numericUUID.append(Character.digit(c, 16));
        }
        return numericUUID.substring(0, 19);
    }

    private static String generateNumericString() {
        StringBuilder sb = new StringBuilder(BANK_CODE_LENGTH);
        for (int i = 0; i < BANK_CODE_LENGTH; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }

    private static int countryCodeToNumeric() {
        return ((COUNTRY_CODE.charAt(0) - 'A' + 10) * 100) + (COUNTRY_CODE.charAt(1) - 'A' + 10);
    }

    private static int mod97(String input) {
        int remainder = 0;
        for (int i = 0; i < input.length(); i++) {
            remainder = (remainder * 10 + (input.charAt(i) - '0')) % 97;
        }
        return remainder;
    }
}
