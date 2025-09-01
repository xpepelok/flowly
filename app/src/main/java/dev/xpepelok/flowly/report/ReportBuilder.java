package dev.xpepelok.flowly.report;

import dev.xpepelok.flowly.data.report.ReportContext;
import dev.xpepelok.flowly.data.report.ReportInputData;
import dev.xpepelok.flowly.data.report.ReportOptionData;
import dev.xpepelok.flowly.sheet.SheetGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportBuilder {
    List<SheetGenerator> generators;

    public Workbook buildWorkbook(ReportInputData input, ReportOptionData options) {
        var context = new ReportContext(input, options);

        for (var g : generators) {
            g.generate(context);
        }

        if (context.getWorkbook().getNumberOfSheets() == 0) {
            context.getWorkbook().createSheet("No Data");
        }

        return context.getWorkbook();
    }
}
