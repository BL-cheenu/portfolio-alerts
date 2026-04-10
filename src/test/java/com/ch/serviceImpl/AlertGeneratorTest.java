package com.ch.serviceImpl;

import com.ch.entity.AlertEntity;
import com.ch.entity.PortfolioEntity;
import com.ch.entity.UserEntity;
import com.ch.kafka.StockPriceCache;
import com.ch.rabbitmq.AlertEmailMessage;
import com.ch.rabbitmq.AlertGenerator;
import com.ch.repository.AlertRepository;
import com.ch.repository.PortfolioRepository;
import com.ch.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertGenerator - US10 Unit Tests")
class AlertGeneratorTest {

    @Mock private UserRepository userRepository;
    @Mock private PortfolioRepository portfolioRepository;
    @Mock private AlertRepository alertRepository;
    @Mock private StockPriceCache stockPriceCache;
    @Mock private RabbitTemplate rabbitTemplate;

    @InjectMocks private AlertGenerator alertGenerator;

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
                .user(mockUser).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        mockAlert = AlertEntity.builder()
                .id(1L).stockSymbol("RELIANCE").companyName("Reliance Industries")
                .upperThreshold(10.0).lowerThreshold(5.0).isActive(true)
                .user(mockUser).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("Upper breach - should publish to RabbitMQ")
    void testUpperBreach_ShouldPublish() {
        when(userRepository.findAll()).thenReturn(List.of(mockUser));
        when(alertRepository.findByUserAndIsActive(mockUser, true)).thenReturn(List.of(mockAlert));
        when(portfolioRepository.findByUser(mockUser)).thenReturn(List.of(mockPortfolio));
        // upperAlertPrice = 2800 + 280 = 3080, current = 3100 → BREACH
        when(stockPriceCache.getCurrentPrice("RELIANCE", 2800.0)).thenReturn(3100.0);

        alertGenerator.checkThresholdsAndGenerateAlerts();

        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(AlertEmailMessage.class));
    }

    @Test
    @DisplayName("Lower breach - should publish to RabbitMQ")
    void testLowerBreach_ShouldPublish() {
        when(userRepository.findAll()).thenReturn(List.of(mockUser));
        when(alertRepository.findByUserAndIsActive(mockUser, true)).thenReturn(List.of(mockAlert));
        when(portfolioRepository.findByUser(mockUser)).thenReturn(List.of(mockPortfolio));
        // lowerAlertPrice = 2800 - 140 = 2660, current = 2600 → BREACH
        when(stockPriceCache.getCurrentPrice("RELIANCE", 2800.0)).thenReturn(2600.0);

        alertGenerator.checkThresholdsAndGenerateAlerts();

        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(AlertEmailMessage.class));
    }

    @Test
    @DisplayName("Within threshold - should NOT publish to RabbitMQ")
    void testNoBreach_ShouldNotPublish() {
        when(userRepository.findAll()).thenReturn(List.of(mockUser));
        when(alertRepository.findByUserAndIsActive(mockUser, true)).thenReturn(List.of(mockAlert));
        when(portfolioRepository.findByUser(mockUser)).thenReturn(List.of(mockPortfolio));
        // upper=3080, lower=2660, current=2900 → NORMAL
        when(stockPriceCache.getCurrentPrice("RELIANCE", 2800.0)).thenReturn(2900.0);

        alertGenerator.checkThresholdsAndGenerateAlerts();

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
    }

    @Test
    @DisplayName("No alerts set - should NOT publish")
    void testNoAlerts_ShouldNotPublish() {
        when(userRepository.findAll()).thenReturn(List.of(mockUser));
        when(alertRepository.findByUserAndIsActive(mockUser, true)).thenReturn(List.of());

        alertGenerator.checkThresholdsAndGenerateAlerts();

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
        verify(portfolioRepository, never()).findByUser(any());
    }

    @Test
    @DisplayName("No users - should not process anything")
    void testNoUsers_ShouldNotProcess() {
        when(userRepository.findAll()).thenReturn(List.of());

        alertGenerator.checkThresholdsAndGenerateAlerts();

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
    }
}