package com.ch.serviceImpl;

import com.ch.dto.CommonDto;
import com.ch.dto.MonitorPortfolioDto;
import com.ch.dto.MonitorStockDto;
import com.ch.entity.AlertEntity;
import com.ch.entity.PortfolioEntity;
import com.ch.entity.UserEntity;
import com.ch.kafka.StockPriceCache;
import com.ch.kafka.StockPriceMessage;
import com.ch.repository.AlertRepository;
import com.ch.repository.PortfolioRepository;
import com.ch.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MonitorServiceImpl - US9 Unit Tests")
class MonitorServiceImplTest {

    @Mock private PortfolioRepository portfolioRepository;
    @Mock private AlertRepository alertRepository;
    @Mock private UserRepository userRepository;
    @Mock private StockPriceCache stockPriceCache;

    @InjectMocks private MonitorServiceImpl monitorService;

    private UserEntity mockUser;
    private PortfolioEntity mockPortfolio;
    private AlertEntity mockAlert;

    @BeforeEach
    void setUp() {
        mockUser = UserEntity.builder()
                .id(1L).username("johndoe").email("john@example.com")
                .name("John").password("hashed").build();

        mockPortfolio = PortfolioEntity.builder()
                .id(1L).stockSymbol("RELIANCE").companyName("Reliance Industries")
                .quantity(10).buyPrice(2800.0).currentPrice(2800.0)
                .user(mockUser).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        mockAlert = AlertEntity.builder()
                .id(1L).stockSymbol("RELIANCE").companyName("Reliance Industries")
                .upperThreshold(10.0).lowerThreshold(5.0)
                .isActive(true).user(mockUser)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
    }

    // ── MONITOR PORTFOLIO TESTS ──────────────────────────────────────────

