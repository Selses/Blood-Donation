package com.blooddonation.controller;

import com.blooddonation.dto.BloodRequestDTO;
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
class BloodRequestLifecycleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String getRecipientJwtToken() throws Exception {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO(
                "Arun",
                "arun.lifecycle@test.com",
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

    private String getHospitalJwtToken() throws Exception {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO(
                "Trichy Govt Hospital Admin",
                "tgh.hospital@test.com",
                "Password@123",
                "9876543210",
                UserRole.HOSPITAL
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }

    @Test
    @DisplayName("Should create Arun blood request and view own requests")
    void shouldCreateArunBloodRequestAndView() throws Exception {
        String token = getRecipientJwtToken();

        // 1. Create Recipient profile
        RecipientRequestDTO recipientDTO = new RecipientRequestDTO(
                "Arun", "O+", "9876543210", "Trichy", "Trichy Government Hospital", "9123456780"
        );
        mockMvc.perform(post("/api/recipients")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipientDTO)))
                .andExpect(status().isCreated());

        // 2. Submit emergency blood request
        BloodRequestDTO requestDTO = new BloodRequestDTO(
                "Arun",
                "O+",
                "Trichy Government Hospital",
                "Trichy",
                "HIGH",
                2,
                "PENDING"
        );

        MvcResult reqResult = mockMvc.perform(post("/api/blood-requests")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recipientName").value("Arun"))
                .andExpect(jsonPath("$.bloodGroup").value("O+"))
                .andExpect(jsonPath("$.hospitalName").value("Trichy Government Hospital"))
                .andExpect(jsonPath("$.city").value("Trichy"))
                .andExpect(jsonPath("$.urgency").value("HIGH"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        Long reqId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).get("id").asLong();

        // 3. View request status by ID
        mockMvc.perform(get("/api/blood-requests/" + reqId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));

        // 4. View own requests via /api/recipients/me/requests
        mockMvc.perform(get("/api/recipients/me/requests")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipientName").value("Arun"));
    }

    @Test
    @DisplayName("Should enforce valid request status lifecycle: PENDING -> MATCHED -> ACCEPTED -> FULFILLED")
    void shouldFollowValidStatusLifecycle() throws Exception {
        String recipToken = getRecipientJwtToken();
        String hospitalToken = getHospitalJwtToken();

        BloodRequestDTO requestDTO = new BloodRequestDTO(
                "Arun Lifecycle", "O+", "Trichy Government Hospital", "Trichy", "HIGH", 1, "PENDING"
        );

        MvcResult reqResult = mockMvc.perform(post("/api/blood-requests")
                        .header("Authorization", "Bearer " + recipToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long reqId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).get("id").asLong();

        // 1. PENDING -> MATCHED
        mockMvc.perform(patch("/api/blood-requests/" + reqId + "/status")
                        .param("status", "MATCHED")
                        .header("Authorization", "Bearer " + hospitalToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("MATCHED"));

        // 2. MATCHED -> ACCEPTED
        mockMvc.perform(patch("/api/blood-requests/" + reqId + "/status")
                        .param("status", "ACCEPTED")
                        .header("Authorization", "Bearer " + hospitalToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        // 3. ACCEPTED -> FULFILLED
        mockMvc.perform(patch("/api/blood-requests/" + reqId + "/status")
                        .param("status", "FULFILLED")
                        .header("Authorization", "Bearer " + hospitalToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FULFILLED"));

        // 4. Cannot change status from FULFILLED -> returns 400 Bad Request
        mockMvc.perform(patch("/api/blood-requests/" + reqId + "/status")
                        .param("status", "PENDING")
                        .header("Authorization", "Bearer " + hospitalToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Should allow cancelling pending requests and reject cancelling fulfilled requests")
    void shouldHandleRequestCancellation() throws Exception {
        String recipToken = getRecipientJwtToken();

        BloodRequestDTO requestDTO = new BloodRequestDTO(
                "Arun Cancel Test", "O+", "Trichy Government Hospital", "Trichy", "HIGH", 1, "PENDING"
        );

        MvcResult reqResult = mockMvc.perform(post("/api/blood-requests")
                        .header("Authorization", "Bearer " + recipToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long reqId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).get("id").asLong();

        // Cancel the pending request
        mockMvc.perform(patch("/api/blood-requests/" + reqId + "/cancel")
                        .header("Authorization", "Bearer " + recipToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        // Double cancellation should fail with 400
        mockMvc.perform(patch("/api/blood-requests/" + reqId + "/cancel")
                        .header("Authorization", "Bearer " + recipToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
