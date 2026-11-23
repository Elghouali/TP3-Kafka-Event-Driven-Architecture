package ma.enset.tp3.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.enset.tp3.common.SaleEvent;
import ma.enset.tp3.common.SalesSummary;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.List;
import java.util.function.Function;

@Configuration
public class SalesAnalyticsTopology {
    private static final Logger logger = LoggerFactory.getLogger(SalesAnalyticsTopology.class);
    private final ObjectMapper mapper;
    public SalesAnalyticsTopology(ObjectMapper mapper) { this.mapper = mapper; }

    @Bean
    public Function<KStream<String, String>, KStream<String, String>> aggregateSales() {
        return source -> source
                .flatMap((key, json) -> {
                    try {
                        SaleEvent sale = mapper.readValue(json, SaleEvent.class);
                        return List.of(KeyValue.pair(sale.product(), json));
                    } catch (Exception e) {
                        logger.warn("Skipping invalid sale: {}", e.getMessage());
                        return List.<KeyValue<String, String>>of();
                    }
                })
                .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
                .aggregate(
                        SalesSummary::empty,
                        (product, json, summary) -> {
                            try {
                                return summary.add(mapper.readValue(json, SaleEvent.class));
                            } catch (Exception e) {
                                logger.error("Unexpected error aggregating sale for {}", product, e);
                                return summary;
                            }
                        },
                        Materialized.<String, SalesSummary, KeyValueStore<Bytes, byte[]>>as("sales-per-product-v1")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new JsonSerde<>(SalesSummary.class))
                )
                .toStream()
                .mapValues(summary -> {
                    try {
                        return mapper.writeValueAsString(summary);
                    } catch (Exception e) {
                        throw new IllegalStateException("Cannot serialize analytics summary", e);
                    }
                });
    }
}
