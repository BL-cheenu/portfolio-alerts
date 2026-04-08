package com.ch.serviceImpl;

import com.ch.dto.AlertRequestDto;
import com.ch.dto.AlertResponseDto;
import com.ch.dto.CommonDto;
import com.ch.entity.AlertEntity;
import com.ch.entity.PortfolioEntity;
import com.ch.entity.UserEntity;
import com.ch.repository.AlertRepository;
import com.ch.repository.PortfolioRepository;
import com.ch.repository.StockRepository;
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
@DisplayName("AlertServiceImpl - US8 Unit Tests")
class AlertServiceImplTest {

    @Mock private AlertRepository alertRepository;
    @Mock private UserRepository userRepository;
    @Mock private PortfolioRepository portfolioRepository;
    @Mock private StockRepository stockRepository;

    @InjectMocks private AlertServiceImpl alertService;

    private UserEntity mockUser;
    private AlertEntity mockAlert;
    private PortfolioEntity mockPortfolio;

    @BeforeEach
    void setUp() {
        mockUser = UserEntity.builder()
                .id(1L).username("johndoe").email("john@example.com")
                .name("John").password("hashed").build();

        mockAlert = AlertEntity.builder()
                .id(1L).stockSymbol("RELIANCE").companyName("Reliance Industries")
                .upperThreshold(10.0).lowerThreshold(5.0)
                .isActive(true).user(mockUser)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        mockPortfolio = PortfolioEntity.builder()
                .id(1L).stockSymbol("RELIANCE").companyName("Reliance Industries")
                .quantity(10).buyPrice(2800.0).currentPrice(3000.0)
                .user(mockUser).build();
    }

    private AlertRequestDto validRequest() {
        return AlertRequestDto.builder()
                .stockSymbol("RELIANCE")
                .companyName("Reliance Industries")
                .upperThreshold(10.0)
                .lowerThreshold(5.0)
                .build();
    }

    // ── SET ALERT TESTS ──────────────────────────────────────────────────

    @Test
    @DisplayName("setAlert - valid request should return SUCCESS with alert prices")
    void testSetAlert_Success() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(stockRepository.existsByTickerSymbolIgnoreCase("RELIANCE")).thenReturn(true);
        when(alertRepository.existsByUserAndStockSymbolIgnoreCase(mockUser, "RELIANCE")).thenReturn(false);
        when(stockRepository.findByTickerSymbolIgnoreCase("RELIANCE")).thenReturn(Optional.empty());
        when(alertRepository.save(any())).thenReturn(mockAlert);
        when(portfolioRepository.findByUserAndStockSymbolIgnoreCase(mockUser, "RELIANCE"))
                .thenReturn(Optional.of(mockPortfolio));

        CommonDto<AlertResponseDto> response = alertService.setAlert("johndoe", validRequest());

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(201, response.getStatusCode());
        assertNotNull(response.getData());
        assertEquals(10.0, response.getData().getUpperThreshold());
        assertEquals(5.0,  response.getData().getLowerThreshold());

