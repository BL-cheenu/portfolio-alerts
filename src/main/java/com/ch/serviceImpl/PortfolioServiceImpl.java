package com.ch.serviceImpl;

import com.ch.dto.*;
import com.ch.entity.PortfolioEntity;
import com.ch.entity.UserEntity;
import com.ch.repository.PortfolioRepository;
import com.ch.repository.StockRepository;
import com.ch.repository.UserRepository;
import com.ch.service.PortfolioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * US6 - Create Portfolio Service
 *
 * Key Concepts Used:
 *  - Stream API for valuation computation
 *  - Stock master validation before saving
 *  - JPA for portfolio persistence
 */
@Service
public class PortfolioServiceImpl implements PortfolioService {

    private static final Logger log = LoggerFactory.getLogger(PortfolioServiceImpl.class);

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    // ── ADD one stock via UI form ────────────────────────────────────────
    @Override
    @Transactional
    public CommonDto<PortfolioItemDto> addStock(String username, CreatePortfolioRequestDto request) {
        log.info("Enter addStock() for user: {}", username);
        CommonDto<PortfolioItemDto> commonDto = new CommonDto<>();

        try {
            // ── 1. Validate request fields ───────────────────────────────
            if (request.getStockSymbol() == null || request.getStockSymbol().trim().isEmpty()) {
                log.warn("addStock failed - stock symbol blank for user: {}", username);
                return buildFail(commonDto, "Stock symbol must not be blank.");
            }
            if (request.getQuantity() <= 0) {
                log.warn("addStock failed - invalid quantity: {}", request.getQuantity());
                return buildFail(commonDto, "Quantity must be greater than 0.");
            }
            if (request.getBuyPrice() <= 0) {
                log.warn("addStock failed - invalid buy price: {}", request.getBuyPrice());
                return buildFail(commonDto, "Buy price must be greater than 0.");
            }

            // ── 2. Fetch user ────────────────────────────────────────────
            Optional<UserEntity> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                log.warn("addStock failed - user not found: {}", username);
                return buildFail(commonDto, "User not found.");
            }
            UserEntity user = userOpt.get();

            // ── 3. Validate stock against master table ───────────────────
            String symbol = request.getStockSymbol().trim().toUpperCase();
            boolean validStock = stockRepository.existsByTickerSymbolIgnoreCase(symbol);
            if (!validStock) {
                log.warn("addStock failed - invalid stock symbol: {}", symbol);
                return buildFail(commonDto, "Stock symbol '" + symbol + "' is not valid. Please select from the stock master list.");
            }

            // ── 4. Check duplicate for same user ─────────────────────────
            if (portfolioRepository.existsByUserAndStockSymbolIgnoreCase(user, symbol)) {
                log.warn("addStock failed - stock already exists: {} for user: {}", symbol, username);
                return buildFail(commonDto, "Stock '" + symbol + "' already exists in your portfolio. Use update instead.");
            }

            // ── 5. Get company name from master if not provided ──────────
            String companyName = request.getCompanyName();
            if (companyName == null || companyName.trim().isEmpty()) {
                companyName = stockRepository.findByTickerSymbolIgnoreCase(symbol)
                        .map(s -> s.getCompanyName())
                        .orElse(symbol);
            }

            // ── 6. Save portfolio entity ─────────────────────────────────
            PortfolioEntity portfolio = PortfolioEntity.builder()
                    .user(user)
                    .stockSymbol(symbol)
                    .companyName(companyName.trim())
                    .quantity(request.getQuantity())
                    .buyPrice(request.getBuyPrice())
                    .currentPrice(request.getBuyPrice()) // default = buy price
                    .build();

            PortfolioEntity saved = portfolioRepository.save(portfolio);
            log.info("Stock added to portfolio. ID: {}, Symbol: {}, User: {}",
                    saved.getId(), saved.getStockSymbol(), username);

            commonDto.setData(toItemDto(saved));
            commonDto.setMsg("Stock '" + symbol + "' added to portfolio successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(201);

        } catch (Exception e) {
            log.error("Error in addStock: {}", e.getMessage());
            buildFail(commonDto, "Failed to add stock to portfolio.");
        }

        return commonDto;
    }

