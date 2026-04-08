package com.ch.serviceImpl;

import com.ch.dto.*;
import com.ch.entity.PortfolioEntity;
import com.ch.entity.UserEntity;
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
@DisplayName("ManagePortfolioServiceImpl - US7 Unit Tests")
class ManagePortfolioServiceImplTest {

    @Mock private PortfolioRepository portfolioRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ManagePortfolioServiceImpl managePortfolioService;

    private UserEntity mockUser;
    private PortfolioEntity mockPortfolio;

    @BeforeEach
    void setUp() {
        mockUser = UserEntity.builder()
                .id(1L).name("John").username("johndoe")
                .email("john@example.com").password("hashed").build();

        mockPortfolio = PortfolioEntity.builder()
                .id(1L).stockSymbol("RELIANCE").companyName("Reliance Industries")
                .quantity(10).buyPrice(2800.0).currentPrice(3000.0)
                .user(mockUser).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
    }

    // ── GET PORTFOLIO TESTS ──────────────────────────────────────────────

    @Test
    @DisplayName("getPortfolio - should return portfolio with correct valuation")
    void testGetPortfolio_Success() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByUser(mockUser)).thenReturn(List.of(mockPortfolio));

        CommonDto<PortfolioValuationDto> response = managePortfolioService.getPortfolio("johndoe");

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(1, response.getData().getTotalStocks());
        // 10 * 2800 = 28000 invested, 10 * 3000 = 30000 current
        assertEquals(28000.0, response.getData().getTotalInvested());
        assertEquals(30000.0, response.getData().getTotalCurrentValue());
        assertEquals(2000.0,  response.getData().getTotalProfitLoss());
    }

    @Test
    @DisplayName("getPortfolio - empty portfolio returns zero totals")
    void testGetPortfolio_Empty() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByUser(mockUser)).thenReturn(List.of());

        CommonDto<PortfolioValuationDto> response = managePortfolioService.getPortfolio("johndoe");

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(0, response.getData().getTotalStocks());
        assertEquals(0.0, response.getData().getTotalInvested());
    }

    @Test
    @DisplayName("getPortfolio - user not found returns FAILED")
    void testGetPortfolio_UserNotFound() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        CommonDto<PortfolioValuationDto> response = managePortfolioService.getPortfolio("johndoe");

        assertEquals("FAILED", response.getStatus());
    }

    // ── UPDATE TESTS ─────────────────────────────────────────────────────

    @Test
    @DisplayName("updateStock - update quantity should succeed")
    void testUpdateStock_QuantitySuccess() {
        UpdatePortfolioRequestDto request = UpdatePortfolioRequestDto.builder()
                .quantity(20).build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.of(mockPortfolio));

        PortfolioEntity updated = PortfolioEntity.builder()
                .id(1L).stockSymbol("RELIANCE").companyName("Reliance Industries")
                .quantity(20).buyPrice(2800.0).currentPrice(3000.0)
                .user(mockUser).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        when(portfolioRepository.save(any())).thenReturn(updated);

        CommonDto<PortfolioItemDto> response = managePortfolioService.updateStock("johndoe", 1L, request);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(20, response.getData().getQuantity());
        verify(portfolioRepository).save(any());
    }

    @Test
    @DisplayName("updateStock - update buy price should succeed")
    void testUpdateStock_BuyPriceSuccess() {
        UpdatePortfolioRequestDto request = UpdatePortfolioRequestDto.builder()
                .buyPrice(2500.0).build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.of(mockPortfolio));

        PortfolioEntity updated = PortfolioEntity.builder()
                .id(1L).stockSymbol("RELIANCE").companyName("Reliance Industries")
                .quantity(10).buyPrice(2500.0).currentPrice(3000.0)
                .user(mockUser).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        when(portfolioRepository.save(any())).thenReturn(updated);

        CommonDto<PortfolioItemDto> response = managePortfolioService.updateStock("johndoe", 1L, request);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(2500.0, response.getData().getBuyPrice());
    }

    @Test
    @DisplayName("updateStock - no valid fields should return FAILED")
    void testUpdateStock_NoFields() {
        UpdatePortfolioRequestDto request = new UpdatePortfolioRequestDto(); // all null

        CommonDto<PortfolioItemDto> response = managePortfolioService.updateStock("johndoe", 1L, request);

        assertEquals("FAILED", response.getStatus());
        assertEquals("Provide at least quantity or buy price to update.", response.getMsg());
        verifyNoInteractions(portfolioRepository);
    }

    @Test
    @DisplayName("updateStock - portfolio not found should return FAILED")
    void testUpdateStock_NotFound() {
        UpdatePortfolioRequestDto request = UpdatePortfolioRequestDto.builder().quantity(5).build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByIdAndUser(99L, mockUser)).thenReturn(Optional.empty());

        CommonDto<PortfolioItemDto> response = managePortfolioService.updateStock("johndoe", 99L, request);

        assertEquals("FAILED", response.getStatus());
        assertEquals("Portfolio record not found.", response.getMsg());
        verify(portfolioRepository, never()).save(any());
    }

    // ── DELETE ONE TESTS ─────────────────────────────────────────────────

    @Test
    @DisplayName("deleteStock - should delete and return SUCCESS")
    void testDeleteStock_Success() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.of(mockPortfolio));
        doNothing().when(portfolioRepository).delete(mockPortfolio);

        CommonDto<PortfolioItemDto> response = managePortfolioService.deleteStock("johndoe", 1L);

        assertEquals("SUCCESS", response.getStatus());
        assertTrue(response.getMsg().contains("RELIANCE"));
        verify(portfolioRepository).delete(mockPortfolio);
    }

    @Test
    @DisplayName("deleteStock - not found should return FAILED")
    void testDeleteStock_NotFound() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByIdAndUser(99L, mockUser)).thenReturn(Optional.empty());

        CommonDto<PortfolioItemDto> response = managePortfolioService.deleteStock("johndoe", 99L);

        assertEquals("FAILED", response.getStatus());
        verify(portfolioRepository, never()).delete(any(PortfolioEntity.class));
    }

    // ── DELETE ALL TESTS ─────────────────────────────────────────────────

    @Test
    @DisplayName("deleteAllStocks - should delete all and return SUCCESS")
    void testDeleteAllStocks_Success() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByUser(mockUser)).thenReturn(List.of(mockPortfolio));
        doNothing().when(portfolioRepository).deleteAll(any());

        CommonDto<PortfolioItemDto> response = managePortfolioService.deleteAllStocks("johndoe");

        assertEquals("SUCCESS", response.getStatus());
        assertTrue(response.getMsg().contains("1"));
        verify(portfolioRepository).deleteAll(any());
    }

    @Test
    @DisplayName("deleteAllStocks - empty portfolio should return FAILED")
    void testDeleteAllStocks_Empty() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(portfolioRepository.findByUser(mockUser)).thenReturn(List.of());

        CommonDto<PortfolioItemDto> response = managePortfolioService.deleteAllStocks("johndoe");

        assertEquals("FAILED", response.getStatus());
        assertEquals("No portfolio records found to delete.", response.getMsg());
        verify(portfolioRepository, never()).deleteAll(any());
    }
}