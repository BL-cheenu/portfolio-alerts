package com.ch.service;

import com.ch.dto.CommonDto;
import com.ch.dto.UserRegisterDto;

public interface UserRegistrationService {
    CommonDto<UserRegisterDto> registerUser(UserRegisterDto request);
}
