package com.blooddonation.controller;

import com.blooddonation.dto.DonorRequestDTO;
import com.blooddonation.dto.DonorResponseDTO;
import com.blooddonation.dto.RegisterRequestDTO;
import com.blooddonation.model.UserRole;
import com.blooddonation.service.DonorService;
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
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DonorService donorService;

    private String getJwtTokenForRole(String emailPrefix, UserRole role) throws Exception {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO(
                role.name() + " User",
                emailPrefix + "_" + role.name().toLowerCase() + "@test.com",
                "Password@123",
                "9876543210",
                role
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseBody).get("accessToken").asText();
    }

    @Test
    @DisplayName("Should reject mutation endpoint without JWT token with HTTP 401 Unauthorized")
    void shouldRejectMutationWithoutToken() throws Exception {
        mockMvc.perform(post("/api/donors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Unauth Donor",
                                  "bloodGroup": "O+",
                                  "city": "Chennai",
                                  "phone": "9876543210",
                                  "available": true
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Should allow mutation when authenticated with DONOR role")
    void shouldAllowDonorMutationWithDonorRole() throws Exception {
        String donorToken = getJwtTokenForRole("donor_test", UserRole.DONOR);

        mockMvc.perform(post("/api/donors")
                        .header("Authorization", "Bearer " + donorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Authorized Donor",
                                  "bloodGroup": "O+",
                                  "city": "Chennai",
                                  "phone": "9876543210",
                                  "available": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Authorized Donor"));
    }

    @Test
    @DisplayName("Should reject donor creation with RECIPIENT role with HTTP 403 Forbidden")
    void shouldRejectDonorCreationWithWrongRole() throws Exception {
        String recipientToken = getJwtTokenForRole("recip_test", UserRole.RECIPIENT);

        mockMvc.perform(post("/api/donors")
                        .header("Authorization", "Bearer " + recipientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Forbidden Donor",
                                  "bloodGroup": "O+",
                                  "city": "Chennai",
                                  "phone": "9876543210",
                                  "available": true
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("Should allow recipient role to create blood requests")
    void shouldAllowRecipientToCreateBloodRequest() throws Exception {
        String recipientToken = getJwtTokenForRole("recip_req_test", UserRole.RECIPIENT);

        mockMvc.perform(post("/api/blood-requests")
                        .header("Authorization", "Bearer " + recipientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "recipientName": "Emergency Patient",
                                  "bloodGroup": "A+",
                                  "hospitalName": "Apollo Hospital",
                                  "city": "Chennai",
                                  "urgency": "CRITICAL",
                                  "requiredUnits": 3,
                                  "status": "PENDING"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recipientName").value("Emergency Patient"))
                .andExpect(jsonPath("$.requiredUnits").value(3));
    }

    @Test
    @DisplayName("Should restrict DELETE operations to ADMIN role only")
    void shouldRestrictDeleteToAdminOnly() throws Exception {
        DonorResponseDTO createdDonor = donorService.createDonor(
                new DonorRequestDTO("To Delete", "B+", "Chennai", "9876543210", true)
        );

        String donorToken = getJwtTokenForRole("donor_delete_test", UserRole.DONOR);

        // Non-admin should get 403 Forbidden
        mockMvc.perform(delete("/api/donors/" + createdDonor.getId())
                        .header("Authorization", "Bearer " + donorToken))
                .andExpect(status().isForbidden());

        String adminToken = getJwtTokenForRole("admin_delete_test", UserRole.ADMIN);

        // Admin should get 204 No Content
        mockMvc.perform(delete("/api/donors/" + createdDonor.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}
