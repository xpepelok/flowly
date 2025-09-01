package dev.xpepelok.flowly.database.user.legal;

import dev.xpepelok.flowly.data.model.CompanyData;

import java.util.Optional;

public interface LegalUserDataTable {
    Optional<CompanyData> getCompanyData(byte[] uuid);

    boolean saveCompanyData(CompanyData companyData);
}