package com.ch.serviceImpl;

import com.ch.dto.StockDto;
import com.ch.service.StockService;
import com.ch.utils.NseTop50Symbols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * US3 - Real-time NSE stock prices using Alpha Vantage API
 *
 * Free API: https://www.alphavantage.co
 * Free tier: 25 requests/day
 * NSE symbol format: RELIANCE.BSE, TCS.BSE
 *
 * NOTE: Free tier = 25 req/day
 * So we fetch top 25 stocks only in free plan
 */
@Service
public class StockServiceImpl implements StockService {

    private static final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

    private static final String ALPHA_VANTAGE_URL =
            "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol={symbol}&apikey={apikey}";

    @Value("${stock.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public StockServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    // ── Fetch NSE Top 25 (free tier = 25 req/day) ───────────────────────
    @Override
    public List<StockDto> getNseTop50Prices() {
        log.info("Fetching NSE stock prices via Alpha Vantage API");

        List<StockDto> stockList = new ArrayList<>();

        // Free tier: 25 requests/day — top 25 stocks fetch பண்றோம்
        List<String> symbols = NseTop50Symbols.SYMBOLS.subList(0, 25);

        for (String symbol : symbols) {
            try {
                StockDto stock = getStockPrice(symbol);
                stockList.add(stock);
                log.debug("Fetched: {} = {}", symbol, stock.getCurrentPrice());

                // Rate limit: 5 req/min — 12 sec wait
                Thread.sleep(12000);

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.warn("Failed for {}: {}", symbol, e.getMessage());
                stockList.add(StockDto.builder()
                        .symbol(symbol.replace(".NS", ""))
                        .companyName(symbol)
                        .currentPrice(0.0)
                        .change(0.0)
                        .changePercent(0.0)
                        .build());
            }
        }

        log.info("Fetched {} stock prices", stockList.size());
        return stockList;
    }

    // ── Fetch single stock ───────────────────────────────────────────────
    @Override
    public StockDto getStockPrice(String symbol) {

        // Alpha Vantage uses BSE suffix for NSE stocks
        String avSymbol = symbol.replace(".NS", ".BSE");

        log.debug("Alpha Vantage call for: {}", avSymbol);

        Map<String, Object> response = restTemplate.getForObject(
                ALPHA_VANTAGE_URL, Map.class, avSymbol, apiKey);

        if (response == null || !response.containsKey("Global Quote")) {
            throw new RuntimeException("No data for: " + avSymbol);
        }

        Map<String, Object> quote = (Map<String, Object>) response.get("Global Quote");

        if (quote == null || quote.isEmpty()) {
            throw new RuntimeException("Empty quote for: " + avSymbol);
        }

        double currentPrice  = getDoubleValue(quote.get("05. price"));
        double change        = getDoubleValue(quote.get("09. change"));
        double changePercent = getDoublePercentValue(quote.get("10. change percent"));

        return StockDto.builder()
                .symbol(symbol.replace(".NS", ""))
                .companyName(symbol.replace(".NS", ""))
                .currentPrice(Math.round(currentPrice * 100.0) / 100.0)
                .change(Math.round(change * 100.0) / 100.0)
                .changePercent(Math.round(changePercent * 100.0) / 100.0)
                .build();
    }

    // ── Helper: safe double ──────────────────────────────────────────────
    private double getDoubleValue(Object value) {
        if (value == null) return 0.0;
        try { return Double.parseDouble(value.toString().trim()); }
        catch (Exception e) { return 0.0; }
    }

    // ── Helper: percent string "0.43%" → 0.43 ───────────────────────────
    private double getDoublePercentValue(Object value) {
        if (value == null) return 0.0;
        try { return Double.parseDouble(value.toString().replace("%", "").trim()); }
        catch (Exception e) { return 0.0; }
    }
}