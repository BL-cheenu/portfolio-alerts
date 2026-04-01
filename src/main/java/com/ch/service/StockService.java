package com.ch.service;

import com.ch.dto.StockDto;

import java.util.List;

public interface StockService {
    List<StockDto> getNseTop50Prices();
    StockDto getStockPrice(String symbol);
}
