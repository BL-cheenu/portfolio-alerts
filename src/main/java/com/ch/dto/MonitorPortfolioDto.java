package com.ch.dto;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonitorPortfolioDto {
    private List<MonitorStockDto> stocks;

    // Overall summary — computed via Stream API
    private double totalInvested;
    private double totalCurrentValue;
    private double totalGainLoss;
    private double totalGainLossPercent;
    private int totalStocks;

    // Alert summary
    private int upperBreachedCount;
    private int lowerBreachedCount;
    private int normalCount;

    private String lastUpdated;
}