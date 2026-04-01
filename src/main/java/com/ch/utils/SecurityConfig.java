package com.ch.utils;

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
