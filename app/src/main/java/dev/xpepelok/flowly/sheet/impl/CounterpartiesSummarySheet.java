package dev.xpepelok.flowly.sheet.impl;

import dev.xpepelok.flowly.data.report.Aggregates;
import dev.xpepelok.flowly.data.report.ReportContext;
import dev.xpepelok.flowly.sheet.SheetGenerator;
import dev.xpepelok.bank.transaction.model.Transaction;
import dev.xpepelok.flowly.data.excel.ExcelStyles;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static dev.xpepelok.flowly.util.ExcelUtil.*;

@Component
@Order(60)
public class CounterpartiesSummarySheet implements SheetGenerator {
    private static final String SHEET_NAME = "Counterparties Summary";
    private static final String[] LEFT_COLUMNS  = {"Sender",   "Tx Count", "Tx Volume"};
    private static final String[] RIGHT_COLUMNS = {"Recipient","Tx Count", "Tx Volume"};
    private static final int RIGHT_OFFSET = 5;
    private static final int TOP_N = 50;

    @Override
    public void generate(ReportContext context) {
        var wb = context.getWorkbook();
        var styles = context.getStyles();

        var sheet = wb.createSheet(SHEET_NAME);

        header(sheet.createRow(0), styles, LEFT_COLUMNS);
        header(sheet.getRow(0), styles, RIGHT_OFFSET, RIGHT_COLUMNS);

        var bySender    = aggregateByCounterparty(context, true);
        var byRecipient = aggregateByCounterparty(context, false);

        var topSenders    = topSorted(bySender);
        var topRecipients = topSorted(byRecipient);

        writeSection(sheet, styles, topSenders,1, 0);
        writeSection(sheet, styles, topRecipients,1, RIGHT_OFFSET);

        autosize(sheet, RIGHT_OFFSET + RIGHT_COLUMNS.length);
    }

    private Map<String, Aggregates.DefaultAggregator> aggregateByCounterparty(ReportContext ctx, boolean bySender) {
        Map<String, Aggregates.DefaultAggregator> map = new HashMap<>();
        var extractSum = ctx.getOptionData().txSumExtractor();

        for (Transaction tx : ctx.getInput().transactions()) {
            var key = bySender ? nz(tx.getSender()) : nz(tx.getRecipient());
            var sum = extractSum.apply(tx);
            map.compute(key, (k, agg) ->
                    agg == null ? new Aggregates.DefaultAggregator(sum, 1)
                            : agg.plus(sum, 1));
        }
        return map;
    }

    private List<Entry<String, Aggregates.DefaultAggregator>> topSorted(Map<String, Aggregates.DefaultAggregator> map) {
        return map.entrySet().stream()
                .sorted((a, b) -> b.getValue().sum().compareTo(a.getValue().sum()))
                .limit(TOP_N)
                .toList();
    }

    private void writeSection(Sheet sheet, ExcelStyles styles, List<Entry<String, Aggregates.DefaultAggregator>> rows, int startRow, int colOffset) {
        int r = startRow;

        for (var e : rows) {
            Row row = sheet.getRow(r);
            if (row == null) {
                row = sheet.createRow(r);
            }
            r++;

            row.createCell(colOffset).setCellValue(e.getKey());
            row.createCell(colOffset + 1).setCellValue(e.getValue().count());

            var moneyCell = row.createCell(colOffset + 2);
            moneyCell.setCellValue(e.getValue().sum().doubleValue());
            moneyCell.setCellStyle(styles.getMoney());
        }
    }

    @Override
    public String name() {
        return SHEET_NAME;
    }
}
