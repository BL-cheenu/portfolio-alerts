package com.ch.repository;

import com.ch.entity.PortfolioEntity;
import com.ch.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity, Long> {

    List<PortfolioEntity> findByUser(UserEntity user);

    Optional<PortfolioEntity> findByUserAndStockSymbolIgnoreCase(UserEntity user, String stockSymbol);

    boolean existsByUserAndStockSymbolIgnoreCase(UserEntity user, String stockSymbol);
}
