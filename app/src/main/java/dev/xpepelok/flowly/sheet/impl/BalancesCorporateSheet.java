package dev.xpepelok.flowly.sheet.impl;

import dev.xpepelok.bank.data.model.BalanceData;
import dev.xpepelok.bank.data.model.BankUser;
import dev.xpepelok.bank.data.model.CompanyData;
import dev.xpepelok.bank.data.model.PersonalOwnerData;
import dev.xpepelok.flowly.data.report.ReportContext;
import dev.xpepelok.flowly.sheet.SheetGenerator;
import dev.xpepelok.flowly.data.excel.ExcelStyles;
import dev.xpepelok.flowly.util.SerializationUtil;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

import static dev.xpepelok.flowly.util.ExcelUtil.*;

@Component
@Order(30)
public class BalancesCorporateSheet implements SheetGenerator {
    private static final String SHEET_NAME = "Balances — Corporate";
    private static final String[] COLUMNS = {
            "CompanyID","Company Name","Company RegID","User RegID", "IBAN",
            "Owner First","Owner Middle","Owner Last", "Owner Born Date",
            "Owner Address", "Balance","Hold","Available"
    };

    @Override
    public void generate(ReportContext context) {
        var workbook = context.getWorkbook();
        var styles = context.getStyles();

        var sheet = workbook.createSheet(SHEET_NAME);
        var headerRow = sheet.createRow(0);
        header(headerRow, styles, COLUMNS);

        int rowIndex = 1;
        for (var corporateUser : context.getInput().corporateUsers()) {
            var row = sheet.createRow(rowIndex++);

            var companyData = corporateUser.getCompanyData();
            BankUser bankUser = corporateUser.getMainData();
            PersonalOwnerData ownerData = bankUser.getOwnerData();
            var balanceData = bankUser.getBalanceData();

            writeCompanyData(row, companyData);
            writeUserData(row, bankUser);
            writeOwnerData(row, ownerData, styles);
            writeBalanceData(row, balanceData, context);
        }

        autosize(sheet, COLUMNS.length);
    }

    private void writeCompanyData(Row row, CompanyData companyData) {
        row.createCell(0).setCellValue(companyData.getCompanyID());
        row.createCell(1).setCellValue(nz(companyData.getCompanyName()));
        row.createCell(2).setCellValue(SerializationUtil.getUUID(companyData.getRegistrationID().toByteArray()).toString());
    }

    private void writeUserData(Row row, BankUser bankUser) {
        row.createCell(3).setCellValue( SerializationUtil.getUUID(bankUser.getRegistrationID().toByteArray()).toString());
        row.createCell(4).setCellValue(nz(bankUser.getIban()));
    }

    private void writeOwnerData(Row row, PersonalOwnerData ownerData, ExcelStyles styles) {
        row.createCell(5).setCellValue(ownerData == null ? "" : nz(ownerData.getFirstName()));
        row.createCell(6).setCellValue(ownerData == null ? "" : nz(ownerData.getMiddleName()));
        row.createCell(7).setCellValue(ownerData == null ? "" : nz(ownerData.getLastName()));

        var bornCell = row.createCell(8);
        if (ownerData != null && ownerData.getBornDate() != 0L) {
            bornCell.setCellValue(Date.from(Instant.ofEpochMilli(ownerData.getBornDate())));
            bornCell.setCellStyle(styles.getDateOnly());
        } else {
            bornCell.setCellValue("");
        }

        row.createCell(9).setCellValue(ownerData == null ? "" : nz(ownerData.getAddress()));
    }

    private void writeBalanceData(Row row, BalanceData balanceData, ReportContext context) {
        var optionData = context.getOptionData();
        var styles = context.getStyles();

        var balance = optionData.balanceExtractor().apply(balanceData);
        var hold = optionData.holdExtractor().apply(balanceData);
        var available = balance.subtract(hold);

        var balanceCell = row.createCell(10);
        balanceCell.setCellValue(balance.doubleValue());
        balanceCell.setCellStyle(styles.getMoney());

        var holdCell = row.createCell(11);
        holdCell.setCellValue(hold.doubleValue());
        holdCell.setCellStyle(styles.getMoney());

        var availableCell = row.createCell(12);
        availableCell.setCellValue(available.doubleValue());
        availableCell.setCellStyle(styles.getMoney());
    }

    @Override
    public String name() {
        return SHEET_NAME;
    }
}
