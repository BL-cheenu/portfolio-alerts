package com.ch.controller;

import com.ch.dto.AlertHistoryDto;
import com.ch.dto.CommonDto;
import com.ch.service.AlertHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for US10 – Alert History
 *
 * GET /api/v1/alert-history                  → All alert history
 * GET /api/v1/alert-history/stock/{symbol}   → History by stock
 *
 * Both endpoints protected — JWT token required
 */
@RestController
@RequestMapping("/api/v1/alert-history")
public class AlertHistoryController {

    private static final Logger log = LoggerFactory.getLogger(AlertHistoryController.class);

    @Autowired
    private AlertHistoryService alertHistoryService;

    // ── Get all alert history ────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<CommonDto<AlertHistoryDto>> getAlertHistory(
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("GET /api/v1/alert-history - user: {}", username);

        CommonDto<AlertHistoryDto> response = alertHistoryService.getAlertHistory(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ── Get history by stock ─────────────────────────────────────────────
    @GetMapping("/stock/{symbol}")
    public ResponseEntity<CommonDto<AlertHistoryDto>> getHistoryByStock(
            @PathVariable String symbol,
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("GET /api/v1/alert-history/stock/{} - user: {}", symbol, username);

        CommonDto<AlertHistoryDto> response =
                alertHistoryService.getAlertHistoryByStock(username, symbol);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}