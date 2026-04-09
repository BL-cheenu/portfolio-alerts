package com.ch.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * US9 - Kafka Consumer
 * Listens to "stock-prices" topic
 * Updates in-memory StockPriceCache with latest prices
 */
@Component
public class StockPriceConsumer {

    private static final Logger log = LoggerFactory.getLogger(StockPriceConsumer.class);

    @Autowired
    private StockPriceCache stockPriceCache;

    @Autowired
    private ObjectMapper objectMapper;

    // ── Listen to stock-prices topic ─────────────────────────────────────
    @KafkaListener(topics = "stock-prices", groupId = "portfolio-alerts-group")
    public void consumePriceUpdate(String message) {
        try {
            StockPriceMessage priceMessage = objectMapper.readValue(message, StockPriceMessage.class);
            stockPriceCache.updatePrice(priceMessage);
            log.info("Kafka received: {} = {}", priceMessage.getSymbol(), priceMessage.getCurrentPrice());
        } catch (Exception e) {
            log.error("Kafka consume failed: {}", e.getMessage());
        }
    }
}