package com.ch.service;

import com.ch.dto.AlertHistoryDto;
import com.ch.dto.CommonDto;
import com.ch.rabbitmq.AlertEmailMessage;

public interface AlertHistoryService {

    // Save alert history after email sent
    void saveHistory(AlertEmailMessage message);

    // Get all alert history for user
    CommonDto<AlertHistoryDto> getAlertHistory(String username);

    // Get history for specific stock
    CommonDto<AlertHistoryDto> getAlertHistoryByStock(String username, String stockSymbol);
}