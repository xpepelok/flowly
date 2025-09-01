package dev.xpepelok.flowly.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

@UtilityClass
public class FormatUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public static String getFormattedTimestamp(TemporalAccessor temporalAccessor) {
        return DATE_TIME_FORMATTER.format(temporalAccessor);
    }

    public static String formatYearMonth(LocalDate date) {
        return YEAR_MONTH_FORMATTER.format(date);
    }
}
