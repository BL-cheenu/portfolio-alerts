package com.ch.controller;

import com.ch.dto.CommonDto;
import com.ch.dto.StockMasterDto;
import com.ch.dto.StockValidationDto;
import com.ch.service.StockMasterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for US4 – Stock Master
 *
 * GET  /api/v1/stocks              → Get all stocks (app init)
 * GET  /api/v1/stocks/search?q=    → Search by ticker or company
 * POST /api/v1/stocks/validate     → Validate tickers from file upload
 */
@RestController
@RequestMapping("/api/v1/stocks")
public class StockMasterController {

    private static final Logger log = LoggerFactory.getLogger(StockMasterController.class);

    @Autowired
    private StockMasterService stockMasterService;

    // ── GET all stocks — app initialisation ─────────────────────────────
    @GetMapping
    public ResponseEntity<CommonDto<StockMasterDto>> getAllStocks() {
        log.info("GET /api/v1/stocks");
        CommonDto<StockMasterDto> response = stockMasterService.getAllStocks();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ── SEARCH stocks by ticker or company name ──────────────────────────
    @GetMapping("/search")
    public ResponseEntity<CommonDto<StockMasterDto>> searchStocks(
            @RequestParam("q") String keyword) {
        log.info("GET /api/v1/stocks/search?q={}", keyword);
        CommonDto<StockMasterDto> response = stockMasterService.searchStocks(keyword);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ── VALIDATE tickers from file upload ────────────────────────────────
    @PostMapping("/validate")
    public ResponseEntity<CommonDto<StockValidationDto>> validateTickers(
            @RequestBody List<String> tickers) {
        log.info("POST /api/v1/stocks/validate - count: {}", tickers.size());
        CommonDto<StockValidationDto> response = stockMasterService.validateTickers(tickers);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
