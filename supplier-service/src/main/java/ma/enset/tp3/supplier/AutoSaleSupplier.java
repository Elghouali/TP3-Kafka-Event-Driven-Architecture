package ma.enset.tp3.supplier;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.enset.tp3.common.SaleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Configuration
public class AutoSaleSupplier {
    private static final Logger logger = LoggerFactory.getLogger(AutoSaleSupplier.class);
    private final ObjectMapper mapper;
    private final AtomicInteger cursor = new AtomicInteger();
    public AutoSaleSupplier(ObjectMapper mapper) { this.mapper = mapper; }
    @Bean
    public Supplier<String> supplySales() {
        return () -> {
            String[] products = {"Laptop", "Phone", "Monitor", "Keyboard"};
            String[] regions = {"Casablanca", "Rabat", "Marrakech", "Fes"};
            BigDecimal[] prices = {new BigDecimal("6500.00"), new BigDecimal("2800.00"),
                    new BigDecimal("1600.00"), new BigDecimal("250.00")};
            int index = Math.floorMod(cursor.getAndIncrement(), products.length);
            SaleEvent event = new SaleEvent(UUID.randomUUID().toString(), products[index], regions[index],
                    index % 3 + 1, prices[index], Instant.now().toEpochMilli());
            try {
                String json = mapper.writeValueAsString(event);
                logger.info("Generated demo sale: {}", json);
                return json;
            } catch (Exception e) {
                throw new IllegalStateException("Unable to serialize generated sale", e);
            }
        };
    }
}
