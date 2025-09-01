package dev.xpepelok.flowly.sheet.impl;

import dev.xpepelok.flowly.data.report.ReportContext;
import dev.xpepelok.flowly.sheet.SheetGenerator;
import dev.xpepelok.flowly.util.FormatUtil;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

import static dev.xpepelok.flowly.util.ExcelUtil.*;

@Component
@Order(40)
public class TransactionsIndexSheet implements SheetGenerator {
    private static final String SHEET_NAME = "Transactions Index";
    private static final String[] COLUMNS = {"Month","Tx Count","Tx Volume","Sheet"};

    @Override
    public void generate(ReportContext context) {
        if (context.getMonths().isEmpty()) return;

        var sheet = context.getWorkbook().createSheet(SHEET_NAME);
        header(sheet.createRow(0), context.getStyles(), COLUMNS);

        int rowIndex = 1;
        var styles = context.getStyles();

        var aggregators = context.getMonthAggregator().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();

        for (var entryAggregator : aggregators) {
            var yearMonth = entryAggregator.getKey();
            var aggregator = entryAggregator.getValue();

            String monthStr = FormatUtil.formatYearMonth(yearMonth.atDay(1));
            String txSheetName = sanitizeSheetName("TX " + monthStr);

            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(monthStr);
            row.createCell(1).setCellValue(aggregator.count());

            var sumCell = row.createCell(2);
            sumCell.setCellValue(aggregator.sum().doubleValue());
            sumCell.setCellStyle(styles.getMoney());

            var linkCell = row.createCell(3);
            linkCell.setCellFormula(String.format("HYPERLINK(\"#'%s'!A1\",\"Open\")", txSheetName));
            linkCell.setCellStyle(styles.getLink());
        }

        autosize(sheet, COLUMNS.length);
    }

    @Override
    public String name() {
        return SHEET_NAME;
    }
}
