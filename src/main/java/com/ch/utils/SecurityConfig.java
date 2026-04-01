package com.ch.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * US1 Requirement: BCrypt password encryption before storing in DB.
 * BCryptPasswordEncoder uses strength 12 (default=10).
 * Strength 12 means 2^12 = 4096 iterations — good balance of security vs performance.
 */

@Configuration
public class SecurityConfig {

    // ── BCrypt Password Encoder ──────────────────────────────────────────
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    // ── Register JWT Filter ──────────────────────────────────────────────
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtAuthFilter);
        registration.addUrlPatterns("/api/v1/home/*", "/api/v1/home");
        registration.setOrder(1);
        return registration;
    }
}
