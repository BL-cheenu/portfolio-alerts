package com.ch.utils;

import com.ch.dto.UploadRowDto;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * US5 - Apache POI Excel Parser
 *
 * Supports: .xls (HSSFWorkbook) and .xlsx (XSSFWorkbook)
 *
 * Expected Excel Format:
 * | Stock Symbol | Company Name | Quantity | Buy Price |
 * |--------------|--------------|----------|-----------|
 * | RELIANCE     | Reliance...  | 10       | 2800.00   |
 */
@Component
public class ExcelParserUtil {

    private static final Logger log = LoggerFactory.getLogger(ExcelParserUtil.class);

    public List<UploadRowDto> parseExcel(MultipartFile file) throws Exception {
        log.info("Parsing excel file: {}", file.getOriginalFilename());

        List<UploadRowDto> rows = new ArrayList<>();
        String filename = file.getOriginalFilename();

        if (filename == null ||
                (!filename.endsWith(".xls") && !filename.endsWith(".xlsx"))) {
            throw new IllegalArgumentException("Only .xls and .xlsx files are supported.");
        }

        try (InputStream is = file.getInputStream()) {
            Workbook workbook;

            // ── Choose workbook type ─────────────────────────────────────
            if (filename.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(is);
            } else {
                workbook = new HSSFWorkbook(is);
            }

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows();
            log.info("Total rows in excel (including header): {}", totalRows);

            // ── Skip header row (row 0) ──────────────────────────────────
            for (int i = 1; i < totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String stockSymbol = getCellValue(row.getCell(0)).trim().toUpperCase();
                    String companyName = getCellValue(row.getCell(1)).trim();
                    int quantity       = (int) getNumericCellValue(row.getCell(2));
                    double buyPrice    = getNumericCellValue(row.getCell(3));

                    if (stockSymbol.isEmpty()) {
                        log.warn("Row {} - empty stock symbol, skipping", i + 1);
                        continue;
                    }
                    if (quantity <= 0 || buyPrice <= 0) {
                        log.warn("Row {} - invalid quantity/price for {}", i + 1, stockSymbol);
                        continue;
                    }

                    rows.add(UploadRowDto.builder()
                            .stockSymbol(stockSymbol)
                            .companyName(companyName)
                            .quantity(quantity)
                            .buyPrice(buyPrice)
                            .build());

                } catch (Exception e) {
                    log.warn("Row {} parse error: {}", i + 1, e.getMessage());
                }
            }

            workbook.close();
        }

        log.info("Parsed {} valid rows from excel", rows.size());
        return rows;
    }

    // ── Get string cell value ────────────────────────────────────────────
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default      -> "";
        };
    }

    // ── Get numeric cell value ───────────────────────────────────────────
    private double getNumericCellValue(Cell cell) {
        if (cell == null) return 0;
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING  -> {
                try { yield Double.parseDouble(cell.getStringCellValue().trim()); }
                catch (Exception e) { yield 0; }
            }
            default -> 0;
        };
    }
}
