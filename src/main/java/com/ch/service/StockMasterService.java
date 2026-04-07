package com.ch.service;

import com.ch.dto.CommonDto;
import com.ch.dto.StockMasterDto;
import com.ch.dto.StockValidationDto;

import java.util.List;

public interface StockMasterService {

    // Load all stocks (app initialisation)
    CommonDto<StockMasterDto> getAllStocks();

    // Search by company name or ticker
    CommonDto<StockMasterDto> searchStocks(String keyword);

    // Validate list of tickers (file upload cross-check)
    CommonDto<StockValidationDto> validateTickers(List<String> tickers);
}
