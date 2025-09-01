package dev.xpepelok.flowly.sheet.impl;

import dev.xpepelok.bank.data.model.BalanceData;
import dev.xpepelok.bank.data.model.BankUser;
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
@Order(20)
public class BalancesRetailSheet implements SheetGenerator {
    private static final String SHEET_NAME = "Balances — Retail";
    private static final String[] COLUMNS = {
            "RegistrationID","IBAN","First Name","Middle Name","Last Name",
            "Born Date","Address","Balance","Hold","Available"
    };

    @Override
    public void generate(ReportContext context) {
        var workbook = context.getWorkbook();
        var styles = context.getStyles();

        var sheet = workbook.createSheet(SHEET_NAME);
        var headerRow = sheet.createRow(0);
        header(headerRow, styles, COLUMNS);

        int rowIndex = 1;
        for (var user : context.getInput().retailUsers()) {
            var row = sheet.createRow(rowIndex++);

            var ownerData = user.getOwnerData();
            var balanceData = user.getBalanceData();

            writeIdentity(row, user);
            writeOwner(row, ownerData, styles);
            writeBalances(row, balanceData, context);
        }

        autosize(sheet, COLUMNS.length);
    }

    private void writeIdentity(Row row, BankUser user) {
        row.createCell(0).setCellValue(SerializationUtil.getUUID(user.getRegistrationID().toByteArray()).toString());
        row.createCell(1).setCellValue(nz(user.getIban()));
    }

    private void writeOwner(Row row, PersonalOwnerData owner, ExcelStyles styles) {
        row.createCell(2).setCellValue(nz(owner.getFirstName()));
        row.createCell(3).setCellValue(nz(owner.getMiddleName()));
        row.createCell(4).setCellValue(nz(owner.getLastName()));

        var bornCell = row.createCell(5);
        bornCell.setCellValue(Date.from(Instant.ofEpochMilli(owner.getBornDate())));
        bornCell.setCellStyle(styles.getDateOnly());

        row.createCell(6).setCellValue(nz(owner.getAddress()));
    }

    private void writeBalances(Row row, BalanceData balanceData, ReportContext context) {
        var styles = context.getStyles();
        var options = context.getOptionData();

        var balance = options.balanceExtractor().apply(balanceData);
        var hold = options.holdExtractor().apply(balanceData);
        var available = balance.subtract(hold);

        var balanceCell = row.createCell(7);
        balanceCell.setCellValue(balance.doubleValue());
        balanceCell.setCellStyle(styles.getMoney());

        var holdCell = row.createCell(8);
        holdCell.setCellValue(hold.doubleValue());
        holdCell.setCellStyle(styles.getMoney());

        var availableCell = row.createCell(9);
        availableCell.setCellValue(available.doubleValue());
        availableCell.setCellStyle(styles.getMoney());
    }

    @Override
    public String name() {
        return SHEET_NAME;
    }
}
