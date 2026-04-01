package com.ch.service;

import com.ch.dto.CommonDto;
import com.ch.dto.HomeResponseDto;

public interface HomeService {
    CommonDto<HomeResponseDto> getHomePage(String username);
}
