package com.blooddonation.controller;

import com.blooddonation.dto.BloodRequestDTO;
import com.blooddonation.dto.DonationHistoryDTO;
import com.blooddonation.dto.DonorRequestDTO;
import com.blooddonation.dto.RegisterRequestDTO;
import com.blooddonation.model.UserRole;
import com.blooddonation.service.BloodRequestService;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DonorManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BloodRequestService bloodRequestService;

    private String getDonorJwtToken() throws Exception {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO(
                "Ravi",
                "ravi.donor@test.com",
                "Password@123",
                "9876543210",
                UserRole.DONOR
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }

    private String getAdminJwtToken() throws Exception {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO(
                "Admin User",
                "admin.donor.mgmt@test.com",
                "Password@123",
                "9876543210",
                UserRole.ADMIN
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }

    @Test
    @DisplayName("Should create Ravi donor profile and verify self-profile endpoints")
    void shouldManageRaviDonorProfile() throws Exception {
        String token = getDonorJwtToken();

        DonorRequestDTO donorDTO = new DonorRequestDTO(
                "Ravi",
                "O+",
                "Trichy",
                "9876543210",
                true
        );
        donorDTO.setLastDonationDate(LocalDate.now().minusMonths(6));

        MvcResult createResult = mockMvc.perform(post("/api/donors")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donorDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ravi"))
                .andExpect(jsonPath("$.bloodGroup").value("O+"))
                .andExpect(jsonPath("$.city").value("Trichy"))
                .andExpect(jsonPath("$.phone").value("9876543210"))
                .andExpect(jsonPath("$.available").value(true))
                .andReturn();

        Long donorId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // Check self-profile /me
        mockMvc.perform(get("/api/donors/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ravi"))
                .andExpect(jsonPath("$.bloodGroup").value("O+"));

        // Toggle availability via /me/availability
        mockMvc.perform(patch("/api/donors/me/availability")
                        .param("available", "false")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));

        // Update profile
        DonorRequestDTO updateDTO = new DonorRequestDTO(
                "Ravi",
                "O+",
                "Trichy East",
                "9876543210",
                true
        );
        mockMvc.perform(put("/api/donors/" + donorId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Trichy East"));
    }

    @Test
    @DisplayName("Should record and retrieve donation history for donor")
    void shouldTrackDonationHistory() throws Exception {
        String donorToken = getDonorJwtToken();
        String adminToken = getAdminJwtToken();

        DonorRequestDTO donorDTO = new DonorRequestDTO("Ravi History", "O+", "Trichy", "9876543210", true);
        MvcResult createResult = mockMvc.perform(post("/api/donors")
                        .header("Authorization", "Bearer " + donorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donorDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long donorId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // Record a donation event
        DonationHistoryDTO historyDTO = new DonationHistoryDTO();
        historyDTO.setDonorId(donorId);
        historyDTO.setBloodGroup("O+");
        historyDTO.setUnitsDonated(1);
        historyDTO.setDonationDate(LocalDate.now());
        historyDTO.setStatus("COMPLETED");
        historyDTO.setRemarks("Trichy Blood Camp 2026");

        mockMvc.perform(post("/api/donors/" + donorId + "/history")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(historyDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.unitsDonated").value(1))
                .andExpect(jsonPath("$.remarks").value("Trichy Blood Camp 2026"));

        // Retrieve donation history
        mockMvc.perform(get("/api/donors/me/history")
                        .header("Authorization", "Bearer " + donorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].remarks").value("Trichy Blood Camp 2026"));
    }

    @Test
    @DisplayName("Should find eligible blood requests for O+ donor (compatible with O+, A+, B+, AB+)")
    void shouldFindEligibleRequestsForDonor() throws Exception {
        String donorToken = getDonorJwtToken();

        DonorRequestDTO donorDTO = new DonorRequestDTO("Ravi Compatible", "O+", "Trichy", "9876543210", true);
        MvcResult createResult = mockMvc.perform(post("/api/donors")
                        .header("Authorization", "Bearer " + donorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donorDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long donorId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // Create a compatible request (A+)
        bloodRequestService.createRequest(new BloodRequestDTO(
                "Patient 1", "A+", "Trichy Govt Hospital", "Trichy", "HIGH", "PENDING"
        ));

        // Create an incompatible request for O+ donor (A-)
        bloodRequestService.createRequest(new BloodRequestDTO(
                "Patient 2", "A-", "Trichy Govt Hospital", "Trichy", "HIGH", "PENDING"
        ));

        mockMvc.perform(get("/api/donors/" + donorId + "/eligible-requests")
                        .header("Authorization", "Bearer " + donorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.bloodGroup == 'A+')]").exists())
                .andExpect(jsonPath("$[?(@.bloodGroup == 'A-')]").doesNotExist());
    }
}
