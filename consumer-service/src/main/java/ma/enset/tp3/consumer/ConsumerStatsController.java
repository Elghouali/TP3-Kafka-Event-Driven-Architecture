package ma.enset.tp3.consumer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
@RestController
public class ConsumerStatsController {
    private final SaleConsumer service;
    public ConsumerStatsController(SaleConsumer service) { this.service = service; }
    @GetMapping("/api/consumer/stats")
    public Map<String,Object> stats() { return Map.of("service", "kafka-consumer", "receivedSinceStart", service.receivedCount()); }
}
