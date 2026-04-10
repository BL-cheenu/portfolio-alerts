package com.ch.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * US10 - RabbitMQ Configuration
 *
 * Fix: Jackson2JsonMessageConverter deprecated in Spring AMQP 4.0
 * Use SimpleMessageConverter instead — messages serialized as JSON string by AlertGenerator
 *
 * Queue    : alert.email.queue
 * Exchange : alert.exchange (Direct)
 * Routing  : alert.email.routingkey
 * DLQ      : alert.email.dlq
 */
@Configuration
public class RabbitMQConfig {

    public static final String ALERT_QUEUE       = "alert.email.queue";
    public static final String ALERT_EXCHANGE    = "alert.exchange";
    public static final String ALERT_ROUTING_KEY = "alert.email.routingkey";
    public static final String ALERT_DLQ         = "alert.email.dlq";
    public static final String ALERT_DL_EXCHANGE = "alert.dl.exchange";

    // ── Queue ────────────────────────────────────────────────────────────
    @Bean
    public Queue alertQueue() {
        return QueueBuilder.durable(ALERT_QUEUE)
                .withArgument("x-dead-letter-exchange", ALERT_DL_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ALERT_DLQ)
                .build();
    }

    @Bean
    public Queue alertDeadLetterQueue() {
        return QueueBuilder.durable(ALERT_DLQ).build();
    }

    // ── Exchange ─────────────────────────────────────────────────────────
    @Bean
    public DirectExchange alertExchange() {
        return new DirectExchange(ALERT_EXCHANGE);
    }

    @Bean
    public DirectExchange alertDeadLetterExchange() {
        return new DirectExchange(ALERT_DL_EXCHANGE);
    }

    // ── Binding ───────────────────────────────────────────────────────────
    @Bean
    public Binding alertBinding() {
        return BindingBuilder.bind(alertQueue())
                .to(alertExchange())
                .with(ALERT_ROUTING_KEY);
    }

    @Bean
    public Binding alertDLQBinding() {
        return BindingBuilder.bind(alertDeadLetterQueue())
                .to(alertDeadLetterExchange())
                .with(ALERT_DLQ);
    }

    // ── FIX: Use SimpleMessageConverter (Jackson2JsonMessageConverter deprecated in 4.0) ──
    @Bean
    public MessageConverter messageConverter() {
        return new SimpleMessageConverter();
    }

    // ── RabbitTemplate ───────────────────────────────────────────────────
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}