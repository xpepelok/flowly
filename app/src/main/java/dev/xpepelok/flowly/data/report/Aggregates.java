package dev.xpepelok.flowly.data.report;

import java.math.BigDecimal;

public final class Aggregates {
    private Aggregates() {}

    public record DefaultAggregator(BigDecimal sum, long count) {
        public DefaultAggregator plus(BigDecimal sum, long count) {
            return new DefaultAggregator(this.sum.add(sum), this.count + count);
        }

        public static DefaultAggregator zero() {
            return new DefaultAggregator(BigDecimal.ZERO, 0);

        }
        public static DefaultAggregator plus(DefaultAggregator first, DefaultAggregator second) {
            return new DefaultAggregator(first.sum.add(second.sum), first.count + second.count);
        }
    }

    public record TotalAggregator(BigDecimal balance, BigDecimal hold) {
        public BigDecimal available() {
            return balance.subtract(hold);
        }

        public static TotalAggregator zero() {
            return new TotalAggregator(BigDecimal.ZERO, BigDecimal.ZERO);
        }

        public TotalAggregator plus(TotalAggregator aggregator) {
            return new TotalAggregator(balance.add(aggregator.balance), hold.add(aggregator.hold));
        }
    }
}
