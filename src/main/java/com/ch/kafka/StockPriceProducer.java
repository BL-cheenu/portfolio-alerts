package com.ch.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * US9 - Kafka Producer
 * Sends live stock price updates to "stock-prices" topic
 * Called by scheduled job every 60 seconds
 */
@Component
public class StockPriceProducer {

    private static final Logger log = LoggerFactory.getLogger(StockPriceProducer.class);
    private static final String TOPIC = "stock-prices";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // ── Send price update to Kafka topic ────────────────────────────────
    public void sendPriceUpdate(StockPriceMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(TOPIC, message.getSymbol(), json);
            log.debug("Kafka sent: {} = {}", message.getSymbol(), message.getCurrentPrice());
        } catch (Exception e) {
            log.error("Kafka send failed for {}: {}", message.getSymbol(), e.getMessage());
        }
    }
}