package dev.xpepelok.flowly.service.report.export;

import dev.xpepelok.flowly.data.report.ReportInputData;
import dev.xpepelok.flowly.data.report.ReportOptionData;
import dev.xpepelok.flowly.report.ReportBuilder;
import dev.xpepelok.flowly.service.report.ReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportExportServiceImpl implements ReportExportService {
    private static final ZoneId REPORT_ZONE = ZoneId.of("Europe/Kyiv");
    ReportService reportService;
    ReportBuilder manager;

    @Override
    public byte[] exportToBytes(String iban) {
        var input = this.reportService.buildForIban(iban);
        var options = ReportOptionData.of(REPORT_ZONE);
        try {
            return this.exportToBytes(input, options);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] exportToBytes(ReportInputData input, ReportOptionData options) {
        try (Workbook wb = manager.buildWorkbook(input, options); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            wb.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Unable to export report to bytes", e);
        }
    }
}

