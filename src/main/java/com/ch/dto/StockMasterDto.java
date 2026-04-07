package com.ch.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockMasterDto {
    private Long id;
    private String companyName;
    private String tickerSymbol;
    private String exchange;
}
