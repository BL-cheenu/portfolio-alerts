package com.ch.dto;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockValidationDto {
    private List<String> validTickers;
    private List<String> invalidTickers;
    private int totalCount;
    private int validCount;
    private int invalidCount;
}
