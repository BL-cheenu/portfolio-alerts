package com.ch.repository;

import com.ch.entity.AlertHistoryEntity;
import com.ch.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertHistoryRepository extends JpaRepository<AlertHistoryEntity, Long> {

    // Get all alert history for a user (newest first)
    List<AlertHistoryEntity> findByUserOrderByTriggeredAtDesc(UserEntity user);

    // Get history for specific stock
    List<AlertHistoryEntity> findByUserAndStockSymbolIgnoreCaseOrderByTriggeredAtDesc(
            UserEntity user, String stockSymbol);
}