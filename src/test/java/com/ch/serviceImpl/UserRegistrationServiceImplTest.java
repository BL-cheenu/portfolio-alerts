package com.ch.serviceImpl;

import com.ch.dto.CommonDto;
import com.ch.dto.UserRegisterDto;
import com.ch.entity.UserEntity;
import com.ch.repository.UserRepository;
import com.ch.utils.UserInputValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRegistrationServiceImpl - Unit Tests")
class UserRegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRegistrationServiceImpl registrationService;

    private UserInputValidator userInputValidator;

    @BeforeEach
    void setUp() {
        // Real validator inject பண்றோம் (Predicate logic test ஆகணும்)
        userInputValidator = new UserInputValidator();
        registrationService = new UserRegistrationServiceImpl();
        registrationService.userInputValidator = userInputValidator;
        registrationService.userRepository = userRepository;
        registrationService.passwordEncoder = passwordEncoder;
    }

    // ── Helper: valid request ────────────────────────────────────────────
    private UserRegisterDto validRequest() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setName("John Doe");
        dto.setUsername("johndoe");
        dto.setEmail("john@example.com");
        dto.setPassword("Secret@123");
        return dto;
    }

    // ── Helper: saved entity ─────────────────────────────────────────────
    private UserEntity savedEntity() {
        return UserEntity.builder()
                .id(1L)
                .name("John Doe")
                .username("johndoe")
                .email("john@example.com")
                .password("$2a$12$hashed")
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ─────────────────────────── SUCCESS ────────────────────────────────

    @Test
    @DisplayName("Valid request - should register user and return SUCCESS")
    void testRegisterUser_Success() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret@123")).thenReturn("$2a$12$hashed");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity());

        CommonDto<UserRegisterDto> response = registrationService.registerUser(validRequest());

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(201, response.getStatusCode());
        assertEquals("User registered successfully.", response.getMsg());
        assertNotNull(response.getData());
        assertEquals("john@example.com", response.getData().getEmail());
    }

    // ─────────────────────── VALIDATION FAILURES ────────────────────────

    @Test
    @DisplayName("Blank name - should return FAILED")
    void testRegisterUser_BlankName() {
        UserRegisterDto req = validRequest();
        req.setName("  ");

        CommonDto<UserRegisterDto> response = registrationService.registerUser(req);

        assertEquals("FAILED", response.getStatus());
        assertEquals(400, response.getStatusCode());
        assertEquals("Name must not be blank.", response.getMsg());
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Invalid email format - should return FAILED")
    void testRegisterUser_InvalidEmail() {
        UserRegisterDto req = validRequest();
        req.setEmail("johnexample.com");

        CommonDto<UserRegisterDto> response = registrationService.registerUser(req);

        assertEquals("FAILED", response.getStatus());
        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid email format.", response.getMsg());
    }

    @Test
    @DisplayName("Weak password (no uppercase) - should return FAILED")
    void testRegisterUser_WeakPassword() {
        UserRegisterDto req = validRequest();
        req.setPassword("secret@123");

        CommonDto<UserRegisterDto> response = registrationService.registerUser(req);

        assertEquals("FAILED", response.getStatus());
        assertEquals(400, response.getStatusCode());
        assertTrue(response.getMsg().contains("uppercase"));
    }

    @Test
    @DisplayName("Password too short - should return FAILED")
    void testRegisterUser_PasswordTooShort() {
        UserRegisterDto req = validRequest();
        req.setPassword("Ab@1");

        CommonDto<UserRegisterDto> response = registrationService.registerUser(req);

        assertEquals("FAILED", response.getStatus());
        assertEquals(400, response.getStatusCode());
        assertEquals("Password must be at least 8 characters long.", response.getMsg());
    }

    // ─────────────────────── DUPLICATE CHECKS ───────────────────────────

    @Test
    @DisplayName("Duplicate username - should return FAILED")
    void testRegisterUser_DuplicateUsername() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        CommonDto<UserRegisterDto> response = registrationService.registerUser(validRequest());

        assertEquals("FAILED", response.getStatus());
        assertEquals(400, response.getStatusCode());
        assertTrue(response.getMsg().contains("already taken"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Duplicate email - should return FAILED")
    void testRegisterUser_DuplicateEmail() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        CommonDto<UserRegisterDto> response = registrationService.registerUser(validRequest());

        assertEquals("FAILED", response.getStatus());
        assertEquals(400, response.getStatusCode());
        assertTrue(response.getMsg().contains("already registered"));
        verify(userRepository, never()).save(any());
    }

    // ─────────────────────── PASSWORD ENCRYPTION ────────────────────────

    @Test
    @DisplayName("Password should be BCrypt encrypted before saving")
    void testRegisterUser_PasswordEncrypted() {
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("Secret@123")).thenReturn("$2a$12$hashed");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity());

        registrationService.registerUser(validRequest());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());

        // Raw password DB-ல store ஆகக்கூடாது
        assertNotEquals("Secret@123", captor.getValue().getPassword());
        assertEquals("$2a$12$hashed", captor.getValue().getPassword());
    }

    // ─────────────────────── EMAIL LOWERCASE ────────────────────────────

    @Test
    @DisplayName("Email should be saved in lowercase")
    void testRegisterUser_EmailSavedLowercase() {
        UserRegisterDto req = validRequest();
        req.setEmail("JOHN@EXAMPLE.COM");

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("$2a$12$hashed");

        UserEntity savedWithLower = UserEntity.builder()
                .id(1L).name("John Doe").username("johndoe")
                .email("john@example.com").password("$2a$12$hashed")
                .createdAt(LocalDateTime.now()).build();
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedWithLower);

        registrationService.registerUser(req);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertEquals("john@example.com", captor.getValue().getEmail());
    }
}