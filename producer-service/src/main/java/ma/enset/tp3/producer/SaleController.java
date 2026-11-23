package ma.enset.tp3.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import ma.enset.tp3.common.SaleEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
public class SaleController {
    private final StreamBridge bridge;
    private final ObjectMapper mapper;
    public SaleController(StreamBridge bridge, ObjectMapper mapper) {
        this.bridge = bridge;
        this.mapper = mapper;
    }

    public record NewSale(
            @NotBlank String product,
            @NotBlank String region,
            @Min(1) int quantity,
            @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal unitPrice) { }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> publish(@Valid @RequestBody NewSale body) throws JsonProcessingException {
        SaleEvent sale = new SaleEvent(UUID.randomUUID().toString(), body.product().trim(),
                body.region().trim(), body.quantity(), body.unitPrice(), Instant.now().toEpochMilli());
        Message<String> message = MessageBuilder.withPayload(mapper.writeValueAsString(sale))
                .setHeader("contentType", "text/plain").build();
        if (!bridge.send("sales-out-0", message)) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Kafka broker unavailable"));
        }
        return ResponseEntity.accepted().body(Map.of("status", "queued", "sale", sale));
    }
}
