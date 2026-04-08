package com.ch.controller;

import com.ch.dto.*;
import com.ch.service.ManagePortfolioService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for US7 – Manage Portfolio
 *
 * GET    /api/v1/portfolio                  → View full portfolio
 * PUT    /api/v1/portfolio/{id}             → Update one stock
 * DELETE /api/v1/portfolio/{id}             → Delete one stock
 * DELETE /api/v1/portfolio                  → Delete all stocks
 *
 * All endpoints protected — JWT token required
 */
@RestController
@RequestMapping("/api/v1/portfolio")
public class ManagePortfolioController {

    private static final Logger log = LoggerFactory.getLogger(ManagePortfolioController.class);

    @Autowired
    private ManagePortfolioService managePortfolioService;

    // ── US7.1 — GET full portfolio ───────────────────────────────────────
    @GetMapping
    public ResponseEntity<CommonDto<PortfolioValuationDto>> getPortfolio(
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("GET /api/v1/portfolio - user: {}", username);

        CommonDto<PortfolioValuationDto> response = managePortfolioService.getPortfolio(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ── US7.2 — PUT update one stock ─────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<CommonDto<PortfolioItemDto>> updateStock(
            @PathVariable Long id,
            @RequestBody UpdatePortfolioRequestDto request,
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("PUT /api/v1/portfolio/{} - user: {}", id, username);

        CommonDto<PortfolioItemDto> response = managePortfolioService.updateStock(username, id, request);
        HttpStatus status = "SUCCESS".equals(response.getStatus()) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }

    // ── US7.3 — DELETE one stock ─────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonDto<PortfolioItemDto>> deleteStock(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("DELETE /api/v1/portfolio/{} - user: {}", id, username);

        CommonDto<PortfolioItemDto> response = managePortfolioService.deleteStock(username, id);
        HttpStatus status = "SUCCESS".equals(response.getStatus()) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }

    // ── US7.4 — DELETE all stocks ────────────────────────────────────────
    @DeleteMapping
    public ResponseEntity<CommonDto<PortfolioItemDto>> deleteAllStocks(
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("DELETE /api/v1/portfolio (all) - user: {}", username);

        CommonDto<PortfolioItemDto> response = managePortfolioService.deleteAllStocks(username);
        HttpStatus status = "SUCCESS".equals(response.getStatus()) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }
}