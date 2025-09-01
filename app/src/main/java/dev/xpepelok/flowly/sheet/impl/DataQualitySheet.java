package dev.xpepelok.flowly.sheet.impl;

import dev.xpepelok.flowly.data.report.ReportContext;
import dev.xpepelok.flowly.sheet.SheetGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static dev.xpepelok.flowly.util.ExcelUtil.isBlank;

@Component
@Order(70)
public class DataQualitySheet implements SheetGenerator {
    private static final String SHEET_NAME = "Data Quality";

    @Override
    public void generate(ReportContext context) {
        var writer = new KeyValueSheetWriter(context, SHEET_NAME);

        writer.writeHeader("Basic Data Quality Checks");
        writeMetrics(writer, buildTransactionMetrics(context));

        writer.writeEmptyRow();

        writer.writeHeader("Retail Users");
        writeMetrics(writer, buildRetailUserMetrics(context));

        writer.writeHeader("Corporate Users");
        writeMetrics(writer, buildCorporateUserMetrics(context));

        writer.autoSizeColumns();
    }

    private List<Metric> buildTransactionMetrics(ReportContext ctx) {
        var input = ctx.getInput();
        var options = ctx.getOptionData();

        long emptySender = input.transactions().stream().filter(t -> isBlank(t.getSender())).count();
        long emptyRecipient = input.transactions().stream().filter(t -> isBlank(t.getRecipient())).count();
        long nonPositiveAmount = input.transactions().stream()
                .filter(t -> options.txSumExtractor().apply(t).compareTo(BigDecimal.ZERO) <= 0)
                .count();
        long datedBeforeEpoch = input.transactions().stream()
                .filter(t -> Instant.ofEpochMilli(t.getTransactionDate())
                        .isBefore(Instant.parse("1970-01-01T00:00:00Z")))
                .count();
        long datedInFuture = input.transactions().stream()
                .filter(t -> Instant.ofEpochMilli(t.getTransactionDate()).isAfter(Instant.now()))
                .count();

        var list = new ArrayList<Metric>(5);
        list.add(new Metric("TX empty sender", emptySender));
        list.add(new Metric("TX empty recipient", emptyRecipient));
        list.add(new Metric("TX non-positive amounts", nonPositiveAmount));
        list.add(new Metric("TX before 1970-01-01", datedBeforeEpoch));
        list.add(new Metric("TX in the future", datedInFuture));
        return list;
    }

    private List<Metric> buildRetailUserMetrics(ReportContext ctx) {
        var input = ctx.getInput();

        long emptyIban = input.retailUsers().stream().filter(u -> isBlank(u.getIban())).count();
        long emptyRegistrationId = input.retailUsers().stream().filter(u -> u.getRegistrationID().isEmpty()).count();

        var list = new ArrayList<Metric>(3);
        list.add(new Metric("Retail empty IBAN", emptyIban));
        list.add(new Metric("Retail empty RegistrationID", emptyRegistrationId));
        return list;
    }

    private List<Metric> buildCorporateUserMetrics(ReportContext ctx) {
        var input = ctx.getInput();

        long emptyCompanyName = input.corporateUsers().stream()
                .filter(u -> isBlank(u.getCompanyData().getCompanyName()))
                .count();
        long emptyUserRegistrationId = input.corporateUsers().stream()
                .filter(u -> u.getMainData().getRegistrationID().isEmpty())
                .count();

        var list = new ArrayList<Metric>(2);
        list.add(new Metric("Corporate empty CompanyName", emptyCompanyName));
        list.add(new Metric("Corporate empty User RegistrationID", emptyUserRegistrationId));
        return list;
    }

    private void writeMetrics(KeyValueSheetWriter writer, List<Metric> metrics) {
        for (var m : metrics) {
            writer.writeMetric(m.label(), m.value());
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class KeyValueSheetWriter {
        ReportContext context;
        Sheet sheet;
        @NonFinal
        int currentRow = 0;

        KeyValueSheetWriter(ReportContext context, String sheetName) {
            this.context = context;
            this.sheet = context.getWorkbook().createSheet(sheetName);
        }

        void writeHeader(String title) {
            var row = sheet.createRow(currentRow++);
            var cell = row.createCell(0);
            cell.setCellValue(title);
            cell.setCellStyle(context.getStyles().getHeader());
        }

        void writeMetric(String key, long value) {
            var row = sheet.createRow(currentRow++);
            var keyCell = row.createCell(0);
            keyCell.setCellValue(key);
            keyCell.setCellStyle(context.getStyles().getKey());
            row.createCell(1).setCellValue((double) value);
        }

        void writeEmptyRow() {
            currentRow++;
        }

        void autoSizeColumns() {
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
        }
    }

    private record Metric(String label, long value) {}

    @Override
    public String name() {
        return SHEET_NAME;
    }
}
