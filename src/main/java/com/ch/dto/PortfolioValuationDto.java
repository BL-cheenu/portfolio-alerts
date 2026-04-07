package com.ch.dto;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PortfolioValuationDto {
    private List<PortfolioItemDto> holdings;
    private double totalInvested;
    private double totalCurrentValue;
    private double totalProfitLoss;
    private double totalProfitLossPercent;
    private int totalStocks;
}
