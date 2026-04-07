package com.ch.controller;

import com.ch.dto.*;
import com.ch.service.PortfolioService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for US6 – Create Portfolio
 *
 * POST /api/v1/portfolio          → Add one stock via UI form
 * GET  /api/v1/portfolio/valuation → Get portfolio with valuation
 *
 * Both endpoints protected — JWT token required
 */
@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {

    private static final Logger log = LoggerFactory.getLogger(PortfolioController.class);

    @Autowired
    private PortfolioService portfolioService;

    // ── ADD one stock to portfolio ───────────────────────────────────────
    @PostMapping
    public ResponseEntity<CommonDto<PortfolioItemDto>> addStock(
            @RequestBody CreatePortfolioRequestDto request,
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("POST /api/v1/portfolio - user: {}, stock: {}", username, request.getStockSymbol());

        CommonDto<PortfolioItemDto> response = portfolioService.addStock(username, request);
        HttpStatus status = "SUCCESS".equals(response.getStatus())
                ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }

    // ── GET portfolio with valuation ─────────────────────────────────────
    @GetMapping("/valuation")
    public ResponseEntity<CommonDto<PortfolioValuationDto>> getValuation(
            HttpServletRequest httpRequest) {

        String username = (String) httpRequest.getAttribute("username");
        log.info("GET /api/v1/portfolio/valuation - user: {}", username);

        CommonDto<PortfolioValuationDto> response = portfolioService.getPortfolioValuation(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
