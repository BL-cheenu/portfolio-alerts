package com.ch.controller;

import com.ch.dto.CommonDto;
import com.ch.dto.HomeResponseDto;
import com.ch.service.HomeService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for US3 – Home Page
 *
 * Base URL : /api/v1/home
 * Endpoint : GET /
 *
 * Protected — JWT token required in Authorization header
 */
@RestController
@RequestMapping("/api/v1/home")
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private HomeService homeService;

    /**
     * GET /api/v1/home
     *
     * Headers:
     *   Authorization: Bearer <JWT_TOKEN>
     */
    @GetMapping
    public ResponseEntity<CommonDto<HomeResponseDto>> getHomePage(HttpServletRequest request) {
        // Username extracted from JWT by JwtAuthFilter
        String username = (String) request.getAttribute("username");
        log.info("GET /api/v1/home - user: {}", username);

        CommonDto<HomeResponseDto> commonDto = homeService.getHomePage(username);
        return new ResponseEntity<>(commonDto, HttpStatus.OK);
    }
}
