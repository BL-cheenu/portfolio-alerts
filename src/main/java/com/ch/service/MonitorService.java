package com.ch.service;

import com.ch.dto.CommonDto;
import com.ch.dto.MonitorPortfolioDto;
import com.ch.dto.MonitorStockDto;

public interface MonitorService {

    // US9 - Full portfolio monitor with live prices + threshold check
    CommonDto<MonitorPortfolioDto> monitorPortfolio(String username);

    // US9 - Monitor single stock
    CommonDto<MonitorStockDto> monitorStock(String username, String stockSymbol);
}