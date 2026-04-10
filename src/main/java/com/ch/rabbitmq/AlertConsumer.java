package com.ch.rabbitmq;

import com.ch.config.RabbitMQConfig;
import com.ch.service.AlertEmailService;
import com.ch.service.AlertHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * US10 - RabbitMQ Alert Consumer
 *
 * 1. Receives AlertEmailMessage from alert.email.queue
 * 2. Sends email via AlertEmailService
 * 3. Saves to alert_history table via AlertHistoryService
 * 4. On failure → Dead Letter Queue
 */
@Component
public class AlertConsumer {

    private static final Logger log = LoggerFactory.getLogger(AlertConsumer.class);

    @Autowired
    private AlertEmailService alertEmailService;

    @Autowired
    private AlertHistoryService alertHistoryService;

    @RabbitListener(queues = RabbitMQConfig.ALERT_QUEUE)
    public void consumeAlert(com.ch.rabbitmq.AlertEmailMessage message) {
        log.info("RabbitMQ consumed: User={}, Stock={}, Breach={}",
                message.getUsername(), message.getStockSymbol(), message.getBreachType());
        try {
            alertEmailService.sendAlertEmail(message);
            log.info("Alert email sent to: {}", message.getEmail());

            alertHistoryService.saveHistory(message);
            log.info("Alert history saved. Stock: {}", message.getStockSymbol());

        } catch (Exception e) {
            log.error("Alert consumer failed — going to DLQ: {}", e.getMessage());
            throw new RuntimeException("Alert processing failed: " + e.getMessage());
        }
    }
}