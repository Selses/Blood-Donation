package com.blooddonation.controller;

import com.blooddonation.dto.LoginRequestDTO;
import com.blooddonation.dto.RegisterRequestDTO;
import com.blooddonation.model.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should successfully register a new donor user")
    void shouldRegisterUser() throws Exception {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO(
                "Ravi Kumar",
                "ravi.auth.test@example.com",
                "Password@123",
                "9876543210",
                UserRole.DONOR
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.name").value("Ravi Kumar"))
                .andExpect(jsonPath("$.email").value("ravi.auth.test@example.com"))
                .andExpect(jsonPath("$.role").value("DONOR"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("Should reject duplicate email registration with HTTP 400")
    void shouldRejectDuplicateRegistration() throws Exception {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO(
                "Duplicate User",
                "dup.auth.test@example.com",
                "Password@123",
                "9876543210",
                UserRole.DONOR
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Should successfully login with valid credentials and return JWT")
    void shouldLoginWithValidCredentials() throws Exception {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO(
                "Login User",
                "login.auth.test@example.com",
                "CorrectPassword123",
                "9876543210",
                UserRole.DONOR
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated());

        LoginRequestDTO loginDTO = new LoginRequestDTO("login.auth.test@example.com", "CorrectPassword123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.email").value("login.auth.test@example.com"))
                .andExpect(jsonPath("$.role").value("DONOR"));
    }

    @Test
    @DisplayName("Should reject login with invalid password with HTTP 401")
    void shouldRejectInvalidCredentials() throws Exception {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO(
                "User Wrong Pass",
                "wrongpass.auth.test@example.com",
                "CorrectPassword123",
                "9876543210",
                UserRole.DONOR
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated());

        LoginRequestDTO loginDTO = new LoginRequestDTO("wrongpass.auth.test@example.com", "WrongPassword!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Should retrieve authenticated user details via /api/auth/me with valid Bearer token")
    void shouldGetMeWithToken() throws Exception {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO(
                "Me User",
                "me.auth.test@example.com",
                "Password@123",
                "9876543210",
                UserRole.RECIPIENT
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("me.auth.test@example.com"))
                .andExpect(jsonPath("$.role").value("RECIPIENT"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("Should reject /api/auth/me without token with HTTP 401")
    void shouldRejectMeWithoutToken() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