        // Buy price 2800, upper 10% = 2800 + 280 = 3080
        assertEquals(3080.0, response.getData().getUpperAlertPrice());
        // Buy price 2800, lower 5%  = 2800 - 140 = 2660
        assertEquals(2660.0, response.getData().getLowerAlertPrice());
    }

    @Test
    @DisplayName("setAlert - blank stock symbol should return FAILED")
    void testSetAlert_BlankSymbol() {
        AlertRequestDto req = validRequest();
        req.setStockSymbol("  ");

        CommonDto<AlertResponseDto> response = alertService.setAlert("johndoe", req);

        assertEquals("FAILED", response.getStatus());
        assertEquals("Stock symbol must not be blank.", response.getMsg());
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("setAlert - zero upper threshold should return FAILED")
    void testSetAlert_ZeroUpperThreshold() {
        AlertRequestDto req = validRequest();
        req.setUpperThreshold(0);

        CommonDto<AlertResponseDto> response = alertService.setAlert("johndoe", req);

        assertEquals("FAILED", response.getStatus());
        assertEquals("Upper threshold must be greater than 0%.", response.getMsg());
    }

    @Test
    @DisplayName("setAlert - invalid stock should return FAILED")
    void testSetAlert_InvalidStock() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(stockRepository.existsByTickerSymbolIgnoreCase("FAKESTOCK")).thenReturn(false);

        AlertRequestDto req = validRequest();
        req.setStockSymbol("FAKESTOCK");

        CommonDto<AlertResponseDto> response = alertService.setAlert("johndoe", req);

        assertEquals("FAILED", response.getStatus());
        assertTrue(response.getMsg().contains("not valid"));
    }

    @Test
    @DisplayName("setAlert - duplicate alert should return FAILED")
    void testSetAlert_DuplicateAlert() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(stockRepository.existsByTickerSymbolIgnoreCase("RELIANCE")).thenReturn(true);
        when(alertRepository.existsByUserAndStockSymbolIgnoreCase(mockUser, "RELIANCE")).thenReturn(true);

        CommonDto<AlertResponseDto> response = alertService.setAlert("johndoe", validRequest());

        assertEquals("FAILED", response.getStatus());
        assertTrue(response.getMsg().contains("already exists"));
        verify(alertRepository, never()).save(any());
    }

    // ── UPDATE ALERT TESTS ───────────────────────────────────────────────

    @Test
    @DisplayName("updateAlert - update upper threshold should succeed")
    void testUpdateAlert_Success() {
        AlertRequestDto req = AlertRequestDto.builder().upperThreshold(15.0).build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(alertRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.of(mockAlert));

        AlertEntity updated = AlertEntity.builder()
                .id(1L).stockSymbol("RELIANCE").companyName("Reliance Industries")
                .upperThreshold(15.0).lowerThreshold(5.0).isActive(true)
                .user(mockUser).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        when(alertRepository.save(any())).thenReturn(updated);
        when(portfolioRepository.findByUserAndStockSymbolIgnoreCase(mockUser, "RELIANCE"))
                .thenReturn(Optional.of(mockPortfolio));

        CommonDto<AlertResponseDto> response = alertService.updateAlert("johndoe", 1L, req);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(15.0, response.getData().getUpperThreshold());
    }

    @Test
    @DisplayName("updateAlert - alert not found should return FAILED")
    void testUpdateAlert_NotFound() {
        AlertRequestDto req = AlertRequestDto.builder().upperThreshold(10.0).build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(alertRepository.findByIdAndUser(99L, mockUser)).thenReturn(Optional.empty());

        CommonDto<AlertResponseDto> response = alertService.updateAlert("johndoe", 99L, req);

        assertEquals("FAILED", response.getStatus());
        assertEquals("Alert not found.", response.getMsg());
    }

    // ── GET ALL TESTS ────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllAlerts - should return list of alerts")
    void testGetAllAlerts_Success() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(alertRepository.findByUser(mockUser)).thenReturn(List.of(mockAlert));
        when(portfolioRepository.findByUserAndStockSymbolIgnoreCase(mockUser, "RELIANCE"))
                .thenReturn(Optional.of(mockPortfolio));

        CommonDto<List<AlertResponseDto>> response = alertService.getAllAlerts("johndoe");

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(1, response.getDataList().size());
        assertEquals("RELIANCE", response.getDataList().get(0).getStockSymbol());
    }

    // ── DELETE TESTS ─────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteAlert - should delete and return SUCCESS")
    void testDeleteAlert_Success() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(alertRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.of(mockAlert));
        doNothing().when(alertRepository).delete(mockAlert);

        CommonDto<AlertResponseDto> response = alertService.deleteAlert("johndoe", 1L);

        assertEquals("SUCCESS", response.getStatus());
        assertTrue(response.getMsg().contains("RELIANCE"));
        verify(alertRepository).delete(mockAlert);
    }

    @Test
    @DisplayName("deleteAlert - not found should return FAILED")
    void testDeleteAlert_NotFound() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(alertRepository.findByIdAndUser(99L, mockUser)).thenReturn(Optional.empty());

        CommonDto<AlertResponseDto> response = alertService.deleteAlert("johndoe", 99L);

        assertEquals("FAILED", response.getStatus());
        assertEquals("Alert not found.", response.getMsg());
    }
}
