package com.ch.serviceImpl;

import com.ch.dto.*;
import com.ch.entity.PortfolioEntity;
import com.ch.entity.UserEntity;
import com.ch.repository.PortfolioRepository;
import com.ch.repository.UserRepository;
import com.ch.service.ManagePortfolioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * US7 - Manage Portfolio Service
 *
 * Key Concepts:
 *  - PUT HTTP for update
 *  - @Transactional for DB transaction management
 *  - Rollback on exception (Spring default for RuntimeException)
 *  - Stream API for portfolio valuation
 */
@Service
public class ManagePortfolioServiceImpl implements ManagePortfolioService {

    private static final Logger log = LoggerFactory.getLogger(ManagePortfolioServiceImpl.class);

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private UserRepository userRepository;

    // ── US7.1 — View full portfolio with valuation ───────────────────────
    @Override
    @Transactional(readOnly = true)
    public CommonDto<PortfolioValuationDto> getPortfolio(String username) {
        log.info("Enter getPortfolio() for user: {}", username);
        CommonDto<PortfolioValuationDto> commonDto = new CommonDto<>();

        try {
            UserEntity user = fetchUser(username);

            List<PortfolioEntity> entities = portfolioRepository.findByUser(user);
            log.info("Fetched {} portfolio records for user: {}", entities.size(), username);

            // Stream API — map to DTO with valuation
            List<PortfolioItemDto> holdings = entities.stream()
                    .map(this::toItemDto)
                    .collect(Collectors.toList());

            // Stream API — compute totals
            double totalInvested     = holdings.stream().mapToDouble(PortfolioItemDto::getTotalInvested).sum();
            double totalCurrentValue = holdings.stream().mapToDouble(PortfolioItemDto::getCurrentValue).sum();
            double totalProfitLoss   = holdings.stream().mapToDouble(PortfolioItemDto::getProfitLoss).sum();
            double totalPLPercent    = totalInvested > 0 ? (totalProfitLoss / totalInvested) * 100 : 0.0;

            PortfolioValuationDto valuation = PortfolioValuationDto.builder()
                    .holdings(holdings)
                    .totalInvested(round(totalInvested))
                    .totalCurrentValue(round(totalCurrentValue))
                    .totalProfitLoss(round(totalProfitLoss))
                    .totalProfitLossPercent(round(totalPLPercent))
                    .totalStocks(holdings.size())
                    .build();

            commonDto.setData(valuation);
            commonDto.setMsg("Portfolio fetched successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);

        } catch (Exception e) {
            log.error("Error in getPortfolio: {}", e.getMessage());
            commonDto.setMsg(e.getMessage());
            commonDto.setStatus("FAILED");
            commonDto.setStatusCode(400);
        }

        return commonDto;
    }

    // ── US7.2 — Update one stock (quantity / buy price) ──────────────────
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonDto<PortfolioItemDto> updateStock(String username, Long portfolioId,
                                                   UpdatePortfolioRequestDto request) {
        log.info("Enter updateStock() for user: {}, portfolioId: {}", username, portfolioId);
        CommonDto<PortfolioItemDto> commonDto = new CommonDto<>();

        try {
            // ── Validate request ─────────────────────────────────────────
            if ((request.getQuantity() == null || request.getQuantity() <= 0) &&
                    (request.getBuyPrice() == null || request.getBuyPrice() <= 0)) {
                log.warn("updateStock failed - no valid fields to update for portfolioId: {}", portfolioId);
                return buildFail(commonDto, "Provide at least quantity or buy price to update.");
            }

            UserEntity user = fetchUser(username);

            // ── Fetch portfolio by ID and user ───────────────────────────
            Optional<PortfolioEntity> portfolioOpt =
                    portfolioRepository.findByIdAndUser(portfolioId, user);

            if (portfolioOpt.isEmpty()) {
                log.warn("updateStock failed - portfolio not found. ID: {}, user: {}", portfolioId, username);
                return buildFail(commonDto, "Portfolio record not found.");
            }

            PortfolioEntity portfolio = portfolioOpt.get();
            String symbol = portfolio.getStockSymbol();

            // ── Apply updates (only provided fields) ─────────────────────
            if (request.getQuantity() != null && request.getQuantity() > 0) {
                log.info("Updating quantity: {} → {} for stock: {}", portfolio.getQuantity(), request.getQuantity(), symbol);
                portfolio.setQuantity(request.getQuantity());
            }
            if (request.getBuyPrice() != null && request.getBuyPrice() > 0) {
                log.info("Updating buyPrice: {} → {} for stock: {}", portfolio.getBuyPrice(), request.getBuyPrice(), symbol);
                portfolio.setBuyPrice(request.getBuyPrice());
            }

            // ── Save — @Transactional handles commit/rollback ────────────
            PortfolioEntity updated = portfolioRepository.save(portfolio);
            log.info("Stock updated successfully. ID: {}, Symbol: {}, User: {}", updated.getId(), symbol, username);

            commonDto.setData(toItemDto(updated));
            commonDto.setMsg("Stock '" + symbol + "' updated successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);

        } catch (Exception e) {
            // @Transactional will auto rollback for RuntimeException
            log.error("Error in updateStock — transaction rolled back: {}", e.getMessage());
            commonDto.setMsg("Update failed. Transaction rolled back.");
            commonDto.setStatus("FAILED");
            commonDto.setStatusCode(500);
            throw new RuntimeException(e); // trigger rollback
        }

        return commonDto;
    }

    // ── US7.3 — Delete one stock ─────────────────────────────────────────
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonDto<PortfolioItemDto> deleteStock(String username, Long portfolioId) {
        log.info("Enter deleteStock() for user: {}, portfolioId: {}", username, portfolioId);
        CommonDto<PortfolioItemDto> commonDto = new CommonDto<>();

        try {
            UserEntity user = fetchUser(username);

            Optional<PortfolioEntity> portfolioOpt =
                    portfolioRepository.findByIdAndUser(portfolioId, user);

            if (portfolioOpt.isEmpty()) {
                log.warn("deleteStock failed - portfolio not found. ID: {}, user: {}", portfolioId, username);
                return buildFail(commonDto, "Portfolio record not found.");
            }

            PortfolioEntity portfolio = portfolioOpt.get();
            String symbol = portfolio.getStockSymbol();

            portfolioRepository.delete(portfolio);
            log.info("Stock deleted. ID: {}, Symbol: {}, User: {}", portfolioId, symbol, username);

            commonDto.setMsg("Stock '" + symbol + "' deleted successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);

        } catch (Exception e) {
            log.error("Error in deleteStock — transaction rolled back: {}", e.getMessage());
            commonDto.setMsg("Delete failed. Transaction rolled back.");
            commonDto.setStatus("FAILED");
            commonDto.setStatusCode(500);
            throw new RuntimeException(e);
        }

        return commonDto;
    }

    // ── US7.4 — Delete ALL stocks ────────────────────────────────────────
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonDto<PortfolioItemDto> deleteAllStocks(String username) {
        log.info("Enter deleteAllStocks() for user: {}", username);
        CommonDto<PortfolioItemDto> commonDto = new CommonDto<>();

        try {
            UserEntity user = fetchUser(username);

            List<PortfolioEntity> all = portfolioRepository.findByUser(user);

            if (all.isEmpty()) {
                log.info("deleteAllStocks - no stocks to delete for user: {}", username);
                return buildFail(commonDto, "No portfolio records found to delete.");
            }

            portfolioRepository.deleteAll(all);
            log.info("All {} stocks deleted for user: {}", all.size(), username);

            commonDto.setMsg("All " + all.size() + " stocks deleted successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);

        } catch (Exception e) {
            log.error("Error in deleteAllStocks — transaction rolled back: {}", e.getMessage());
            commonDto.setMsg("Delete all failed. Transaction rolled back.");
            commonDto.setStatus("FAILED");
            commonDto.setStatusCode(500);
            throw new RuntimeException(e);
        }

        return commonDto;
    }

    // ── Fetch user helper — throws if not found ──────────────────────────
    private UserEntity fetchUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new RuntimeException("User not found.");
                });
    }

    // ── Entity → PortfolioItemDto with valuation ─────────────────────────
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