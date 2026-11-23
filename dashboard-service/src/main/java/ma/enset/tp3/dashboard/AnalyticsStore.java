package ma.enset.tp3.dashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.enset.tp3.common.SalesSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Component
public class AnalyticsStore {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsStore.class);
    private final Map<String, SalesSummary> summaries = new ConcurrentHashMap<>();
    private final List<SseEmitter> listeners = new CopyOnWriteArrayList<>();
    private final ObjectMapper mapper;
    public AnalyticsStore(ObjectMapper mapper) { this.mapper = mapper; }

    @Bean
    public Consumer<String> dashboardUpdate() {
        return json -> {
            try {
                SalesSummary next = mapper.readValue(json, SalesSummary.class);
                summaries.put(next.product(), next); // latest cumulative state for each product
                for (SseEmitter emitter : listeners) {
                    try { emitter.send(SseEmitter.event().name("summary").data(next)); }
                    catch (IOException | IllegalStateException ex) { listeners.remove(emitter); }
                }
            } catch (Exception ex) {
                log.warn("Ignored invalid analytics update: {}", ex.getMessage());
            }
        };
    }
    public List<SalesSummary> all() {
        return summaries.values().stream().sorted(Comparator.comparing(SalesSummary::revenue).reversed()).toList();
    }
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);
        emitter.onCompletion(() -> listeners.remove(emitter));
        emitter.onTimeout(() -> listeners.remove(emitter));
        emitter.onError(e -> listeners.remove(emitter));
        listeners.add(emitter);
        try { emitter.send(SseEmitter.event().name("ready").data("connected")); }
        catch (IOException ex) { listeners.remove(emitter); }
        return emitter;
    }
}
