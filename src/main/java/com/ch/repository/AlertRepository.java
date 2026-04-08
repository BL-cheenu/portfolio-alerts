package com.ch.repository;

import com.ch.entity.AlertEntity;
import com.ch.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<AlertEntity, Long> {

    // Get all alerts for a user
    List<AlertEntity> findByUser(UserEntity user);

    // Get alert for specific stock and user
    Optional<AlertEntity> findByUserAndStockSymbolIgnoreCase(UserEntity user, String stockSymbol);

    // Get by ID and user (security)
    Optional<AlertEntity> findByIdAndUser(Long id, UserEntity user);

    // Check if alert already exists
    boolean existsByUserAndStockSymbolIgnoreCase(UserEntity user, String stockSymbol);

    // Get only active alerts
    List<AlertEntity> findByUserAndIsActive(UserEntity user, boolean isActive);
}
