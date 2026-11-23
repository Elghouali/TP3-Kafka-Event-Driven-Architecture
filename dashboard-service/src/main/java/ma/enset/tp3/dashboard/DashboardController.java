package ma.enset.tp3.dashboard;

import ma.enset.tp3.common.SalesSummary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DashboardController {
    private final AnalyticsStore store;
    public DashboardController(AnalyticsStore store) { this.store = store; }
    @GetMapping("/analytics")
    public List<SalesSummary> summary() { return store.all(); }
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() { return store.subscribe(); }
}
