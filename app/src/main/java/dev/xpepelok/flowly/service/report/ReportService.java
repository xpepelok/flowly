package dev.xpepelok.flowly.service.report;

import dev.xpepelok.flowly.data.report.ReportInputData;

public interface ReportService {
    ReportInputData buildForIban(String iban);

    ReportInputData refreshForIban(String iban);
}
