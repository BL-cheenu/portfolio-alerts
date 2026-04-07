package com.ch.serviceImpl;

import com.ch.dto.CommonDto;
import com.ch.dto.StockMasterDto;
import com.ch.dto.StockValidationDto;
import com.ch.entity.StockEntity;
import com.ch.repository.StockRepository;
import com.ch.service.StockMasterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * US4 - Stock Master Service
 *
 * Operations:
 *  1. getAllStocks()     — app initialisation-ல் load பண்றோம்
 *  2. searchStocks()    — user ticker/company name search பண்றது
 *  3. validateTickers() — file upload cross-check
 */
@Service
public class StockMasterServiceImpl implements StockMasterService {

    private static final Logger log = LoggerFactory.getLogger(StockMasterServiceImpl.class);

    @Autowired
    private StockRepository stockRepository;

    // ── 1. Get all stocks — app init ─────────────────────────────────────
    @Override
    public CommonDto<StockMasterDto> getAllStocks() {
        log.info("Enter getAllStocks()");
        CommonDto<StockMasterDto> commonDto = new CommonDto<>();

        try {
            List<StockEntity> stocks = stockRepository.findAll();
            List<StockMasterDto> dtoList = stocks.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());

            commonDto.setDataList(dtoList);
            commonDto.setMsg("Stocks fetched successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);
            log.info("Total stocks fetched: {}", dtoList.size());

        } catch (Exception e) {
            log.error("Error in getAllStocks: {}", e.getMessage());
            commonDto.setMsg("Failed to fetch stocks.");
            commonDto.setStatus("FAILED");
            commonDto.setStatusCode(500);
        }

        return commonDto;
    }

    // ── 2. Search by ticker or company name ──────────────────────────────
    @Override
    public CommonDto<StockMasterDto> searchStocks(String keyword) {
        log.info("Enter searchStocks() keyword: {}", keyword);
        CommonDto<StockMasterDto> commonDto = new CommonDto<>();

        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                commonDto.setMsg("Search keyword must not be blank.");
                commonDto.setStatus("FAILED");
                commonDto.setStatusCode(400);
                return commonDto;
            }

            // Search by ticker OR company name
            List<StockEntity> results = stockRepository
                    .findByTickerSymbolContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(
                            keyword.trim(), keyword.trim());

            List<StockMasterDto> dtoList = results.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());

            commonDto.setDataList(dtoList);
            commonDto.setMsg(dtoList.isEmpty() ? "No stocks found." : "Stocks found.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);
            log.info("Search '{}' returned {} results", keyword, dtoList.size());

        } catch (Exception e) {
            log.error("Error in searchStocks: {}", e.getMessage());
            commonDto.setMsg("Search failed.");
            commonDto.setStatus("FAILED");
            commonDto.setStatusCode(500);
        }

        return commonDto;
    }

    // ── 3. Validate tickers from file upload ─────────────────────────────
    @Override
    public CommonDto<StockValidationDto> validateTickers(List<String> tickers) {
        log.info("Enter validateTickers() count: {}", tickers != null ? tickers.size() : 0);
        CommonDto<StockValidationDto> commonDto = new CommonDto<>();

        try {
            if (tickers == null || tickers.isEmpty()) {
                commonDto.setMsg("Ticker list must not be empty.");
                commonDto.setStatus("FAILED");
                commonDto.setStatusCode(400);
                return commonDto;
            }

            List<String> validTickers   = new ArrayList<>();
            List<String> invalidTickers = new ArrayList<>();

            // Cross-check each ticker against stocks master table
            for (String ticker : tickers) {
                if (ticker == null || ticker.trim().isEmpty()) continue;

                boolean exists = stockRepository.existsByTickerSymbolIgnoreCase(ticker.trim());
                if (exists) {
                    validTickers.add(ticker.trim().toUpperCase());
                } else {
                    invalidTickers.add(ticker.trim().toUpperCase());
                    log.warn("Invalid ticker found: {}", ticker);
                }
            }

            StockValidationDto validationDto = StockValidationDto.builder()
                    .validTickers(validTickers)
                    .invalidTickers(invalidTickers)
                    .totalCount(tickers.size())
                    .validCount(validTickers.size())
                    .invalidCount(invalidTickers.size())
                    .build();

            commonDto.setData(validationDto);
            commonDto.setMsg(invalidTickers.isEmpty()
                    ? "All tickers are valid."
                    : invalidTickers.size() + " invalid ticker(s) found.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);
            log.info("Validation done. Valid: {}, Invalid: {}", validTickers.size(), invalidTickers.size());

        } catch (Exception e) {
            log.error("Error in validateTickers: {}", e.getMessage());
            commonDto.setMsg("Validation failed.");
            commonDto.setStatus("FAILED");
            commonDto.setStatusCode(500);
        }

        return commonDto;
    }

    // ── Entity → DTO ─────────────────────────────────────────────────────
    private StockMasterDto toDto(StockEntity entity) {
        return StockMasterDto.builder()
                .id(entity.getId())
                .companyName(entity.getCompanyName())
                .tickerSymbol(entity.getTickerSymbol())
                .exchange(entity.getExchange())
                .build();
    }
}
