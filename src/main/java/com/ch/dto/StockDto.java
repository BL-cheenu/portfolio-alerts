package com.ch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockDto {
    private String symbol;
    private String companyName;
    private double currentPrice;
    private double change;
    private double changePercent;
}
