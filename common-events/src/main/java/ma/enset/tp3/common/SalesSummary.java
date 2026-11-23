package ma.enset.tp3.common;

import java.math.BigDecimal;

/** Replayed/materialized per-product totals; updated for each valid sale. */
public record SalesSummary(String product, long orders, long units, BigDecimal revenue,
                           long lastUpdatedEpochMs) {
    public SalesSummary {
        if (product == null) throw new IllegalArgumentException("product cannot be null");
        if (revenue == null) throw new IllegalArgumentException("revenue cannot be null");
    }
    public static SalesSummary empty() { return new SalesSummary("", 0, 0, BigDecimal.ZERO, 0); }
    public SalesSummary add(SaleEvent event) {
        return new SalesSummary(event.product(), orders + 1, units + event.quantity(),
                revenue.add(event.total()), event.occurredAtEpochMs());
    }
}
