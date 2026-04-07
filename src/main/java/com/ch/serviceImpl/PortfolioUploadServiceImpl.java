package com.ch.serviceImpl;

import com.ch.dto.*;
import com.ch.entity.PortfolioEntity;
import com.ch.entity.UserEntity;
import com.ch.repository.PortfolioRepository;
import com.ch.repository.StockRepository;
import com.ch.repository.UserRepository;
import com.ch.service.PortfolioUploadService;
import com.ch.utils.ExcelParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * US5 - Portfolio Upload Service
 *
 * Flow:
 *  Step 1 — previewUpload()
 *    - Parse .xls file using Apache POI
 *    - Validate each ticker against stocks master table
 *    - Classify: new / conflict / invalid
 *    - Return preview to user
 *
 *  Step 2 — confirmUpload()
 *    - User sends consent (which conflicts to update)
 *    - Add new stocks
 *    - Update conflict stocks
 *    - Return result summary
 */
@Service
public class PortfolioUploadServiceImpl implements PortfolioUploadService {

    private static final Logger log = LoggerFactory.getLogger(PortfolioUploadServiceImpl.class);

    @Autowired
    private ExcelParserUtil excelParserUtil;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private UserRepository userRepository;

    // ── STEP 1: Parse + Preview ──────────────────────────────────────────
    @Override
    public CommonDto<UploadPreviewDto> previewUpload(String username, MultipartFile file) {
        log.info("Enter previewUpload() for user: {}", username);
        CommonDto<UploadPreviewDto> commonDto = new CommonDto<>();

        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                return buildFail(commonDto, "File must not be empty.");
            }

            // Fetch user
            Optional<UserEntity> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return buildFail(commonDto, "User not found.");
            }
            UserEntity user = userOpt.get();

            // Parse excel
            List<UploadRowDto> parsedRows = excelParserUtil.parseExcel(file);
            if (parsedRows.isEmpty()) {
                return buildFail(commonDto, "No valid rows found in the uploaded file.");
            }

            // Classify rows
            List<UploadRowDto> newStocks      = new ArrayList<>();
            List<UploadRowDto> conflictStocks = new ArrayList<>();
            List<String> invalidStocks        = new ArrayList<>();

            for (UploadRowDto row : parsedRows) {
                // ── Validate against stock master table ──────────────────
                boolean validStock = stockRepository
                        .existsByTickerSymbolIgnoreCase(row.getStockSymbol());

                if (!validStock) {
                    log.warn("Invalid stock symbol: {}", row.getStockSymbol());
                    invalidStocks.add(row.getStockSymbol());
                    continue;
                }

                // ── Check if already in user portfolio ───────────────────
                boolean exists = portfolioRepository
                        .existsByUserAndStockSymbolIgnoreCase(user, row.getStockSymbol());

                if (exists) {
                    conflictStocks.add(row); // already exists — need user consent
                } else {
                    newStocks.add(row);      // new stock — will be added
                }
            }

            UploadPreviewDto preview = UploadPreviewDto.builder()
                    .newStocks(newStocks)
                    .conflictStocks(conflictStocks)
                    .invalidStocks(invalidStocks)
                    .totalRows(parsedRows.size())
                    .newCount(newStocks.size())
                    .conflictCount(conflictStocks.size())
                    .invalidCount(invalidStocks.size())
                    .build();

            commonDto.setData(preview);
            commonDto.setMsg("Preview generated. Please confirm to proceed.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);
            log.info("Preview: new={}, conflict={}, invalid={}",
                    newStocks.size(), conflictStocks.size(), invalidStocks.size());

        } catch (Exception e) {
            log.error("Error in previewUpload: {}", e.getMessage());
            buildFail(commonDto, "Failed to parse file: " + e.getMessage());
        }

        return commonDto;
    }

    // ── STEP 2: Confirm + Save ───────────────────────────────────────────
    @Override
    @Transactional
    public CommonDto<UploadResultDto> confirmUpload(String username, UploadConfirmDto confirmDto) {
        log.info("Enter confirmUpload() for user: {}", username);
        CommonDto<UploadResultDto> commonDto = new CommonDto<>();

        try {
            Optional<UserEntity> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return buildFailResult(commonDto, "User not found.");
            }
            UserEntity user = userOpt.get();

            int addedCount   = 0;
            int updatedCount = 0;

            // ── ADD new stocks ───────────────────────────────────────────
            if (confirmDto.getNewStocks() != null) {
                for (UploadRowDto row : confirmDto.getNewStocks()) {
                    PortfolioEntity entity = PortfolioEntity.builder()
                            .user(user)
                            .stockSymbol(row.getStockSymbol().toUpperCase())
                            .companyName(row.getCompanyName())
                            .quantity(row.getQuantity())
                            .buyPrice(row.getBuyPrice())
                            .build();
                    portfolioRepository.save(entity);
                    addedCount++;
                    log.info("Added: {} for user: {}", row.getStockSymbol(), username);
                }
            }

            // ── UPDATE conflict stocks (user consented) ──────────────────
            if (confirmDto.getUpdateStocks() != null) {
                for (UploadRowDto row : confirmDto.getUpdateStocks()) {
                    Optional<PortfolioEntity> existing = portfolioRepository
                            .findByUserAndStockSymbolIgnoreCase(user, row.getStockSymbol());

                    if (existing.isPresent()) {
                        PortfolioEntity entity = existing.get();
                        entity.setQuantity(row.getQuantity());
                        entity.setBuyPrice(row.getBuyPrice());
                        if (row.getCompanyName() != null && !row.getCompanyName().isEmpty()) {
                            entity.setCompanyName(row.getCompanyName());
                        }
                        portfolioRepository.save(entity);
                        updatedCount++;
                        log.info("Updated: {} for user: {}", row.getStockSymbol(), username);
                    }
                }
            }

            int skipped = 0;
            String msg = String.format(
                    "Upload complete. Added: %d, Updated: %d, Skipped: %d",
                    addedCount, updatedCount, skipped);

            UploadResultDto result = UploadResultDto.builder()
                    .addedCount(addedCount)
                    .updatedCount(updatedCount)
                    .skippedCount(skipped)
                    .message(msg)
                    .build();

            commonDto.setData(result);
            commonDto.setMsg(msg);
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);
            log.info("confirmUpload done. Added: {}, Updated: {}", addedCount, updatedCount);

        } catch (Exception e) {
            log.error("Error in confirmUpload: {}", e.getMessage());
            buildFailResult(commonDto, "Upload failed: " + e.getMessage());
        }

        return commonDto;
    }

    // ── Helpers ──────────────────────────────────────────────────────────
    private <T> CommonDto<T> buildFail(CommonDto<T> dto, String msg) {
        dto.setMsg(msg);
        dto.setStatus("FAILED");
        dto.setStatusCode(400);
        return dto;
    }

    private <T> CommonDto<T> buildFailResult(CommonDto<T> dto, String msg) {
        dto.setMsg(msg);
        dto.setStatus("FAILED");
        dto.setStatusCode(400);
        return dto;
    }
}
