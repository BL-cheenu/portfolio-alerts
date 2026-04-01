package com.ch.controller;

import com.ch.dto.CommonDto;
import com.ch.dto.UserRegisterDto;
import com.ch.service.UserRegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserRegistrationController.class)
@DisplayName("UserRegistrationController - API Tests")
class UserRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRegistrationService userRegistrationService;

    @Autowired
    private ObjectMapper objectMapper;

    // ── Helper ───────────────────────────────────────────────────────────
    private UserRegisterDto validRequest() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setName("John Doe");
        dto.setUsername("johndoe");
        dto.setEmail("john@example.com");
        dto.setPassword("Secret@123");
        return dto;
    }

    // ─────────────────────────── SUCCESS ────────────────────────────────

    @Test
    @DisplayName("POST /register - valid request should return 201")
    void testRegister_Success() throws Exception {
        UserRegisterDto responseDto = new UserRegisterDto();
        responseDto.setName("John Doe");
        responseDto.setUsername("johndoe");
        responseDto.setEmail("john@example.com");

        CommonDto<UserRegisterDto> commonDto = new CommonDto<>();
        commonDto.setMsg("User registered successfully.");
        commonDto.setData(responseDto);
        commonDto.setStatus("SUCCESS");
        commonDto.setStatusCode(201);

        when(userRegistrationService.registerUser(any(UserRegisterDto.class))).thenReturn(commonDto);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.msg").value("User registered successfully."))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

    // ─────────────────────── FAILED RESPONSE ────────────────────────────

    @Test
    @DisplayName("POST /register - invalid email should return FAILED response")
    void testRegister_InvalidEmail() throws Exception {
        CommonDto<UserRegisterDto> commonDto = new CommonDto<>();
        commonDto.setMsg("Invalid email format.");
        commonDto.setStatus("FAILED");
        commonDto.setStatusCode(400);

        when(userRegistrationService.registerUser(any(UserRegisterDto.class))).thenReturn(commonDto);

        UserRegisterDto req = validRequest();
        req.setEmail("johnexample.com");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated()) // Controller always returns 201 — service handles logic
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.msg").value("Invalid email format."));
    }

    @Test
    @DisplayName("POST /register - duplicate email should return FAILED response")
    void testRegister_DuplicateEmail() throws Exception {
        CommonDto<UserRegisterDto> commonDto = new CommonDto<>();
        commonDto.setMsg("Email 'john@example.com' is already registered.");
        commonDto.setStatus("FAILED");
        commonDto.setStatusCode(400);

        when(userRegistrationService.registerUser(any(UserRegisterDto.class))).thenReturn(commonDto);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.statusCode").value(400));
    }
}