package com.ch.serviceImpl;

import com.ch.dto.AlertHistoryDto;
import com.ch.dto.CommonDto;
import com.ch.entity.AlertHistoryEntity;
import com.ch.entity.UserEntity;
import com.ch.rabbitmq.AlertEmailMessage;
import com.ch.repository.AlertHistoryRepository;
import com.ch.repository.UserRepository;
import com.ch.service.AlertHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertHistoryServiceImpl implements AlertHistoryService {

    private static final Logger log = LoggerFactory.getLogger(AlertHistoryServiceImpl.class);

    @Autowired
    private AlertHistoryRepository alertHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    // ── Save history after email sent ────────────────────────────────────
    @Override
    @Transactional
    public void saveHistory(AlertEmailMessage message) {
        try {
            UserEntity user = userRepository.findByUsername(message.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            AlertHistoryEntity history = AlertHistoryEntity.builder()
                    .user(user)
                    .stockSymbol(message.getStockSymbol())
                    .companyName(message.getCompanyName())
                    .breachType(message.getBreachType())
                    .buyPrice(message.getBuyPrice())
                    .currentPrice(message.getCurrentPrice())
                    .alertPrice(message.getAlertPrice())
                    .gainLoss(message.getGainLoss())
                    .gainLossPercent(message.getGainLossPercent())
                    .emailSentTo(message.getEmail())
                    .triggeredAt(LocalDateTime.now())
                    .build();

            alertHistoryRepository.save(history);
            log.info("Alert history saved. User: {}, Stock: {}", message.getUsername(), message.getStockSymbol());

        } catch (Exception e) {
            log.error("Failed to save alert history: {}", e.getMessage());
        }
    }

    // ── Get all alert history ────────────────────────────────────────────
    @Override
    public CommonDto<AlertHistoryDto> getAlertHistory(String username) {
        log.info("Enter getAlertHistory() for user: {}", username);
        CommonDto<AlertHistoryDto> commonDto = new CommonDto<>();

        try {
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found."));

            List<AlertHistoryDto> history = alertHistoryRepository
                    .findByUserOrderByTriggeredAtDesc(user)
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());

            commonDto.setDataList(history);
            commonDto.setMsg("Alert history fetched successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);
            log.info("Fetched {} alert history records for user: {}", history.size(), username);

        } catch (Exception e) {
            log.error("Error in getAlertHistory: {}", e.getMessage());
            commonDto.setMsg(e.getMessage());
            commonDto.setStatus("FAILED");
            commonDto.setStatusCode(500);
        }

        return commonDto;
    }

    // ── Get history by stock ─────────────────────────────────────────────
    @Override
    public CommonDto<AlertHistoryDto> getAlertHistoryByStock(String username, String stockSymbol) {
        log.info("Enter getAlertHistoryByStock() user: {}, stock: {}", username, stockSymbol);
        CommonDto<AlertHistoryDto> commonDto = new CommonDto<>();

        try {
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found."));

            List<AlertHistoryDto> history = alertHistoryRepository
                    .findByUserAndStockSymbolIgnoreCaseOrderByTriggeredAtDesc(user, stockSymbol.trim().toUpperCase())
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());

            commonDto.setDataList(history);
            commonDto.setMsg(history.isEmpty()
                    ? "No alert history for stock '" + stockSymbol + "'."
                    : "Alert history fetched.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);

        } catch (Exception e) {
            log.error("Error in getAlertHistoryByStock: {}", e.getMessage());
            commonDto.setMsg(e.getMessage());
            commonDto.setStatus("FAILED");
            commonDto.setStatusCode(500);
        }

        return commonDto;
    }

    private AlertHistoryDto toDto(AlertHistoryEntity entity) {
        return AlertHistoryDto.builder()
                .id(entity.getId())
                .stockSymbol(entity.getStockSymbol())
                .companyName(entity.getCompanyName())
                .breachType(entity.getBreachType())
                .buyPrice(entity.getBuyPrice())
                .currentPrice(entity.getCurrentPrice())
                .alertPrice(entity.getAlertPrice())
                .gainLoss(entity.getGainLoss())
                .gainLossPercent(entity.getGainLossPercent())
                .emailSentTo(entity.getEmailSentTo())
                .triggeredAt(entity.getTriggeredAt() != null ? entity.getTriggeredAt().toString() : null)
                .build();
    }
}