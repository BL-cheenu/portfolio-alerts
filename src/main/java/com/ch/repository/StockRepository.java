package com.ch.repository;

import com.ch.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, Long> {

    // Search by company name (case-insensitive, partial match)
    List<StockEntity> findByCompanyNameContainingIgnoreCase(String companyName);

    // Search by ticker symbol (exact, case-insensitive)
    Optional<StockEntity> findByTickerSymbolIgnoreCase(String tickerSymbol);

    // Search by ticker OR company name
    List<StockEntity> findByTickerSymbolContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(
            String ticker, String companyName);

    // Check if ticker exists (for validation)
    boolean existsByTickerSymbolIgnoreCase(String tickerSymbol);
}
