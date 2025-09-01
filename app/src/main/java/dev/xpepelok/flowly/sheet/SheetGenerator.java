package dev.xpepelok.flowly.sheet;

import dev.xpepelok.flowly.data.report.ReportContext;

public interface SheetGenerator {
    void generate(ReportContext ctx);

    String name();
}
