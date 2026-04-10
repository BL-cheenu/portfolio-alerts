package com.ch.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * US10 - Alert History Entity
 * Stores every alert email that was sent — for UI display
 */
@Entity
@Table(name = "alert_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "stock_symbol", nullable = false)
    private String stockSymbol;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "breach_type", nullable = false)
    private String breachType;          // UPPER_BREACHED / LOWER_BREACHED

    @Column(name = "buy_price")
    private double buyPrice;

    @Column(name = "current_price")
    private double currentPrice;

    @Column(name = "alert_price")
    private double alertPrice;

    @Column(name = "gain_loss")
    private double gainLoss;

    @Column(name = "gain_loss_percent")
    private double gainLossPercent;

    @Column(name = "email_sent_to")
    private String emailSentTo;

    @Column(name = "triggered_at")
    private LocalDateTime triggeredAt;
}