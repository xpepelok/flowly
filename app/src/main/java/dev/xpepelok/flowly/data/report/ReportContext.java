package dev.xpepelok.flowly.data.report;

import dev.xpepelok.bank.transaction.model.Transaction;
import dev.xpepelok.flowly.data.excel.ExcelStyles;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dev.xpepelok.flowly.util.BalanceUtil.sumBalances;
import static dev.xpepelok.flowly.util.BalanceUtil.sumBalancesCorporate;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportContext {
    Workbook workbook = new XSSFWorkbook();
    ExcelStyles styles = new ExcelStyles(workbook);
    ReportInputData input;
    ReportOptionData optionData;
    Map<YearMonth, List<Transaction>> transactionsByMonth;
    List<YearMonth> months;
    Map<YearMonth, Aggregates.DefaultAggregator> monthAggregator;
    YearMonth latestMonth;
    Aggregates.TotalAggregator retailTotals;
    Aggregates.TotalAggregator corporationTotals;

    public ReportContext(ReportInputData input, ReportOptionData optionData) {
        this.input = normalizeInput(input);
        this.optionData = optionData;

        this.transactionsByMonth = groupTransactionsByMonth();
        this.months = initializeMonths();
        this.monthAggregator = aggregateMonths();
        this.latestMonth = findLatestMonth();
        this.retailTotals = sumBalances(this.input.retailUsers(), optionData.balanceExtractor(), optionData.holdExtractor());
        this.corporationTotals = sumBalancesCorporate(this.input.corporateUsers(), optionData.balanceExtractor(), optionData.holdExtractor());
    }

    private ReportInputData normalizeInput(ReportInputData input) {
        return new ReportInputData(
                Objects.requireNonNullElse(input.transactions(), List.of()),
                Objects.requireNonNullElse(input.retailUsers(), List.of()),
                Objects.requireNonNullElse(input.corporateUsers(), List.of())
        );
    }

    private Map<YearMonth, List<Transaction>> groupTransactionsByMonth() {
        ZoneId zoneId = optionData.zone();
        return this.input.transactions().stream()
                .collect(Collectors.groupingBy(
                        t -> YearMonth.from(LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(t.getTransactionDate()), zoneId))
                ));
    }

    private List<YearMonth> initializeMonths() {
        List<YearMonth> m = new ArrayList<>(transactionsByMonth.keySet());
        Collections.sort(m);
        return m;
    }

    private Map<YearMonth, Aggregates.DefaultAggregator> aggregateMonths() {
        Map<YearMonth, Aggregates.DefaultAggregator> result = new LinkedHashMap<>();
        Function<Transaction, BigDecimal> txSum = optionData.txSumExtractor();

        for (YearMonth ym : months) {
            BigDecimal sum = BigDecimal.ZERO;
            long cnt = 0;
            for (var t : transactionsByMonth.get(ym)) {
                sum = sum.add(Objects.requireNonNullElse(txSum.apply(t), BigDecimal.ZERO));
                cnt++;
            }
            result.put(ym, new Aggregates.DefaultAggregator(sum, cnt));
        }

        return result;
    }

    private YearMonth findLatestMonth() {
        return months.isEmpty() ? null : months.getLast();
    }
}
