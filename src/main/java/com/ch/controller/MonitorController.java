package com.ch.controller;

import com.ch.dto.CommonDto;
import com.ch.dto.MonitorPortfolioDto;
import com.ch.dto.MonitorStockDto;
import com.ch.service.MonitorService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for US9 – Real-time Portfolio Monitor
 *
 * GET /api/v1/monitor              → Full portfolio monitor
 * GET /api/v1/monitor/{symbol}     → Single stock monitor
 *
 * Both endpoints protected — JWT token required
 */
@RestController
@RequestMapping("/api/v1/monitor")
public class MonitorController {

    private static final Logger log = LoggerFactory.getLogger(MonitorController.class);

    @Autowired
    private MonitorService monitorService;

    // ── Monitor full portfolio ───────────────────────────────────────────
    @GetMapping
    public ResponseEntity<CommonDto<MonitorPortfolioDto>> monitorPortfolio(
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("GET /api/v1/monitor - user: {}", username);

        CommonDto<MonitorPortfolioDto> response = monitorService.monitorPortfolio(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ── Monitor single stock ─────────────────────────────────────────────
    @GetMapping("/{symbol}")
    public ResponseEntity<CommonDto<MonitorStockDto>> monitorStock(
            @PathVariable String symbol,
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("GET /api/v1/monitor/{} - user: {}", symbol, username);

        CommonDto<MonitorStockDto> response = monitorService.monitorStock(username, symbol);
        HttpStatus status = "SUCCESS".equals(response.getStatus())
                ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(response, status);
    }
}