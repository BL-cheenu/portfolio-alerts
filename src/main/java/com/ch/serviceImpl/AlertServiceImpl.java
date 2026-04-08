package com.ch.serviceImpl;

import com.ch.dto.AlertRequestDto;
import com.ch.dto.AlertResponseDto;
import com.ch.dto.CommonDto;
import com.ch.entity.AlertEntity;
import com.ch.entity.PortfolioEntity;
import com.ch.entity.UserEntity;
import com.ch.repository.AlertRepository;
import com.ch.repository.PortfolioRepository;
import com.ch.repository.StockRepository;
import com.ch.repository.UserRepository;
import com.ch.service.AlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * US8 - Alert Threshold Service
 *
 * Upper threshold: alert when price rises X% above buy price
 * Lower threshold: alert when price drops X% below buy price
 *
 * Alert Price Calculation:
 *   upperAlertPrice = buyPrice + (buyPrice * upperThreshold / 100)
 *   lowerAlertPrice = buyPrice - (buyPrice * lowerThreshold / 100)
 */
@Service
public class AlertServiceImpl implements AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertServiceImpl.class);

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private StockRepository stockRepository;

    // ── US8.1 — Set new alert threshold ─────────────────────────────────
    @Override
    @Transactional
    public CommonDto<AlertResponseDto> setAlert(String username, AlertRequestDto request) {
        log.info("Enter setAlert() for user: {}, stock: {}", username, request.getStockSymbol());
        CommonDto<AlertResponseDto> commonDto = new CommonDto<>();

        try {
            // ── Validate request ─────────────────────────────────────────
            String validationError = validateRequest(request);
            if (validationError != null) {
                log.warn("setAlert validation failed: {}", validationError);
                return buildFail(commonDto, validationError);
            }

            UserEntity user = fetchUser(username);
            String symbol = request.getStockSymbol().trim().toUpperCase();

            // ── Validate stock against master ────────────────────────────
            if (!stockRepository.existsByTickerSymbolIgnoreCase(symbol)) {
                log.warn("setAlert failed - invalid stock: {}", symbol);
                return buildFail(commonDto, "Stock '" + symbol + "' is not valid.");
            }

            // ── Check duplicate alert ────────────────────────────────────
            if (alertRepository.existsByUserAndStockSymbolIgnoreCase(user, symbol)) {
                log.warn("setAlert failed - alert already exists for stock: {}", symbol);
                return buildFail(commonDto, "Alert already exists for '" + symbol + "'. Use update instead.");
            }

            // ── Get company name from master ─────────────────────────────
            String companyName = resolveCompanyName(request, symbol);

            // ── Save alert ───────────────────────────────────────────────
            AlertEntity alert = AlertEntity.builder()
                    .user(user)
                    .stockSymbol(symbol)
                    .companyName(companyName)
                    .upperThreshold(request.getUpperThreshold())
                    .lowerThreshold(request.getLowerThreshold())
                    .build();

            AlertEntity saved = alertRepository.save(alert);
            log.info("Alert set. ID: {}, Stock: {}, Upper: {}%, Lower: {}%",
                    saved.getId(), symbol, request.getUpperThreshold(), request.getLowerThreshold());

            // ── Get buy price from portfolio for alert price calc ────────
            double buyPrice = getBuyPrice(user, symbol);

            commonDto.setData(toDto(saved, buyPrice));
            commonDto.setMsg("Alert set for '" + symbol + "' successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(201);

        } catch (Exception e) {
            log.error("Error in setAlert: {}", e.getMessage());
            buildFail(commonDto, "Failed to set alert.");
        }

        return commonDto;
    }

    // ── US8.2 — Update existing threshold ───────────────────────────────
    @Override
    @Transactional
    public CommonDto<AlertResponseDto> updateAlert(String username, Long alertId,
                                                    AlertRequestDto request) {
        log.info("Enter updateAlert() for user: {}, alertId: {}", username, alertId);
        CommonDto<AlertResponseDto> commonDto = new CommonDto<>();

        try {
            // Validate thresholds
            if (request.getUpperThreshold() <= 0 && request.getLowerThreshold() <= 0) {
                return buildFail(commonDto, "Provide at least upper or lower threshold to update.");
            }

            UserEntity user = fetchUser(username);

            Optional<AlertEntity> alertOpt = alertRepository.findByIdAndUser(alertId, user);
            if (alertOpt.isEmpty()) {
                log.warn("updateAlert failed - alert not found. ID: {}", alertId);
                return buildFail(commonDto, "Alert not found.");
            }

            AlertEntity alert = alertOpt.get();

            // Update only provided values
            if (request.getUpperThreshold() > 0) {
                log.info("Updating upperThreshold: {}% → {}% for stock: {}",
                        alert.getUpperThreshold(), request.getUpperThreshold(), alert.getStockSymbol());
                alert.setUpperThreshold(request.getUpperThreshold());
            }
            if (request.getLowerThreshold() > 0) {
                log.info("Updating lowerThreshold: {}% → {}% for stock: {}",
                        alert.getLowerThreshold(), request.getLowerThreshold(), alert.getStockSymbol());
                alert.setLowerThreshold(request.getLowerThreshold());
            }

            AlertEntity updated = alertRepository.save(alert);
            double buyPrice = getBuyPrice(user, updated.getStockSymbol());

            log.info("Alert updated. ID: {}, Stock: {}", updated.getId(), updated.getStockSymbol());

            commonDto.setData(toDto(updated, buyPrice));
            commonDto.setMsg("Alert for '" + updated.getStockSymbol() + "' updated successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);

        } catch (Exception e) {
            log.error("Error in updateAlert: {}", e.getMessage());
            buildFail(commonDto, "Failed to update alert.");
        }

        return commonDto;
    }

    // ── US8.3 — Get all alerts ───────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public CommonDto<AlertResponseDto> getAllAlerts(String username) {
        log.info("Enter getAllAlerts() for user: {}", username);
        CommonDto<AlertResponseDto> commonDto = new CommonDto<>();

        try {
            UserEntity user = fetchUser(username);
            List<AlertEntity> alerts = alertRepository.findByUser(user);

            // Stream API — map to DTO with buy price
            List<AlertResponseDto> dtoList = alerts.stream()
                    .map(alert -> {
                        double buyPrice = getBuyPrice(user, alert.getStockSymbol());
                        return toDto(alert, buyPrice);
                    })
                    .collect(Collectors.toList());

            commonDto.setDataList(dtoList);
            commonDto.setMsg("Alerts fetched successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);
            log.info("Fetched {} alerts for user: {}", dtoList.size(), username);

        } catch (Exception e) {
            log.error("Error in getAllAlerts: {}", e.getMessage());
            commonDto.setMsg("Failed to fetch alerts.");
            commonDto.setStatus("FAILED");
            commonDto.setStatusCode(500);
        }

        return commonDto;
    }

    // ── US8.4 — Get alert for specific stock ────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public CommonDto<AlertResponseDto> getAlertByStock(String username, String stockSymbol) {
        log.info("Enter getAlertByStock() for user: {}, stock: {}", username, stockSymbol);
        CommonDto<AlertResponseDto> commonDto = new CommonDto<>();

        try {
            UserEntity user = fetchUser(username);
            String symbol = stockSymbol.trim().toUpperCase();

            Optional<AlertEntity> alertOpt =
                    alertRepository.findByUserAndStockSymbolIgnoreCase(user, symbol);

            if (alertOpt.isEmpty()) {
                return buildFail(commonDto, "No alert found for stock '" + symbol + "'.");
            }

            double buyPrice = getBuyPrice(user, symbol);
            commonDto.setData(toDto(alertOpt.get(), buyPrice));
            commonDto.setMsg("Alert fetched successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);

        } catch (Exception e) {
            log.error("Error in getAlertByStock: {}", e.getMessage());
            buildFail(commonDto, "Failed to fetch alert.");
        }

        return commonDto;
    }

    // ── US8.5 — Delete alert ─────────────────────────────────────────────
    @Override
    @Transactional
    public CommonDto<AlertResponseDto> deleteAlert(String username, Long alertId) {
        log.info("Enter deleteAlert() for user: {}, alertId: {}", username, alertId);
        CommonDto<AlertResponseDto> commonDto = new CommonDto<>();

        try {
            UserEntity user = fetchUser(username);

            Optional<AlertEntity> alertOpt = alertRepository.findByIdAndUser(alertId, user);
            if (alertOpt.isEmpty()) {
                log.warn("deleteAlert failed - alert not found. ID: {}", alertId);
                return buildFail(commonDto, "Alert not found.");
            }

            String symbol = alertOpt.get().getStockSymbol();
            alertRepository.delete(alertOpt.get());
            log.info("Alert deleted. ID: {}, Stock: {}", alertId, symbol);

            commonDto.setMsg("Alert for '" + symbol + "' deleted successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);

        } catch (Exception e) {
            log.error("Error in deleteAlert: {}", e.getMessage());
            buildFail(commonDto, "Failed to delete alert.");
        }

        return commonDto;
    }

    // ── Alert price calculation ──────────────────────────────────────────
    // upperAlertPrice = buyPrice + (buyPrice * upperThreshold / 100)
    // lowerAlertPrice = buyPrice - (buyPrice * lowerThreshold / 100)
    private AlertResponseDto toDto(AlertEntity entity, double buyPrice) {
        double upperAlertPrice = buyPrice > 0
                ? buyPrice + (buyPrice * entity.getUpperThreshold() / 100) : 0.0;
        double lowerAlertPrice = buyPrice > 0
                ? buyPrice - (buyPrice * entity.getLowerThreshold() / 100) : 0.0;

        return AlertResponseDto.builder()
                .id(entity.getId())
                .stockSymbol(entity.getStockSymbol())
                .companyName(entity.getCompanyName())
                .upperThreshold(entity.getUpperThreshold())
                .lowerThreshold(entity.getLowerThreshold())
                .buyPrice(round(buyPrice))
                .upperAlertPrice(round(upperAlertPrice))
                .lowerAlertPrice(round(lowerAlertPrice))
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .build();
    }

    // ── Get buy price from portfolio ─────────────────────────────────────
    private double getBuyPrice(UserEntity user, String symbol) {
        return portfolioRepository
                .findByUserAndStockSymbolIgnoreCase(user, symbol)
                .map(PortfolioEntity::getBuyPrice)
                .orElse(0.0);
    }

    // ── Fetch user ───────────────────────────────────────────────────────
    private UserEntity fetchUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new RuntimeException("User not found.");
                });
    }

    // ── Resolve company name ─────────────────────────────────────────────
    private String resolveCompanyName(AlertRequestDto request, String symbol) {
        if (request.getCompanyName() != null && !request.getCompanyName().trim().isEmpty()) {
            return request.getCompanyName().trim();
        }
        return stockRepository.findByTickerSymbolIgnoreCase(symbol)
                .map(s -> s.getCompanyName())
                .orElse(symbol);
    }

    // ── Validate request ─────────────────────────────────────────────────
    private String validateRequest(AlertRequestDto request) {
        if (request.getStockSymbol() == null || request.getStockSymbol().trim().isEmpty()) {
            return "Stock symbol must not be blank.";
        }
        if (request.getUpperThreshold() <= 0) {
            return "Upper threshold must be greater than 0%.";
        }
        if (request.getLowerThreshold() <= 0) {
            return "Lower threshold must be greater than 0%.";
        }
        if (request.getUpperThreshold() > 100) {
            return "Upper threshold cannot exceed 100%.";
        }
        if (request.getLowerThreshold() > 100) {
            return "Lower threshold cannot exceed 100%.";
        }
        return null;
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
