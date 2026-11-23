package ma.enset.tp3.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.enset.tp3.common.SaleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class SaleConsumer {
    private static final Logger logger = LoggerFactory.getLogger(SaleConsumer.class);
    private final ObjectMapper mapper;
    private final AtomicLong received = new AtomicLong();
    public SaleConsumer(ObjectMapper mapper) { this.mapper = mapper; }

    @Bean
    public Consumer<String> logSales() {
        return json -> {
            try {
                SaleEvent sale = mapper.readValue(json, SaleEvent.class);
                long n = received.incrementAndGet();
                logger.info("Sale #{} | id={} product={} region={} qty={} amount={} MAD",
                        n, sale.eventId(), sale.product(), sale.region(), sale.quantity(), sale.total());
            } catch (Exception ex) {
                logger.error("Invalid sales event: {}", ex.getMessage());
            }
        };
    }
    public long receivedCount() { return received.get(); }
}