    @Test
    @DisplayName("monitorPortfolio - should compute correct gain/loss")
    void testMonitorPortfolio_GainLoss() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByUser(mockUser)).thenReturn(List.of(mockPortfolio));
        when(alertRepository.findByUser(mockUser)).thenReturn(List.of());
        // Live price = 3000 (gain of 200 per share)
        when(stockPriceCache.getCurrentPrice("RELIANCE", 2800.0)).thenReturn(3000.0);

        CommonDto<MonitorPortfolioDto> response = monitorService.monitorPortfolio("johndoe");

        assertEquals("SUCCESS", response.getStatus());
        MonitorPortfolioDto data = response.getData();
        assertEquals(1, data.getTotalStocks());
        assertEquals(28000.0, data.getTotalInvested());    // 10 * 2800
        assertEquals(30000.0, data.getTotalCurrentValue()); // 10 * 3000
        assertEquals(2000.0,  data.getTotalGainLoss());    // 30000 - 28000
    }

    @Test
    @DisplayName("monitorPortfolio - should detect UPPER threshold breach")
    void testMonitorPortfolio_UpperBreach() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByUser(mockUser)).thenReturn(List.of(mockPortfolio));
        when(alertRepository.findByUser(mockUser)).thenReturn(List.of(mockAlert));
        // upperAlertPrice = 2800 + 280 = 3080, current = 3100 → BREACHED
        when(stockPriceCache.getCurrentPrice("RELIANCE", 2800.0)).thenReturn(3100.0);

        CommonDto<MonitorPortfolioDto> response = monitorService.monitorPortfolio("johndoe");

        assertEquals("SUCCESS", response.getStatus());
        MonitorStockDto stock = response.getData().getStocks().get(0);
        assertTrue(stock.isUpperBreached());
        assertFalse(stock.isLowerBreached());
        assertEquals("UPPER_BREACHED", stock.getAlertStatus());
        assertEquals(1, response.getData().getUpperBreachedCount());
    }

    @Test
    @DisplayName("monitorPortfolio - should detect LOWER threshold breach")
    void testMonitorPortfolio_LowerBreach() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByUser(mockUser)).thenReturn(List.of(mockPortfolio));
        when(alertRepository.findByUser(mockUser)).thenReturn(List.of(mockAlert));
        // lowerAlertPrice = 2800 - 140 = 2660, current = 2600 → BREACHED
        when(stockPriceCache.getCurrentPrice("RELIANCE", 2800.0)).thenReturn(2600.0);

        CommonDto<MonitorPortfolioDto> response = monitorService.monitorPortfolio("johndoe");

        assertEquals("SUCCESS", response.getStatus());
        MonitorStockDto stock = response.getData().getStocks().get(0);
        assertFalse(stock.isUpperBreached());
        assertTrue(stock.isLowerBreached());
        assertEquals("LOWER_BREACHED", stock.getAlertStatus());
        assertEquals(1, response.getData().getLowerBreachedCount());
    }

    @Test
    @DisplayName("monitorPortfolio - NORMAL when within threshold")
    void testMonitorPortfolio_Normal() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByUser(mockUser)).thenReturn(List.of(mockPortfolio));
        when(alertRepository.findByUser(mockUser)).thenReturn(List.of(mockAlert));
        // upper=3080, lower=2660, current=2900 → NORMAL
        when(stockPriceCache.getCurrentPrice("RELIANCE", 2800.0)).thenReturn(2900.0);

        CommonDto<MonitorPortfolioDto> response = monitorService.monitorPortfolio("johndoe");

        assertEquals("SUCCESS", response.getStatus());
        MonitorStockDto stock = response.getData().getStocks().get(0);
        assertEquals("NORMAL", stock.getAlertStatus());
        assertEquals(1, response.getData().getNormalCount());
    }

    @Test
    @DisplayName("monitorPortfolio - no alert set shows NO_ALERT_SET")
    void testMonitorPortfolio_NoAlertSet() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByUser(mockUser)).thenReturn(List.of(mockPortfolio));
        when(alertRepository.findByUser(mockUser)).thenReturn(List.of()); // no alerts
        when(stockPriceCache.getCurrentPrice("RELIANCE", 2800.0)).thenReturn(3000.0);

        CommonDto<MonitorPortfolioDto> response = monitorService.monitorPortfolio("johndoe");

        assertEquals("SUCCESS", response.getStatus());
        assertEquals("NO_ALERT_SET", response.getData().getStocks().get(0).getAlertStatus());
    }

    @Test
    @DisplayName("monitorPortfolio - empty portfolio returns 404")
    void testMonitorPortfolio_EmptyPortfolio() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByUser(mockUser)).thenReturn(List.of());

        CommonDto<MonitorPortfolioDto> response = monitorService.monitorPortfolio("johndoe");

        assertEquals("FAILED", response.getStatus());
        assertEquals(404, response.getStatusCode());
    }

    @Test
    @DisplayName("monitorPortfolio - user not found returns FAILED")
    void testMonitorPortfolio_UserNotFound() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        CommonDto<MonitorPortfolioDto> response = monitorService.monitorPortfolio("johndoe");

        assertEquals("FAILED", response.getStatus());
    }

    // ── MONITOR SINGLE STOCK TESTS ───────────────────────────────────────

    @Test
    @DisplayName("monitorStock - should return correct data for single stock")
    void testMonitorStock_Success() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByUserAndStockSymbolIgnoreCase(mockUser, "RELIANCE"))
                .thenReturn(Optional.of(mockPortfolio));
        when(alertRepository.findByUser(mockUser)).thenReturn(List.of(mockAlert));
        when(stockPriceCache.getCurrentPrice("RELIANCE", 2800.0)).thenReturn(3000.0);

        CommonDto<MonitorStockDto> response = monitorService.monitorStock("johndoe", "RELIANCE");

        assertEquals("SUCCESS", response.getStatus());
        assertEquals("RELIANCE", response.getData().getStockSymbol());
        assertEquals(3000.0, response.getData().getCurrentPrice());
        assertEquals(2000.0, response.getData().getGainLoss()); // 10*(3000-2800)
    }

    @Test
    @DisplayName("monitorStock - stock not in portfolio returns FAILED")
    void testMonitorStock_NotFound() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByUserAndStockSymbolIgnoreCase(mockUser, "WIPRO"))
                .thenReturn(Optional.empty());

        CommonDto<MonitorStockDto> response = monitorService.monitorStock("johndoe", "WIPRO");

        assertEquals("FAILED", response.getStatus());
        assertTrue(response.getMsg().contains("not found in portfolio"));
    }
}