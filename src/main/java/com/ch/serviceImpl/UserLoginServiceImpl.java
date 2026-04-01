package com.ch.serviceImpl;


import com.ch.customexception.UserLoginException;
import com.ch.dto.CommonDto;
import com.ch.dto.LoginRequestDto;
import com.ch.dto.LoginResponseDto;
import com.ch.entity.UserEntity;
import com.ch.repository.UserRepository;
import com.ch.service.UserLoginService;
import com.ch.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * US2 - User Login and Authentication
 *
 * Steps:
 *  1. Validate request fields not empty
 *  2. Fetch user by username using Optional (US2 requirement)
 *  3. Verify password using BCrypt
 *  4. Generate JWT token
 *  5. Return success response with token
 */
@Service
public class UserLoginServiceImpl implements UserLoginService {

    private static final Logger log = LoggerFactory.getLogger(UserLoginServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public CommonDto<LoginResponseDto> login(LoginRequestDto request) {
        log.info("Enter into UserLoginServiceImpl login() method");

        CommonDto<LoginResponseDto> commonDto = new CommonDto<>();

        try {
            // ── 1. Basic field validation ───────────────────────────────
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                log.warn("Login failed - username is blank.");
                throw new UserLoginException("Username must not be blank.");
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                log.warn("Login failed - password is blank.");
                throw new UserLoginException("Password must not be blank.");
            }

            // ── 2. Fetch user by username (Optional) ────────────────────
            log.info("Fetching user for username: {}", request.getUsername());
            Optional<UserEntity> optionalUser = userRepository.findByUsername(request.getUsername().trim());

            if (optionalUser.isEmpty()) {
                log.warn("Login failed - username not found: {}", request.getUsername());
                throw new UserLoginException("Invalid username or password.");
            }

            UserEntity user = optionalUser.get();

            // ── 3. Verify password (BCrypt) ─────────────────────────────
            boolean passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());

            if (!passwordMatch) {
                log.warn("Login failed - invalid password for username: {}", request.getUsername());
                throw new UserLoginException("Invalid username or password.");
            }

            // ── 4. Generate JWT token ────────────────────────────────────
            String token = jwtUtil.generateToken(user.getUsername());
            log.info("JWT token generated for username: {}", user.getUsername());

            // ── 5. Build success response ────────────────────────────────
            LoginResponseDto responseDto = new LoginResponseDto();
            responseDto.setUsername(user.getUsername());
            responseDto.setEmail(user.getEmail());
            responseDto.setToken(token);

            commonDto.setData(responseDto);
            commonDto.setMsg("Login successful.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(200);

            log.info("Exit from UserLoginServiceImpl login() method");

        } catch (UserLoginException ex) {
            log.error("Login failed: {}", ex.getMessage());
            commonDto.setMsg(ex.getMessage());
            commonDto.setStatus("FAILED");
            commonDto.setStatusCode(401);
        }

        return commonDto;
    }
}
