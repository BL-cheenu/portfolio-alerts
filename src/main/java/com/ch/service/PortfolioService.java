package com.ch.service;

import com.ch.dto.CommonDto;
import com.ch.dto.CreatePortfolioRequestDto;
import com.ch.dto.PortfolioItemDto;
import com.ch.dto.PortfolioValuationDto;

public interface PortfolioService {

    // US6 - Add one stock to portfolio via UI form
    CommonDto<PortfolioItemDto> addStock(String username, CreatePortfolioRequestDto request);

    // US6 - Get portfolio with full valuation (Stream API)
    CommonDto<PortfolioValuationDto> getPortfolioValuation(String username);
}
