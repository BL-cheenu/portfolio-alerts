package com.ch.serviceImpl;

import com.ch.dto.CommonDto;
import com.ch.dto.HomeResponseDto;
import com.ch.dto.StockDto;
import com.ch.service.HomeService;
import com.ch.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * US3 - Home page service
 * Returns menu options + NSE Top 50 ticker for authenticated user
 */
@Service
public class HomeServiceImpl implements HomeService {
    private static final Logger log = LoggerFactory.getLogger(HomeServiceImpl.class);

    @Autowired
    private StockService stockService;

    // ── Menu options as per US3 requirement ─────────────────────────────
    private static final List<String> MENU_OPTIONS = List.of(
            "Portfolio Creation / Updation",
            "Alert Setting",
            "Monitor Portfolio"
    );

    @Override
    public CommonDto<HomeResponseDto> getHomePage(String username) {
        log.info("Enter into HomeServiceImpl getHomePage() for user: {}", username);

        CommonDto<HomeResponseDto> commonDto = new CommonDto<>();

        try {
            // ── Fetch NSE Top 50 real-time prices ───────────────────────
            log.info("Fetching NSE Top 50 stock ticker data");
            List<StockDto> ticker = stockService.getNseTop50Prices();

            // ── Build home response ──────────────────────────────────────
            HomeResponseDto homeResponse = HomeResponseDto.builder()
                    .welcomeMessage("Welcome to Portfolio Alerts App!")
                    .username(username)
                    .menuOptions(MENU_OPTIONS)
                    .nseTop50Ticker(ticker)
                    .build();

            commonDto.setData(homeResponse);
            commonDto.setMsg("Home page loaded successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);

            log.info("Exit from HomeServiceImpl getHomePage() for user: {}", username);

        } catch (Exception ex) {
            log.error("Error loading home page: {}", ex.getMessage());
            commonDto.setMsg("Failed to load home page.");
            commonDto.setStatus("FAILED");
            commonDto.setStatusCode(500);
        }

        return commonDto;
    }
}
