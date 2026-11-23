package ma.enset.tp3.common;

import java.math.BigDecimal;

/** Stable JSON contract exchanged on the sales-events topic. */
public record SaleEvent(String eventId, String product, String region, int quantity,
                        BigDecimal unitPrice, long occurredAtEpochMs) {
    public SaleEvent {
        if (eventId == null || eventId.isBlank()) throw new IllegalArgumentException("eventId required");
        if (product == null || product.isBlank()) throw new IllegalArgumentException("product required");
        if (region == null || region.isBlank()) throw new IllegalArgumentException("region required");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        if (unitPrice == null || unitPrice.signum() <= 0) throw new IllegalArgumentException("unitPrice must be positive");
        if (occurredAtEpochMs <= 0) throw new IllegalArgumentException("timestamp required");
    }
    public BigDecimal total() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
}
