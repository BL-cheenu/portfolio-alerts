package com.ch.kafka;

import lombok.*;

/**
 * US9 - Kafka Message Model
 * Producer sends this to "stock-prices" topic
 * Consumer receives and updates in-memory price cache
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockPriceMessage {
    private String symbol;
    private double currentPrice;
    private double change;
    private double changePercent;
    private long timestamp;
}