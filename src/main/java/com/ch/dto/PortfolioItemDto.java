package com.ch.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PortfolioItemDto {
    private Long id;
    private String stockSymbol;
    private String companyName;
    private int quantity;
    private double buyPrice;
    private double currentPrice;
    private double totalInvested;       // quantity * buyPrice
    private double currentValue;        // quantity * currentPrice
    private double profitLoss;          // currentValue - totalInvested
    private double profitLossPercent;   // (profitLoss / totalInvested) * 100
    private String createdAt;
    private String updatedAt;
}
