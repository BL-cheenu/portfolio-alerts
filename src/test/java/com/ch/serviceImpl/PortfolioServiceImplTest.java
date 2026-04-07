package com.ch.serviceImpl;

import com.ch.dto.*;
import com.ch.entity.PortfolioEntity;
import com.ch.entity.StockEntity;
import com.ch.entity.UserEntity;
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
@DisplayName("PortfolioServiceImpl - US6 Unit Tests")
class PortfolioServiceImplTest {

    @Mock private PortfolioRepository portfolioRepository;
    @Mock private UserRepository userRepository;
    @Mock private StockRepository stockRepository;

    @InjectMocks private PortfolioServiceImpl portfolioService;

    private UserEntity mockUser;

    @BeforeEach
    void setUp() {
        mockUser = UserEntity.builder()
                .id(1L).name("John").username("johndoe")
                .email("john@example.com").password("hashed").build();
    }

    // ── Helper ───────────────────────────────────────────────────────────
    private CreatePortfolioRequestDto validRequest() {
        return CreatePortfolioRequestDto.builder()
                .stockSymbol("RELIANCE")
                .companyName("Reliance Industries")
                .quantity(10)
                .buyPrice(2800.0)
                .build();
    }

    private PortfolioEntity savedEntity() {
        return PortfolioEntity.builder()
                .id(1L).stockSymbol("RELIANCE").companyName("Reliance Industries")
                .quantity(10).buyPrice(2800.0).currentPrice(2800.0)
                .user(mockUser).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
    }

    // ── ADD STOCK TESTS ──────────────────────────────────────────────────

    @Test
    @DisplayName("addStock - valid request should return SUCCESS")
    void testAddStock_Success() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(stockRepository.existsByTickerSymbolIgnoreCase("RELIANCE")).thenReturn(true);
        when(portfolioRepository.existsByUserAndStockSymbolIgnoreCase(mockUser, "RELIANCE")).thenReturn(false);
        when(stockRepository.findByTickerSymbolIgnoreCase("RELIANCE"))
                .thenReturn(Optional.of(StockEntity.builder()
                        .tickerSymbol("RELIANCE").companyName("Reliance Industries").build()));
        when(portfolioRepository.save(any())).thenReturn(savedEntity());

        CommonDto<PortfolioItemDto> response = portfolioService.addStock("johndoe", validRequest());

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(201, response.getStatusCode());
        assertNotNull(response.getData());
        assertEquals("RELIANCE", response.getData().getStockSymbol());
    }

    @Test
    @DisplayName("addStock - blank stock symbol should return FAILED")
    void testAddStock_BlankSymbol() {
        CreatePortfolioRequestDto req = validRequest();
        req.setStockSymbol("  ");

        CommonDto<PortfolioItemDto> response = portfolioService.addStock("johndoe", req);

        assertEquals("FAILED", response.getStatus());
        assertEquals("Stock symbol must not be blank.", response.getMsg());
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("addStock - quantity zero should return FAILED")
    void testAddStock_ZeroQuantity() {
        CreatePortfolioRequestDto req = validRequest();
        req.setQuantity(0);

        CommonDto<PortfolioItemDto> response = portfolioService.addStock("johndoe", req);

        assertEquals("FAILED", response.getStatus());
        assertEquals("Quantity must be greater than 0.", response.getMsg());
    }

    @Test
    @DisplayName("addStock - invalid stock symbol should return FAILED")
    void testAddStock_InvalidSymbol() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(stockRepository.existsByTickerSymbolIgnoreCase("FAKESTOCK")).thenReturn(false);

        CreatePortfolioRequestDto req = validRequest();
        req.setStockSymbol("FAKESTOCK");

        CommonDto<PortfolioItemDto> response = portfolioService.addStock("johndoe", req);

        assertEquals("FAILED", response.getStatus());
        assertTrue(response.getMsg().contains("not valid"));
    }

    @Test
    @DisplayName("addStock - duplicate stock should return FAILED")
    void testAddStock_DuplicateStock() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(stockRepository.existsByTickerSymbolIgnoreCase("RELIANCE")).thenReturn(true);
        when(portfolioRepository.existsByUserAndStockSymbolIgnoreCase(mockUser, "RELIANCE")).thenReturn(true);

        CommonDto<PortfolioItemDto> response = portfolioService.addStock("johndoe", validRequest());

        assertEquals("FAILED", response.getStatus());
        assertTrue(response.getMsg().contains("already exists"));
        verify(portfolioRepository, never()).save(any());
    }

    // ── VALUATION TESTS ──────────────────────────────────────────────────

    @Test
    @DisplayName("getPortfolioValuation - Stream API computes correct totals")
    void testGetValuation_CorrectTotals() {
        PortfolioEntity e1 = PortfolioEntity.builder()
                .id(1L).stockSymbol("RELIANCE").companyName("Reliance")
                .quantity(10).buyPrice(2800.0).currentPrice(3000.0)
                .user(mockUser).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        PortfolioEntity e2 = PortfolioEntity.builder()
                .id(2L).stockSymbol("TCS").companyName("TCS")
                .quantity(5).buyPrice(3900.0).currentPrice(4000.0)
                .user(mockUser).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByUser(mockUser)).thenReturn(List.of(e1, e2));

        CommonDto<PortfolioValuationDto> response = portfolioService.getPortfolioValuation("johndoe");

        assertEquals("SUCCESS", response.getStatus());
        PortfolioValuationDto val = response.getData();

        // RELIANCE: 10*2800=28000 invested, 10*3000=30000 current
        // TCS:       5*3900=19500 invested,  5*4000=20000 current
        assertEquals(47500.0, val.getTotalInvested());
        assertEquals(50000.0, val.getTotalCurrentValue());
        assertEquals(2500.0,  val.getTotalProfitLoss());
        assertEquals(2,       val.getTotalStocks());
    }

    @Test
    @DisplayName("getPortfolioValuation - empty portfolio returns zero totals")
    void testGetValuation_EmptyPortfolio() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByUser(mockUser)).thenReturn(List.of());

        CommonDto<PortfolioValuationDto> response = portfolioService.getPortfolioValuation("johndoe");

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(0.0, response.getData().getTotalInvested());
        assertEquals(0,   response.getData().getTotalStocks());
    }

    @Test
    @DisplayName("getPortfolioValuation - user not found returns 404")
    void testGetValuation_UserNotFound() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        CommonDto<PortfolioValuationDto> response = portfolioService.getPortfolioValuation("johndoe");

        assertEquals("FAILED", response.getStatus());
        assertEquals(404, response.getStatusCode());
    }
}
