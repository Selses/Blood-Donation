package com.blooddonation.controller;

import com.blooddonation.dto.RecipientRequestDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RecipientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String getRecipientJwtToken() throws Exception {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO(
                "Arun Recipient",
                "arun.recipient@test.com",
                "Password@123",
                "9876543210",
                UserRole.RECIPIENT
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }

    @Test
    @DisplayName("Should successfully create and retrieve recipient profile for Arun")
    void shouldCreateAndGetRecipientProfile() throws Exception {
        String token = getRecipientJwtToken();

        RecipientRequestDTO recipientDTO = new RecipientRequestDTO(
                "Arun",
                "O+",
                "9876543210",
                "Trichy",
                "Trichy Government Hospital",
                "9123456780"
        );

        MvcResult createResult = mockMvc.perform(post("/api/recipients")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipientDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Arun"))
                .andExpect(jsonPath("$.bloodGroup").value("O+"))
                .andExpect(jsonPath("$.city").value("Trichy"))
                .andExpect(jsonPath("$.hospitalName").value("Trichy Government Hospital"))
                .andReturn();

        Long recipientId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // Get by ID
        mockMvc.perform(get("/api/recipients/" + recipientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Arun"));

        // Get own profile via /me
        mockMvc.perform(get("/api/recipients/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Arun"))
                .andExpect(jsonPath("$.city").value("Trichy"));
    }

    @Test
    @DisplayName("Should update recipient profile")
    void shouldUpdateRecipientProfile() throws Exception {
        String token = getRecipientJwtToken();

        RecipientRequestDTO recipientDTO = new RecipientRequestDTO(
                "Arun Initial",
                "O+",
                "9876543210",
                "Trichy",
                "Trichy Government Hospital",
                "9123456780"
        );

        MvcResult createResult = mockMvc.perform(post("/api/recipients")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipientDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long recipientId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        RecipientRequestDTO updateDTO = new RecipientRequestDTO(
                "Arun Kumar Updated",
                "O+",
                "9876543210",
                "Madurai",
                "Apollo Speciality Hospital",
                "9123456789"
        );

        mockMvc.perform(put("/api/recipients/" + recipientId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Arun Kumar Updated"))
                .andExpect(jsonPath("$.city").value("Madurai"));
    }
}
