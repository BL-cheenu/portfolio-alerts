package com.ch.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertHistoryDto {
    private Long id;
    private String stockSymbol;
    private String companyName;
    private String breachType;
    private double buyPrice;
    private double currentPrice;
    private double alertPrice;
    private double gainLoss;
    private double gainLossPercent;
    private String emailSentTo;
    private String triggeredAt;
}