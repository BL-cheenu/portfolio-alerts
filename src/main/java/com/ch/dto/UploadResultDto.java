package com.ch.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadResultDto {
    private int addedCount;
    private int updatedCount;
    private int skippedCount;
    private int invalidCount;
    private String message;
}
