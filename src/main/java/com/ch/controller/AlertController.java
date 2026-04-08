package com.ch.controller;

import com.ch.dto.AlertRequestDto;
import com.ch.dto.AlertResponseDto;
import com.ch.dto.CommonDto;
import com.ch.service.AlertService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for US8 – Alert Threshold Setting
 *
 * POST   /api/v1/alerts              → Set new alert
 * PUT    /api/v1/alerts/{id}         → Update alert threshold
 * GET    /api/v1/alerts              → Get all alerts
 * GET    /api/v1/alerts/stock/{sym}  → Get alert by stock
 * DELETE /api/v1/alerts/{id}         → Delete alert
 *
 * All endpoints protected — JWT token required
 */
@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {

    private static final Logger log = LoggerFactory.getLogger(AlertController.class);

    @Autowired
    private AlertService alertService;

    // ── SET new alert ────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<CommonDto<AlertResponseDto>> setAlert(
            @RequestBody AlertRequestDto request,
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("POST /api/v1/alerts - user: {}, stock: {}", username, request.getStockSymbol());

        CommonDto<AlertResponseDto> response = alertService.setAlert(username, request);
        HttpStatus status = "SUCCESS".equals(response.getStatus())
                ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }

    // ── UPDATE alert threshold ───────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<CommonDto<AlertResponseDto>> updateAlert(
            @PathVariable Long id,
            @RequestBody AlertRequestDto request,
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("PUT /api/v1/alerts/{} - user: {}", id, username);

        CommonDto<AlertResponseDto> response = alertService.updateAlert(username, id, request);
        HttpStatus status = "SUCCESS".equals(response.getStatus())
                ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }

    // ── GET all alerts ───────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<CommonDto<AlertResponseDto>> getAllAlerts(
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("GET /api/v1/alerts - user: {}", username);

        CommonDto<AlertResponseDto>response = alertService.getAllAlerts(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ── GET alert by stock symbol ────────────────────────────────────────
    @GetMapping("/stock/{symbol}")
    public ResponseEntity<CommonDto<AlertResponseDto>> getAlertByStock(
            @PathVariable String symbol,
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("GET /api/v1/alerts/stock/{} - user: {}", symbol, username);

        CommonDto<AlertResponseDto> response = alertService.getAlertByStock(username, symbol);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ── DELETE alert ─────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonDto<AlertResponseDto>> deleteAlert(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("DELETE /api/v1/alerts/{} - user: {}", id, username);

        CommonDto<AlertResponseDto> response = alertService.deleteAlert(username, id);
        HttpStatus status = "SUCCESS".equals(response.getStatus())
                ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }
}
