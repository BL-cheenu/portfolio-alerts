package com.ch.controller;

import com.ch.dto.CommonDto;
import com.ch.dto.LoginRequestDto;
import com.ch.dto.LoginResponseDto;
import com.ch.service.UserLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for US2 – User Login
 *
 * Base URL : /api/v1/auth
 * Endpoint : POST /login
 */
@RestController
@RequestMapping("/api/v1/auth")
public class UserLoginController {

    private static final Logger log = LoggerFactory.getLogger(UserLoginController.class);

    @Autowired
    private UserLoginService userLoginService;

    /**
     * POST /api/v1/auth/login
     *
     * Request Body (JSON):
     * {
     *   "username" : "johndoe",
     *   "password" : "Secret@123"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<CommonDto<LoginResponseDto>> login(@RequestBody LoginRequestDto request) {
        log.info("POST /api/v1/auth/login - incoming request for username: {}", request.getUsername());
        CommonDto<LoginResponseDto> commonDto = userLoginService.login(request);
        HttpStatus status = "SUCCESS".equals(commonDto.getStatus()) ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(commonDto, status);
    }
}
