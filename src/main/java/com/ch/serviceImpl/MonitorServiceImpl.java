package com.ch.serviceImpl;

import com.ch.dto.CommonDto;
import com.ch.dto.MonitorPortfolioDto;
import com.ch.dto.MonitorStockDto;
import com.ch.entity.AlertEntity;
import com.ch.entity.PortfolioEntity;
import com.ch.entity.UserEntity;
import com.ch.kafka.StockPriceCache;
import com.ch.repository.AlertRepository;
import com.ch.repository.PortfolioRepository;
import com.ch.repository.UserRepository;
import com.ch.service.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * US9 - Real-time Portfolio Monitor Service
 *
 * Key Concepts:
 *  - Kafka consumer updates StockPriceCache with live prices
 *  - Stream API computes per-stock and overall valuation
 *  - Threshold breach check against US8 alert settings
 *
 * Flow:
 *  1. Fetch user portfolio (JPA)
 *  2. Fetch user alerts (JPA)
 *  3. Get live prices from StockPriceCache (Kafka updated)
 *  4. Stream API: compute gainLoss per stock
 *  5. Stream API: compute overall totals
 *  6. Check threshold breach for each stock
 *  7. Return MonitorPortfolioDto
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    private static final Logger log = LoggerFactory.getLogger(MonitorServiceImpl.class);

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockPriceCache stockPriceCache;

    // ── US9 — Monitor full portfolio ─────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public CommonDto<MonitorPortfolioDto> monitorPortfolio(String username) {
        log.info("Enter monitorPortfolio() for user: {}", username);
        CommonDto<MonitorPortfolioDto> commonDto = new CommonDto<>();

        try {
            UserEntity user = fetchUser(username);

            // ── Fetch portfolio and alerts ───────────────────────────────
            List<PortfolioEntity> portfolio = portfolioRepository.findByUser(user);

            if (portfolio.isEmpty()) {
                commonDto.setMsg("No portfolio found.");
                commonDto.setStatus("FAILED");
                commonDto.setStatusCode(404);
                return commonDto;
            }

            // Stream API: map alerts to symbol → AlertEntity
            Map<String, AlertEntity> alertMap = alertRepository.findByUser(user).stream()
                    .collect(Collectors.toMap(
                            a -> a.getStockSymbol().toUpperCase(),
                            Function.identity()
                    ));

            // ── Stream API: map each portfolio to MonitorStockDto ────────
            List<MonitorStockDto> stocks = portfolio.stream()
                    .map(p -> buildMonitorStock(p, alertMap))
                    .collect(Collectors.toList());

            // ── Stream API: compute overall totals ───────────────────────
            double totalInvested     = stocks.stream().mapToDouble(MonitorStockDto::getTotalInvested).sum();
            double totalCurrentValue = stocks.stream().mapToDouble(MonitorStockDto::getCurrentValue).sum();
            double totalGainLoss     = stocks.stream().mapToDouble(MonitorStockDto::getGainLoss).sum();
            double totalGLPercent    = totalInvested > 0 ? (totalGainLoss / totalInvested) * 100 : 0.0;

            // ── Stream API: count alert statuses ─────────────────────────
            long upperBreached = stocks.stream().filter(MonitorStockDto::isUpperBreached).count();
            long lowerBreached = stocks.stream().filter(MonitorStockDto::isLowerBreached).count();
            long normal        = stocks.stream()
                    .filter(s -> !s.isUpperBreached() && !s.isLowerBreached()).count();

            MonitorPortfolioDto result = MonitorPortfolioDto.builder()
                    .stocks(stocks)
                    .totalInvested(round(totalInvested))
                    .totalCurrentValue(round(totalCurrentValue))
                    .totalGainLoss(round(totalGainLoss))
                    .totalGainLossPercent(round(totalGLPercent))
                    .totalStocks(stocks.size())
                    .upperBreachedCount((int) upperBreached)
                    .lowerBreachedCount((int) lowerBreached)
                    .normalCount((int) normal)
                    .lastUpdated(LocalDateTime.now().toString())
                    .build();

            commonDto.setData(result);
            commonDto.setMsg("Portfolio monitoring data fetched successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);

            log.info("Monitor done for user: {}. Stocks: {}, UpperBreached: {}, LowerBreached: {}",
                    username, stocks.size(), upperBreached, lowerBreached);

        } catch (Exception e) {
            log.error("Error in monitorPortfolio: {}", e.getMessage());
            commonDto.setMsg("Failed to fetch monitor data.");
            commonDto.setStatus("FAILED");
            commonDto.setStatusCode(500);
        }

        return commonDto;
    }

    // ── US9 — Monitor single stock ────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public CommonDto<MonitorStockDto> monitorStock(String username, String stockSymbol) {
        log.info("Enter monitorStock() for user: {}, stock: {}", username, stockSymbol);
        CommonDto<MonitorStockDto> commonDto = new CommonDto<>();

        try {
            UserEntity user = fetchUser(username);
            String symbol = stockSymbol.trim().toUpperCase();

            // Fetch from portfolio
            Optional<PortfolioEntity> portfolioOpt =
                    portfolioRepository.findByUserAndStockSymbolIgnoreCase(user, symbol);

            if (portfolioOpt.isEmpty()) {
                return buildFail(commonDto, "Stock '" + symbol + "' not found in portfolio.");
            }

            // Fetch alert if set
            Map<String, AlertEntity> alertMap = alertRepository.findByUser(user).stream()
                    .collect(Collectors.toMap(
                            a -> a.getStockSymbol().toUpperCase(),
                            Function.identity()
                    ));

            MonitorStockDto stockDto = buildMonitorStock(portfolioOpt.get(), alertMap);

            commonDto.setData(stockDto);
            commonDto.setMsg("Stock monitor data fetched.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);

        } catch (Exception e) {
            log.error("Error in monitorStock: {}", e.getMessage());
            buildFail(commonDto, "Failed to monitor stock.");
        }

        return commonDto;
    }

    // ── Build MonitorStockDto for one portfolio entry ────────────────────
    private MonitorStockDto buildMonitorStock(PortfolioEntity p,
                                              Map<String, AlertEntity> alertMap) {
        String symbol = p.getStockSymbol().toUpperCase();

        // Get live price from Kafka cache — fallback to currentPrice in DB
        double currentPrice = stockPriceCache.getCurrentPrice(symbol, p.getCurrentPrice());

        double totalInvested  = p.getQuantity() * p.getBuyPrice();
        double currentValue   = p.getQuantity() * currentPrice;
        double gainLoss       = currentValue - totalInvested;
        double gainLossPercent = totalInvested > 0 ? (gainLoss / totalInvested) * 100 : 0.0;

        // ── Threshold breach check ───────────────────────────────────────
        AlertEntity alert = alertMap.get(symbol);
        Double upperThreshold  = null;
        Double lowerThreshold  = null;
        Double upperAlertPrice = null;
        Double lowerAlertPrice = null;
        boolean upperBreached  = false;
        boolean lowerBreached  = false;
        String alertStatus     = "NO_ALERT_SET";

        if (alert != null) {
            upperThreshold  = alert.getUpperThreshold();
            lowerThreshold  = alert.getLowerThreshold();
            upperAlertPrice = p.getBuyPrice() + (p.getBuyPrice() * upperThreshold / 100);
            lowerAlertPrice = p.getBuyPrice() - (p.getBuyPrice() * lowerThreshold / 100);
            upperBreached   = currentPrice >= upperAlertPrice;
            lowerBreached   = currentPrice <= lowerAlertPrice;

            if (upperBreached) {
                alertStatus = "UPPER_BREACHED";
                log.warn("UPPER threshold breached! Stock: {}, Current: {}, Alert: {}",
                        symbol, currentPrice, upperAlertPrice);
            } else if (lowerBreached) {
                alertStatus = "LOWER_BREACHED";
                log.warn("LOWER threshold breached! Stock: {}, Current: {}, Alert: {}",
                        symbol, currentPrice, lowerAlertPrice);
            } else {
                alertStatus = "NORMAL";
            }
        }

        return MonitorStockDto.builder()
                .stockSymbol(symbol)
                .companyName(p.getCompanyName())
                .quantity(p.getQuantity())
                .buyPrice(round(p.getBuyPrice()))
                .currentPrice(round(currentPrice))
                .totalInvested(round(totalInvested))
                .currentValue(round(currentValue))
                .gainLoss(round(gainLoss))
                .gainLossPercent(round(gainLossPercent))
                .upperThreshold(upperThreshold)
                .lowerThreshold(lowerThreshold)
                .upperAlertPrice(upperAlertPrice != null ? round(upperAlertPrice) : null)
                .lowerAlertPrice(lowerAlertPrice != null ? round(lowerAlertPrice) : null)
                .upperBreached(upperBreached)
                .lowerBreached(lowerBreached)
                .alertStatus(alertStatus)
                .build();
    }

    private UserEntity fetchUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new RuntimeException("User not found.");
                });
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private <T> CommonDto<T> buildFail(CommonDto<T> dto, String msg) {
        dto.setMsg(msg);
        dto.setStatus("FAILED");
        dto.setStatusCode(400);
        return dto;
    }
}