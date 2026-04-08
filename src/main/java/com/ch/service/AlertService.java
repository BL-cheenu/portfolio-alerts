package com.ch.service;

import com.ch.dto.AlertRequestDto;
import com.ch.dto.AlertResponseDto;
import com.ch.dto.CommonDto;

public interface AlertService {

    // US8.1 - Set new alert threshold
    CommonDto<AlertResponseDto> setAlert(String username, AlertRequestDto request);

    // US8.2 - Update existing alert threshold
    CommonDto<AlertResponseDto> updateAlert(String username, Long alertId, AlertRequestDto request);

    // US8.3 - Get all alerts for user
    CommonDto<AlertResponseDto> getAllAlerts(String username);

    // US8.4 - Get alert for specific stock
    CommonDto<AlertResponseDto> getAlertByStock(String username, String stockSymbol);

    // US8.5 - Delete alert
    CommonDto<AlertResponseDto> deleteAlert(String username, Long alertId);
}
