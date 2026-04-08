package com.ch.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertRequestDto {
    private String stockSymbol;
    private String companyName;
    private double upperThreshold;  // % e.g. 10.0 = 10%
    private double lowerThreshold;  // % e.g. 5.0  = 5%
}
