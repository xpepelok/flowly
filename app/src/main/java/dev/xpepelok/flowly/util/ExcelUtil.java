package dev.xpepelok.flowly.util;

import dev.xpepelok.flowly.data.excel.ExcelStyles;
import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.regex.Pattern;

@UtilityClass
public final class ExcelUtil {
    private static final Pattern ILLEGAL_SHEET_CHARS = Pattern.compile("[:\\\\/?*\\[\\]]");
    private static final Pattern EDGE_QUOTES = Pattern.compile("^'+|'+$");

    public static void header(Row row, ExcelStyles excelStyles, String... cols) {
        for (int i = 0; i < cols.length; i++) {
            var c = row.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(excelStyles.getHeader());
        }
    }

    public static void header(Row row, ExcelStyles excelStyles, int offset, String... cols) {
        for (int i = 0; i < cols.length; i++) {
            var c = row.createCell(offset + i);
            c.setCellValue(cols[i]);
            c.setCellStyle(excelStyles.getHeader());
        }
    }

    public static void autosize(Sheet sheet, int cols) {
        for (int i = 0; i < cols; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public static String sanitizeSheetName(String name) {
        if (name == null || name.isBlank()) return "Sheet";

        String cleaned = ILLEGAL_SHEET_CHARS.matcher(name).replaceAll("-");

        cleaned = EDGE_QUOTES.matcher(cleaned).replaceAll("");

        if (cleaned.isBlank()) cleaned = "Sheet";

        return cleaned.length() > 31 ? cleaned.substring(0, 31) : cleaned;
    }

    public static String nz(String string) {
        return string == null ? "" : string;
    }

    public static boolean isBlank(String string) {
        return string == null || string.isBlank();
    }
}
