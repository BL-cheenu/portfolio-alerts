package com.ch.rabbitmq;

import com.ch.config.RabbitMQConfig;
import com.ch.entity.AlertEntity;
import com.ch.entity.PortfolioEntity;
import com.ch.entity.UserEntity;
import com.ch.kafka.StockPriceCache;
import com.ch.repository.AlertRepository;
import com.ch.repository.PortfolioRepository;
import com.ch.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * US10 - Alert Generator (RabbitMQ Producer)
 *
 * Runs every 60 seconds:
 *  1. Checks all users' portfolio vs threshold
 *  2. If breached → serialize AlertEmailMessage as JSON string
 *  3. Publish JSON string to RabbitMQ queue
 *
 * Note: Sends String (JSON) because SimpleMessageConverter is used
 */
@Component
public class AlertGenerator {

    private static final Logger log = LoggerFactory.getLogger(AlertGenerator.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private StockPriceCache stockPriceCache;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 60000)
    public void checkThresholdsAndGenerateAlerts() {
        log.info("AlertGenerator: checking thresholds for all users...");
        List<UserEntity> users = userRepository.findAll();
        int alertsSent = 0;

        for (UserEntity user : users) {
            try {
                alertsSent += processUserAlerts(user);
            } catch (Exception e) {
                log.error("AlertGenerator error for user {}: {}", user.getUsername(), e.getMessage());
            }
        }
        log.info("AlertGenerator: done. Alerts published: {}", alertsSent);
    }

    private int processUserAlerts(UserEntity user) {
        List<AlertEntity> alerts = alertRepository.findByUserAndIsActive(user, true);
        if (alerts.isEmpty()) return 0;

        Map<String, AlertEntity> alertMap = alerts.stream()
                .collect(Collectors.toMap(
                        a -> a.getStockSymbol().toUpperCase(),
                        Function.identity()
                ));

        List<PortfolioEntity> portfolio = portfolioRepository.findByUser(user);
        int count = 0;

        for (PortfolioEntity holding : portfolio) {
            String symbol = holding.getStockSymbol().toUpperCase();
            AlertEntity alert = alertMap.get(symbol);
            if (alert == null) continue;

            double currentPrice   = stockPriceCache.getCurrentPrice(symbol, holding.getCurrentPrice());
            double upperAlertPrice = holding.getBuyPrice() + (holding.getBuyPrice() * alert.getUpperThreshold() / 100);
            double lowerAlertPrice = holding.getBuyPrice() - (holding.getBuyPrice() * alert.getLowerThreshold() / 100);

            if (currentPrice >= upperAlertPrice) {
                publishAlert(user, holding, currentPrice, "UPPER_BREACHED",
                        alert.getUpperThreshold(), upperAlertPrice);
                count++;
                log.warn("UPPER breach! User: {}, Stock: {}, Price: {}", user.getUsername(), symbol, currentPrice);
            } else if (currentPrice <= lowerAlertPrice) {
                publishAlert(user, holding, currentPrice, "LOWER_BREACHED",
                        alert.getLowerThreshold(), lowerAlertPrice);
                count++;
                log.warn("LOWER breach! User: {}, Stock: {}, Price: {}", user.getUsername(), symbol, currentPrice);
            }
        }
        return count;
    }

    private void publishAlert(UserEntity user, PortfolioEntity holding,
                              double currentPrice, String breachType,
                              double thresholdValue, double alertPrice) {
        try {
            double totalInvested   = holding.getQuantity() * holding.getBuyPrice();
            double currentValue    = holding.getQuantity() * currentPrice;
            double gainLoss        = currentValue - totalInvested;
            double gainLossPercent = totalInvested > 0 ? (gainLoss / totalInvested) * 100 : 0.0;

            AlertEmailMessage message = AlertEmailMessage.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .stockSymbol(holding.getStockSymbol())
                    .companyName(holding.getCompanyName())
                    .buyPrice(holding.getBuyPrice())
                    .currentPrice(round(currentPrice))
                    .quantity(holding.getQuantity())
                    .totalInvested(round(totalInvested))
                    .currentValue(round(currentValue))
                    .gainLoss(round(gainLoss))
                    .gainLossPercent(round(gainLossPercent))
                    .breachType(breachType)
                    .thresholdValue(thresholdValue)
                    .alertPrice(round(alertPrice))
                    .triggeredAt(LocalDateTime.now().toString())
                    .build();

            // FIX: Serialize to JSON String — SimpleMessageConverter compatible
            String json = objectMapper.writeValueAsString(message);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ALERT_EXCHANGE,
                    RabbitMQConfig.ALERT_ROUTING_KEY,
                    json
            );
            log.info("Alert published to RabbitMQ. User: {}, Stock: {}, Breach: {}",
                    user.getUsername(), holding.getStockSymbol(), breachType);

        } catch (Exception e) {
            log.error("Failed to publish alert for {}: {}", holding.getStockSymbol(), e.getMessage());
        }
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}