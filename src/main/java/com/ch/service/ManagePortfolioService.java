package com.ch.service;

import com.ch.dto.CommonDto;
import com.ch.dto.PortfolioItemDto;
import com.ch.dto.PortfolioValuationDto;
import com.ch.dto.UpdatePortfolioRequestDto;

public interface ManagePortfolioService {

    // US7.1 - View full portfolio
    CommonDto<PortfolioValuationDto> getPortfolio(String username);

    // US7.2 - Update one stock (quantity / buy price)
    CommonDto<PortfolioItemDto> updateStock(String username, Long portfolioId,
                                            UpdatePortfolioRequestDto request);

    // US7.3 - Delete one stock
    CommonDto<PortfolioItemDto> deleteStock(String username, Long portfolioId);

    // US7.4 - Delete all stocks
    CommonDto<PortfolioItemDto> deleteAllStocks(String username);
}