package dev.xpepelok.flowly.sheet.impl;

import dev.xpepelok.flowly.data.report.Aggregates;
import dev.xpepelok.flowly.data.report.ReportContext;
import dev.xpepelok.flowly.sheet.SheetGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(10)
public class SummarySheet implements SheetGenerator {
    private static final String SHEET_NAME = "Summary";

    @Override
    public void generate(ReportContext ctx) {
        var writer = new KeyValueSheetWriter(ctx, SHEET_NAME);

        writer.writeTitle();

        writer.writeKeyValue("Generated At", java.time.LocalDateTime.now(ctx.getOptionData().zone()).toString(), false);
        writer.writeKeyValue("Timezone", ctx.getOptionData().zone().toString(), false);
        writer.writeKeyValue("Retail Users", ctx.getInput().retailUsers().size(), false);
        writer.writeKeyValue("Corporate Users", ctx.getInput().corporateUsers().size(), false);

        writer.writeEmptyRow();
        writer.writeKeyValue("Retail Balance (Total)", ctx.getRetailTotals().balance(), true);
        writer.writeKeyValue("Retail Hold", ctx.getRetailTotals().hold(), true);
        writer.writeKeyValue("Retail Available", ctx.getRetailTotals().available(), true);

        writer.writeEmptyRow();
        writer.writeKeyValue("Corporate Balance (Total)", ctx.getCorporationTotals().balance(), true);
        writer.writeKeyValue("Corporate Hold", ctx.getCorporationTotals().hold(), true);
        writer.writeKeyValue("Corporate Available", ctx.getCorporationTotals().available(), true);

        var allBal  = ctx.getRetailTotals().balance().add(ctx.getCorporationTotals().balance());
        var allHold = ctx.getRetailTotals().hold().add(ctx.getCorporationTotals().hold());

        writer.writeEmptyRow();
        writer.writeKeyValue("All Balances (Total)", allBal, true);
        writer.writeKeyValue("All Holds", allHold, true);
        writer.writeKeyValue("All Available", allBal.subtract(allHold), true);

        if (!ctx.getMonths().isEmpty()) {
            var latestYm = ctx.getLatestMonth();
            var latestAgg = ctx.getMonthAggregator().get(latestYm);

            writer.writeEmptyRow();
            writer.writeKeyValue("Latest Month", latestYm.toString(), false);
            writer.writeKeyValue("Latest Month Tx Count", latestAgg.count(), false);
            writer.writeKeyValue("Latest Month Tx Volume", latestAgg.sum(), true);

            int y = latestYm.getYear();
            var ytdAgg = ctx.getMonthAggregator().entrySet().stream()
                    .filter(e -> e.getKey().getYear() == y)
                    .map(java.util.Map.Entry::getValue)
                    .reduce(new Aggregates.DefaultAggregator(BigDecimal.ZERO, 0L), Aggregates.DefaultAggregator::plus);

            writer.writeKeyValue("YTD Year", y, false);
            writer.writeKeyValue("YTD Tx Count", ytdAgg.count(), false);
            writer.writeKeyValue("YTD Tx Volume", ytdAgg.sum(), true);
        }

        writer.autoSizeColumns();
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class KeyValueSheetWriter {
        private static final String TITLE = "Flowly Reports";
        ReportContext context;
        Sheet sheet;
        @NonFinal
        int currentRow = 0;

        KeyValueSheetWriter(ReportContext context, String sheetName) {
            this.context = context;
            this.sheet = context.getWorkbook().createSheet(sheetName);
        }

        void writeTitle() {
            var row = sheet.createRow(currentRow++);
            var cell = row.createCell(0);
            cell.setCellValue(TITLE);
            cell.setCellStyle(context.getStyles().getTitle());
        }

        void writeKeyValue(String key, Object value, boolean money) {
            var row = sheet.createRow(currentRow++);

            var keyCell = row.createCell(0);
            keyCell.setCellValue(key);
            keyCell.setCellStyle(context.getStyles().getKey());

            var valueCell = row.createCell(1);
            if (value instanceof Number number) {
                valueCell.setCellValue(number.doubleValue());
                if (money) valueCell.setCellStyle(context.getStyles().getMoney());
            } else if (value instanceof java.math.BigDecimal bd) {
                valueCell.setCellValue(bd.doubleValue());
                if (money) valueCell.setCellStyle(context.getStyles().getMoney());
            } else {
                valueCell.setCellValue(String.valueOf(value));
            }
        }

        void writeEmptyRow() {
            currentRow++;
        }

        void autoSizeColumns() {
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
        }
    }

    @Override
    public String name() {
        return SHEET_NAME;
    }
}
