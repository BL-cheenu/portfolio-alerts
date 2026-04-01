package com.ch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomeResponseDto {

    private String welcomeMessage;
    private String username;
    private List<String> menuOptions;
    private List<StockDto> nseTop50Ticker;
}
