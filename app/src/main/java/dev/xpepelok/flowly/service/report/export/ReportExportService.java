package dev.xpepelok.flowly.service.report.export;

public interface ReportExportService {
    byte[] exportToBytes(String iban);
}
