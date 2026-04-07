package com.ch.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePortfolioRequestDto {
    private String stockSymbol;   // validated against stock master
    private String companyName;
    private int quantity;
    private double buyPrice;
}
