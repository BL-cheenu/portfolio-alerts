package com.ch.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadPreviewDto {
    private List<UploadRowDto> newStocks;       // will be added
    private List<UploadRowDto> conflictStocks;  // already exist - need user consent
    private List<String> invalidStocks;         // not in master stocks table
    private int totalRows;
    private int newCount;
    private int conflictCount;
    private int invalidCount;
}
