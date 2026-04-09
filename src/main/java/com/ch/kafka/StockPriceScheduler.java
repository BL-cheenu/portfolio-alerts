package com.ch.kafka;

import com.ch.utils.NseTop50Symbols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * US9 - Scheduled Stock Price Fetcher
 *
 * Every 60 seconds:
 *  1. Fetches latest prices from Alpha Vantage API
 *  2. Publishes each price to Kafka "stock-prices" topic
 *  3. Consumer updates in-memory cache
 *
 * Enable scheduling in main class: @EnableScheduling
 */
@Component
public class StockPriceScheduler {

    private static final Logger log = LoggerFactory.getLogger(StockPriceScheduler.class);

    private static final String ALPHA_URL =
            "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol={symbol}&apikey={key}";

    @Value("${stock.api.key}")
    private String apiKey;

    @Autowired
    private StockPriceProducer producer;

    @Autowired
    private StockPriceCache stockPriceCache;

    private final RestTemplate restTemplate = new RestTemplate();

    // ── Run every 60 seconds ─────────────────────────────────────────────
    @Scheduled(fixedDelay = 60000)
    public void fetchAndPublishPrices() {
        log.info("Scheduler: fetching stock prices...");

        // Top 10 stocks only (free API: 5 req/min, 25/day)
        List<String> symbols = NseTop50Symbols.SYMBOLS.subList(0, 10);

        for (String symbol : symbols) {
            try {
                String avSymbol = symbol.replace(".NS", ".BSE");
                Map<String, Object> response = restTemplate.getForObject(
                        ALPHA_URL, Map.class, avSymbol, apiKey);

                if (response == null || !response.containsKey("Global Quote")) continue;

                Map<String, Object> quote = (Map<String, Object>) response.get("Global Quote");
                if (quote == null || quote.isEmpty()) continue;

                double price  = parseDouble(quote.get("05. price"));
                double change = parseDouble(quote.get("09. change"));
                double changePct = parsePercent(quote.get("10. change percent"));

                StockPriceMessage msg = StockPriceMessage.builder()
                        .symbol(symbol.replace(".NS", ""))
                        .currentPrice(price)
                        .change(change)
                        .changePercent(changePct)
                        .timestamp(System.currentTimeMillis())
                        .build();

                // Publish to Kafka
                producer.sendPriceUpdate(msg);

                // Also update local cache directly (fallback if Kafka not available)
                stockPriceCache.updatePrice(msg);

                log.debug("Scheduler published: {} = {}", msg.getSymbol(), price);

                // Rate limit: 5 req/min
                Thread.sleep(12000);

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.warn("Scheduler failed for {}: {}", symbol, e.getMessage());
            }
        }

        log.info("Scheduler: done. Cache size: {}", stockPriceCache.size());
    }

    private double parseDouble(Object val) {
        if (val == null) return 0.0;
        try { return Double.parseDouble(val.toString().trim()); }
        catch (Exception e) { return 0.0; }
    }

    private double parsePercent(Object val) {
        if (val == null) return 0.0;
        try { return Double.parseDouble(val.toString().replace("%", "").trim()); }
        catch (Exception e) { return 0.0; }
    }
}