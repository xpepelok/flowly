package dev.xpepelok.flowly.sheet.impl;

import dev.xpepelok.flowly.data.report.ReportContext;
import dev.xpepelok.flowly.sheet.SheetGenerator;
import dev.xpepelok.bank.transaction.model.Transaction;
import dev.xpepelok.flowly.util.FormatUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static dev.xpepelok.flowly.util.ExcelUtil.*;

@Component
@Order(50)
public class MonthlyTransactionsSheet implements SheetGenerator {
    private static final String SHEET_NAME_PREFIX = "TX ";
    private static final String[] COLUMNS = {"Transaction Date", "Sender", "Recipient", "Sum"};

    @Override
    public void generate(ReportContext context) {
        for (var ym : context.getMonths()) {
            var sheetName = sanitizeSheetName(SHEET_NAME_PREFIX + FormatUtil.formatYearMonth(ym.atDay(1)));
            var sheet = context.getWorkbook().createSheet(sheetName);

            createHeader(sheet, context);
            var transactions = context.getTransactionsByMonth().get(ym);
            transactions.sort(Comparator.comparingLong(Transaction::getTransactionDate));

            var subtotal = writeTransactions(sheet, transactions, context);
            writeTotal(sheet, transactions.size() + 1, subtotal, context);

            autosize(sheet, COLUMNS.length);
        }
    }

    private void createHeader(Sheet sheet, ReportContext context) {
        var headerRow = sheet.createRow(0);
        header(headerRow, context.getStyles(), COLUMNS);
    }

    private BigDecimal writeTransactions(Sheet sheet, List<Transaction> transactions, ReportContext context) {
        var styles = context.getStyles();
        var sumExtractor = context.getOptionData().txSumExtractor();

        int rowIndex = 1;
        BigDecimal subtotal = BigDecimal.ZERO;

        for (var tx : transactions) {
            Row row = sheet.createRow(rowIndex++);

            var dateCell = row.createCell(0);
            dateCell.setCellValue(Date.from(Instant.ofEpochMilli(tx.getTransactionDate())));
            dateCell.setCellStyle(styles.getDateTime());

            row.createCell(1).setCellValue(nz(tx.getSender()));
            row.createCell(2).setCellValue(nz(tx.getRecipient()));

            var sum = sumExtractor.apply(tx);
            var sumCell = row.createCell(3);
            sumCell.setCellValue(sum.doubleValue());
            sumCell.setCellStyle(styles.getMoney());

            subtotal = subtotal.add(sum);
        }
        return subtotal;
    }

    private void writeTotal(Sheet sheet, int rowIndex, BigDecimal subtotal, ReportContext context) {
        var styles = context.getStyles();
        var totalRow = sheet.createRow(rowIndex);

        var labelCell = totalRow.createCell(2);
        labelCell.setCellValue("Total:");
        labelCell.setCellStyle(styles.getHeader());

        var totalCell = totalRow.createCell(3);
        totalCell.setCellValue(subtotal.doubleValue());
        totalCell.setCellStyle(styles.getMoney());
    }

    @Override
    public String name() {
        return SHEET_NAME_PREFIX + "YYYY-MM";
    }
}
