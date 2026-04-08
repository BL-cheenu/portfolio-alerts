package com.ch.repository;

import com.ch.entity.PortfolioEntity;
import com.ch.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity, Long> {

    // US6 - Get all portfolios for a user
    List<PortfolioEntity> findByUser(UserEntity user);

    // US6 - Check duplicate stock for user
    boolean existsByUserAndStockSymbolIgnoreCase(UserEntity user, String stockSymbol);

    // US6 - Find by ticker and user
    Optional<PortfolioEntity> findByUserAndStockSymbolIgnoreCase(UserEntity user, String stockSymbol);

    // US7 - Find specific portfolio by ID and user (security: user can only access own data)
    Optional<PortfolioEntity> findByIdAndUser(Long id, UserEntity user);

    // US7 - Delete all stocks for a user
    void deleteByUser(UserEntity user);
}
