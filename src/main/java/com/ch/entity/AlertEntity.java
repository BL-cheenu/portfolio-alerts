package com.ch.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * US8 - Alert Threshold Entity
 *
 * Stores upper and lower % threshold for each stock per user.
 * Example: upperThreshold = 10.0 means alert when price rises 10% above buy price
 *          lowerThreshold = 5.0  means alert when price drops 5% below buy price
 */
@Entity
@Table(name = "alerts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "stock_symbol"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertEntity {

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

    // Upper threshold in % — alert when price rises above this %
    @Column(name = "upper_threshold", nullable = false)
    private double upperThreshold;

    // Lower threshold in % — alert when price drops below this %
    @Column(name = "lower_threshold", nullable = false)
    private double lowerThreshold;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive  = true;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
