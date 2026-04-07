package com.ch.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadRowDto {
    private String stockSymbol;
    private String companyName;
    private int quantity;
    private double buyPrice;
}
