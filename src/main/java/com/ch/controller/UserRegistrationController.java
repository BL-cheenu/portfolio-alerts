package com.ch.controller;

import com.ch.dto.CommonDto;
import com.ch.dto.UserRegisterDto;
import com.ch.service.UserRegistrationService;
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
 * REST Controller for US1 – User Registration
 * <p>
 * Base URL : /api/v1/auth
 * Endpoint : POST /register
 */
@RestController
@RequestMapping("/api/v1/auth")
public class UserRegistrationController {
    private static final Logger log = LoggerFactory.getLogger(UserRegistrationController.class);

    @Autowired
    UserRegistrationService userRegistrationService;

    /**
     * POST /api/v1/auth/register
     * <p>
     * Request Body (JSON):
     * {
     * "name"     : "John Doe",
     * "username" : "johndoe",
     * "email"    : "john@example.com",
     * "password" : "Secret@123"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<CommonDto<UserRegisterDto>> register(@RequestBody UserRegisterDto request) {
        log.info("POST /api/v1/auth/register – incoming request for email: {}", request.getEmail());
        CommonDto<UserRegisterDto> commonDto = userRegistrationService.registerUser(request);
        return new ResponseEntity<>(commonDto, HttpStatus.CREATED);
    }
}
