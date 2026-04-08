package com.ch.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePortfolioRequestDto {
    private Integer quantity;   // null = no change
    private Double buyPrice;    // null = no change
}