    // ── GET portfolio with valuation — Stream API ────────────────────────
    @Override
    public CommonDto<PortfolioValuationDto> getPortfolioValuation(String username) {
        log.info("Enter getPortfolioValuation() for user: {}", username);
        CommonDto<PortfolioValuationDto> commonDto = new CommonDto<>();

        try {
            // Fetch user
            Optional<UserEntity> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                log.warn("getPortfolioValuation failed - user not found: {}", username);
                commonDto.setMsg("User not found.");
                commonDto.setStatus("FAILED");
                commonDto.setStatusCode(404);
                return commonDto;
            }

            // Fetch all portfolio entities
            List<PortfolioEntity> entities = portfolioRepository.findByUser(userOpt.get());

            // ── Stream API: map entities to PortfolioItemDto ─────────────
            List<PortfolioItemDto> holdings = entities.stream()
                    .map(this::toItemDto)
                    .collect(Collectors.toList());

            // ── Stream API: compute total invested ───────────────────────
            double totalInvested = holdings.stream()
                    .mapToDouble(PortfolioItemDto::getTotalInvested)
                    .sum();

            // ── Stream API: compute total current value ──────────────────
            double totalCurrentValue = holdings.stream()
                    .mapToDouble(PortfolioItemDto::getCurrentValue)
                    .sum();

            // ── Stream API: compute total profit/loss ────────────────────
            double totalProfitLoss = holdings.stream()
                    .mapToDouble(PortfolioItemDto::getProfitLoss)
                    .sum();

            // Total P&L %
            double totalProfitLossPercent = totalInvested > 0
                    ? (totalProfitLoss / totalInvested) * 100 : 0.0;

            PortfolioValuationDto valuation = PortfolioValuationDto.builder()
                    .holdings(holdings)
                    .totalInvested(round(totalInvested))
                    .totalCurrentValue(round(totalCurrentValue))
                    .totalProfitLoss(round(totalProfitLoss))
                    .totalProfitLossPercent(round(totalProfitLossPercent))
                    .totalStocks(holdings.size())
                    .build();

            commonDto.setData(valuation);
            commonDto.setMsg("Portfolio valuation fetched successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);
            log.info("Portfolio valuation fetched for user: {}. Stocks: {}, Total Invested: {}",
                    username, holdings.size(), totalInvested);

        } catch (Exception e) {
            log.error("Error in getPortfolioValuation: {}", e.getMessage());
            commonDto.setMsg("Failed to fetch portfolio valuation.");
            commonDto.setStatus("FAILED");
            commonDto.setStatusCode(500);
        }

        return commonDto;
    }

    // ── Entity → PortfolioItemDto (with valuation) ───────────────────────
    private PortfolioItemDto toItemDto(PortfolioEntity entity) {
        double totalInvested     = entity.getQuantity() * entity.getBuyPrice();
        double currentValue      = entity.getQuantity() * entity.getCurrentPrice();
        double profitLoss        = currentValue - totalInvested;
        double profitLossPercent = totalInvested > 0 ? (profitLoss / totalInvested) * 100 : 0.0;

        return PortfolioItemDto.builder()
                .id(entity.getId())
                .stockSymbol(entity.getStockSymbol())
                .companyName(entity.getCompanyName())
                .quantity(entity.getQuantity())
                .buyPrice(entity.getBuyPrice())
                .currentPrice(entity.getCurrentPrice())
                .totalInvested(round(totalInvested))
                .currentValue(round(currentValue))
                .profitLoss(round(profitLoss))
                .profitLossPercent(round(profitLossPercent))
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .build();
    }

    // ── Helper: round to 2 decimal places ───────────────────────────────
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // ── Helper: build fail response ──────────────────────────────────────
    private <T> CommonDto<T> buildFail(CommonDto<T> dto, String msg) {
        log.warn("Portfolio operation failed: {}", msg);
        dto.setMsg(msg);
        dto.setStatus("FAILED");
        dto.setStatusCode(400);
        return dto;
    }
}
