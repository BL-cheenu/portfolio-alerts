package com.ch.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadConfirmDto {
    // New stocks to add — always added
    private List<UploadRowDto> newStocks;

    // Conflict stocks user chose to update
    private List<UploadRowDto> updateStocks;
}
