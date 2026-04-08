package com.ch.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertResponseDto {
    private Long id;
    private String stockSymbol;
    private String companyName;
    private double upperThreshold;      // % set by user
    private double lowerThreshold;      // % set by user
    private double buyPrice;            // from portfolio
    private double upperAlertPrice;     // buyPrice + (buyPrice * upperThreshold / 100)
    private double lowerAlertPrice;     // buyPrice - (buyPrice * lowerThreshold / 100)
    private boolean isActive;
    private String createdAt;
    private String updatedAt;
}
