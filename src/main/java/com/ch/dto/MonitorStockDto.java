package com.ch.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonitorStockDto {
    private String stockSymbol;
    private String companyName;
    private int quantity;
    private double buyPrice;
    private double currentPrice;
    private double totalInvested;       // quantity * buyPrice
    private double currentValue;        // quantity * currentPrice
    private double gainLoss;            // currentValue - totalInvested
    private double gainLossPercent;     // (gainLoss / totalInvested) * 100

    // US9: Threshold breach indicators
    private Double upperThreshold;      // % set by user (null if not set)
    private Double lowerThreshold;      // % set by user (null if not set)
    private Double upperAlertPrice;     // buyPrice + (buyPrice * upper% / 100)
    private Double lowerAlertPrice;     // buyPrice - (buyPrice * lower% / 100)
    private boolean upperBreached;      // currentPrice >= upperAlertPrice
    private boolean lowerBreached;      // currentPrice <= lowerAlertPrice
    private String alertStatus;         // "UPPER_BREACHED" / "LOWER_BREACHED" / "NORMAL"
}