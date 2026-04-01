package com.ch.service;

import com.ch.dto.CommonDto;
import com.ch.dto.LoginRequestDto;
import com.ch.dto.LoginResponseDto;

public interface UserLoginService {
    CommonDto<LoginResponseDto> login(LoginRequestDto request);
}
