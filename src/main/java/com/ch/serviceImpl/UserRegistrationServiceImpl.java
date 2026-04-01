package com.ch.serviceImpl;

import com.ch.customexception.UserRegistrationException;
import com.ch.dto.CommonDto;
import com.ch.dto.UserRegisterDto;
import com.ch.entity.UserEntity;
import com.ch.repository.UserRepository;
import com.ch.service.UserRegistrationService;
import com.ch.utils.UserInputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Registers a new user after validating all inputs.
 *
 * Steps:
 *  1. Validate name, email format, password rules  (Java 8 Predicate)
 *  2. Check for duplicate username / email in DB
 *  3. Encrypt password with BCrypt
 *  4. Persist user via JPA
 *  5. Return success response
 */

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationService.class);

    @Autowired
    UserInputValidator userInputValidator;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CommonDto<UserRegisterDto> registerUser(UserRegisterDto request) {
        log.info("Enter into UserRegistrationServiceImpl registerUser() method");

        CommonDto<UserRegisterDto> commonDto = new CommonDto<>();

        try {
            log.info("Registration attempt for email: {}", request.getEmail());

            // ── 1. Name validation ──────────────────────────────────────────
            if (!userInputValidator.isValidName.test(request.getName())) {
                log.warn("Registration failed - blank name provided.");
                throw new UserRegistrationException("Name must not be blank.");
            }

            // ── 2. Email format validation ──────────────────────────────────
            if (!userInputValidator.isValidEmail.test(request.getEmail())) {
                log.warn("Registration failed - invalid email format: {}", request.getEmail());
                throw new UserRegistrationException("Invalid email format.");
            }

            // ── 3. Password rule validation ─────────────────────────────────
            if (!userInputValidator.isValidPassword.test(request.getPassword())) {
                String reason = userInputValidator.describePasswordFailure(request.getPassword());
                log.warn("Registration failed - password rule violated: {}", reason);
                throw new UserRegistrationException(reason);
            }

            // ── 4. Duplicate username check ─────────────────────────────────
            if (userRepository.existsByUsername(request.getUsername())) {
                log.warn("Registration failed - username already exists: {}", request.getUsername());
                throw new UserRegistrationException("Username '" + request.getUsername() + "' is already taken.");
            }

            // ── 5. Duplicate email check ────────────────────────────────────
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Registration failed - email already registered: {}", request.getEmail());
                throw new UserRegistrationException("Email '" + request.getEmail() + "' is already registered.");
            }

            // ── 6. Encrypt password (BCrypt) ────────────────────────────────
            String encryptedPassword = passwordEncoder.encode(request.getPassword());
            log.debug("Password encrypted successfully for email: {}", request.getEmail());

            // ── 7. Persist user (JPA) ───────────────────────────────────────
            UserEntity user = UserEntity.builder()
                    .name(request.getName().trim())
                    .username(request.getUsername().trim())
                    .email(request.getEmail().trim().toLowerCase())
                    .createdAt(LocalDateTime.now())
                    .password(encryptedPassword)
                    .build();

            UserEntity savedUser = userRepository.save(user);
            log.info("User registered successfully. ID: {}, Email: {}", savedUser.getId(), savedUser.getEmail());

            // ── 8. Success response ─────────────────────────────────────────
            UserRegisterDto responseDto = new UserRegisterDto();
            responseDto.setName(savedUser.getName());
            responseDto.setUsername(savedUser.getUsername());
            responseDto.setEmail(savedUser.getEmail());

            commonDto.setData(responseDto);
            commonDto.setMsg("User registered successfully.");
            commonDto.setStatus("SUCCESS");
            commonDto.setStatusCode(201);

            log.info("Exit from UserRegistrationServiceImpl registerUser() method");

        } catch (UserRegistrationException ex) {
            log.error("Registration validation failed: {}", ex.getMessage());
            commonDto.setMsg(ex.getMessage());
            commonDto.setStatus("FAILED");
            commonDto.setStatusCode(400);
        }

        return commonDto;
    }
}
