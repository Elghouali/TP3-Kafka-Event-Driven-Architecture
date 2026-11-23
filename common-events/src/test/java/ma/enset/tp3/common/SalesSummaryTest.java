package ma.enset.tp3.common;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class SalesSummaryTest {
    @Test void computesRevenueAndCounts() {
        var s = SalesSummary.empty()
                .add(new SaleEvent("a", "Laptop", "Casa", 2, new BigDecimal("4500.00"), 1000))
                .add(new SaleEvent("b", "Laptop", "Rabat", 1, new BigDecimal("4500.00"), 2000));
        assertEquals("Laptop", s.product());
        assertEquals(2, s.orders());
        assertEquals(3, s.units());
        assertEquals(0, new BigDecimal("13500.00").compareTo(s.revenue()));
        assertEquals(2000, s.lastUpdatedEpochMs());
    }
    @Test void rejectsInvalidSale() {
        assertThrows(IllegalArgumentException.class, () ->
                new SaleEvent("x", "Laptop", "Casa", 0, BigDecimal.TEN, 1000));
        assertThrows(IllegalArgumentException.class, () ->
                new SaleEvent("x", "Laptop", "Casa", 1, BigDecimal.ZERO, 1000));
    }
}
