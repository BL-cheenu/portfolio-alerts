package com.ch.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * US9 - In-Memory Stock Price Cache
 *
 * Kafka consumer updates this cache when new prices arrive.
 * MonitorService reads from this cache for real-time portfolio valuation.
 * ConcurrentHashMap — thread-safe for concurrent read/write.
 */
@Component
public class StockPriceCache {

    private static final Logger log = LoggerFactory.getLogger(StockPriceCache.class);

    // symbol → StockPriceMessage (latest price)
    private final Map<String, StockPriceMessage> priceCache = new ConcurrentHashMap<>();

    // ── Update cache (called by Kafka consumer) ──────────────────────────
    public void updatePrice(StockPriceMessage message) {
        priceCache.put(message.getSymbol().toUpperCase(), message);
        log.debug("Cache updated: {} = {}", message.getSymbol(), message.getCurrentPrice());
    }

    // ── Get latest price for symbol ──────────────────────────────────────
    public Optional<StockPriceMessage> getPrice(String symbol) {
        return Optional.ofNullable(priceCache.get(symbol.toUpperCase()));
    }

    // ── Get current price or fallback ────────────────────────────────────
    public double getCurrentPrice(String symbol, double fallback) {
        return getPrice(symbol)
                .map(StockPriceMessage::getCurrentPrice)
                .orElse(fallback);
    }

    // ── Get all cached prices ────────────────────────────────────────────
    public Map<String, StockPriceMessage> getAllPrices() {
        return Map.copyOf(priceCache);
    }

    // ── Cache size ───────────────────────────────────────────────────────
    public int size() {
        return priceCache.size();
    }
}