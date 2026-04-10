package com.ch.rabbitmq;

import lombok.*;
import java.io.Serializable;

/**
 * US10 - RabbitMQ Alert Message
 *
 * Alert Generator produces this when threshold is breached.
 * Alert Consumer receives and sends email to user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertEmailMessage implements Serializable {
    private String username;
    private String email;
    private String stockSymbol;
    private String companyName;
    private double buyPrice;
    private double currentPrice;
    private int quantity;
    private double totalInvested;
    private double currentValue;
    private double gainLoss;
    private double gainLossPercent;
    private String breachType;      // "UPPER_BREACHED" / "LOWER_BREACHED"
    private double thresholdValue;  // % threshold that was crossed
    private double alertPrice;      // actual price level crossed
    private String triggeredAt;
}