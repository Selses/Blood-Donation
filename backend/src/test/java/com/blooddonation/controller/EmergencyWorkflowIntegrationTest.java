package com.blooddonation.controller;

import com.blooddonation.dto.BloodRequestDTO;
import com.blooddonation.dto.DonorRequestDTO;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmergencyWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String getAuthToken(String name, String email, UserRole role) throws Exception {
        RegisterRequestDTO registerDTO = new RegisterRequestDTO(
                name,
                email,
                "Password@123",
                "9876543210",
                role
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }

    private Long createRecipient(String token, String name, String bloodGroup, String city) throws Exception {
        com.blooddonation.dto.RecipientRequestDTO dto = new com.blooddonation.dto.RecipientRequestDTO(
                name, bloodGroup, "9876543210", city, "Trichy GH", "9876543211"
        );
        MvcResult result = mockMvc.perform(post("/api/recipients")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private Long createDonor(String token, String name, String bloodGroup, String city, boolean available) throws Exception {
        DonorRequestDTO dto = new DonorRequestDTO(name, bloodGroup, city, "9876543210", available);
        dto.setLastDonationDate(LocalDate.now().minusMonths(4));

        MvcResult result = mockMvc.perform(post("/api/donors")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    @DisplayName("Complete workflow: Create Request -> MATCHED -> Donor Accepts -> ACCEPTED -> Hospital Fulfills -> FULFILLED")
    void shouldExecuteCompleteEmergencyWorkflowSuccessfully() throws Exception {
        // Setup Users
        String recipientToken = getAuthToken("Arun", "arun.wf@test.com", UserRole.RECIPIENT);
        String raviToken = getAuthToken("Ravi", "ravi.wf@test.com", UserRole.DONOR);
        String kumarToken = getAuthToken("Kumar", "kumar.wf@test.com", UserRole.DONOR);
        String hospitalToken = getAuthToken("Trichy GH", "tgh.wf@test.com", UserRole.HOSPITAL);

        // Create compatible Donors
        createDonor(raviToken, "Ravi", "O+", "Trichy", true);
        createDonor(kumarToken, "Kumar", "O-", "Trichy", true);

        // 1. Recipient creates blood request for O+
        BloodRequestDTO requestDTO = new BloodRequestDTO(
                "Arun", "O+", "Trichy Government Hospital", "Trichy", "HIGH", 1, "PENDING"
        );

        MvcResult reqResult = mockMvc.perform(post("/api/blood-requests")
                        .header("Authorization", "Bearer " + recipientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("MATCHED")) // Status automatically MATCHED because donors exist
                .andReturn();

        Long reqId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).get("id").asLong();

        // 2. Fetch matches for request
        MvcResult matchesResult = mockMvc.perform(get("/api/blood-requests/" + reqId + "/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.donorName == 'Ravi')]").exists())
                .andExpect(jsonPath("$[?(@.donorName == 'Kumar')]").exists())
                .andReturn();

        var matchesArray = objectMapper.readTree(matchesResult.getResponse().getContentAsString());
        Long raviMatchId = null;
        Long kumarMatchId = null;
        for (var m : matchesArray) {
            if ("Ravi".equals(m.get("donorName").asText())) {
                raviMatchId = m.get("matchId").asLong();
            } else if ("Kumar".equals(m.get("donorName").asText())) {
                kumarMatchId = m.get("matchId").asLong();
            }
        }

        // 3. Ravi accepts match
        mockMvc.perform(post("/api/blood-requests/" + reqId + "/matches/" + raviMatchId + "/accept")
                        .header("Authorization", "Bearer " + raviToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        // 4. Request status is now ACCEPTED
        mockMvc.perform(get("/api/blood-requests/" + reqId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        // 5. Kumar tries to accept the same request afterward -> Conflict rejection (400 Bad Request)
        mockMvc.perform(post("/api/blood-requests/" + reqId + "/matches/" + kumarMatchId + "/accept")
                        .header("Authorization", "Bearer " + kumarToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This blood request has already been accepted by another donor."));

        // 6. Hospital fulfills the request
        mockMvc.perform(post("/api/blood-requests/" + reqId + "/fulfill")
                        .header("Authorization", "Bearer " + hospitalToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FULFILLED"));

        // 7. Request is now FULFILLED
        mockMvc.perform(get("/api/blood-requests/" + reqId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FULFILLED"));
    }

    @Test
    @DisplayName("Decline flow: Donor declines -> remaining donors keep request MATCHED -> all decline returns request to PENDING")
    void shouldHandleDonorDeclineFlowProperly() throws Exception {
        String recipientToken = getAuthToken("Recipient Flow", "recip.decline@test.com", UserRole.RECIPIENT);
        String donor1Token = getAuthToken("Donor 1", "d1.decline@test.com", UserRole.DONOR);
        String donor2Token = getAuthToken("Donor 2", "d2.decline@test.com", UserRole.DONOR);

        createDonor(donor1Token, "Donor 1", "AB-", "Trichy", true);
        createDonor(donor2Token, "Donor 2", "AB-", "Trichy", true);

        // Recipient creates request (AB-) -> compatible donors: A-, B-, AB-, O-
        BloodRequestDTO requestDTO = new BloodRequestDTO(
                "Patient AB-", "AB-", "Trichy GH", "Trichy", "HIGH", 1, "PENDING"
        );

        MvcResult reqResult = mockMvc.perform(post("/api/blood-requests")
                        .header("Authorization", "Bearer " + recipientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("MATCHED"))
                .andReturn();

        Long reqId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).get("id").asLong();

        MvcResult matchesResult = mockMvc.perform(get("/api/blood-requests/" + reqId + "/matches"))
                .andExpect(status().isOk())
                .andReturn();

        var matchesNode = objectMapper.readTree(matchesResult.getResponse().getContentAsString());
        Long match1Id = null;
        Long match2Id = null;
        for (var m : matchesNode) {
            if ("Donor 1".equals(m.get("donorName").asText())) {
                match1Id = m.get("matchId").asLong();
            } else if ("Donor 2".equals(m.get("donorName").asText())) {
                match2Id = m.get("matchId").asLong();
            }
        }

        // Donor 1 declines
        mockMvc.perform(post("/api/blood-requests/" + reqId + "/matches/" + match1Id + "/decline")
                        .header("Authorization", "Bearer " + donor1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DECLINED"));

        // Since Donor 2 is still pending, request stays MATCHED
        mockMvc.perform(get("/api/blood-requests/" + reqId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("MATCHED"));

        // Donor 2 also declines
        mockMvc.perform(post("/api/blood-requests/" + reqId + "/matches/" + match2Id + "/decline")
                        .header("Authorization", "Bearer " + donor2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DECLINED"));

        // No active pending matches remain -> returns to PENDING
        mockMvc.perform(get("/api/blood-requests/" + reqId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Cancellation and invalid transition validations")
    void shouldValidateCancellationsAndInvalidTransitions() throws Exception {
        String recipientToken = getAuthToken("Recipient Cancel", "recip.cancel@test.com", UserRole.RECIPIENT);
        String hospitalToken = getAuthToken("Hospital Cancel", "hosp.cancel@test.com", UserRole.HOSPITAL);

        // 1. Create a request with no matching donors -> remains PENDING
        BloodRequestDTO requestDTO = new BloodRequestDTO(
                "Patient Rare", "B-", "Trichy GH", "Trichy", "CRITICAL", 1, "PENDING"
        );

        MvcResult reqResult = mockMvc.perform(post("/api/blood-requests")
                        .header("Authorization", "Bearer " + recipientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        Long reqId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).get("id").asLong();

        // 2. Direct PENDING -> FULFILLED invalid transition rejected
        mockMvc.perform(patch("/api/blood-requests/" + reqId + "/status")
                        .param("status", "FULFILLED")
                        .header("Authorization", "Bearer " + hospitalToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        // 3. Direct fulfillment call on unaccepted request rejected
        mockMvc.perform(post("/api/blood-requests/" + reqId + "/fulfill")
                        .header("Authorization", "Bearer " + hospitalToken))
                .andExpect(status().isBadRequest());

        // 4. Cancel PENDING request
        mockMvc.perform(patch("/api/blood-requests/" + reqId + "/cancel")
                        .header("Authorization", "Bearer " + recipientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        // 5. CANCELLED -> ACCEPTED invalid transition rejected
        mockMvc.perform(patch("/api/blood-requests/" + reqId + "/status")
                        .param("status", "ACCEPTED")
                        .header("Authorization", "Bearer " + hospitalToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Authorization and ownership security checks")
    void shouldEnforceAuthorizationAndOwnership() throws Exception {
        String recipAToken = getAuthToken("Recipient A", "recipA@test.com", UserRole.RECIPIENT);
        String recipBToken = getAuthToken("Recipient B", "recipB@test.com", UserRole.RECIPIENT);
        String donorAToken = getAuthToken("Donor A", "donorA@test.com", UserRole.DONOR);
        String donorBToken = getAuthToken("Donor B", "donorB@test.com", UserRole.DONOR);

        createRecipient(recipAToken, "Recipient A", "O+", "Trichy");
        createRecipient(recipBToken, "Recipient B", "O+", "Trichy");
        createDonor(donorAToken, "Donor A", "O+", "Trichy", true);
        createDonor(donorBToken, "Donor B", "O+", "Trichy", true);

        // Recipient A creates request
        BloodRequestDTO requestDTO = new BloodRequestDTO(
                "Patient A", "O+", "Trichy GH", "Trichy", "HIGH", 1, "PENDING"
        );
        MvcResult reqResult = mockMvc.perform(post("/api/blood-requests")
                        .header("Authorization", "Bearer " + recipAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long reqId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).get("id").asLong();

        // 1. Recipient B tries to cancel Recipient A's request -> REJECTED (400 Unauthorized)
        mockMvc.perform(patch("/api/blood-requests/" + reqId + "/cancel")
                        .header("Authorization", "Bearer " + recipBToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unauthorized: You can only cancel your own blood request"));

        // Find matches
        MvcResult matchesResult = mockMvc.perform(get("/api/blood-requests/" + reqId + "/matches"))
                .andExpect(status().isOk())
                .andReturn();

        Long donorAMatchId = null;
        for (var node : objectMapper.readTree(matchesResult.getResponse().getContentAsString())) {
            if ("Donor A".equals(node.get("donorName").asText())) {
                donorAMatchId = node.get("matchId").asLong();
            }
        }

        // 2. Donor B tries to accept Donor A's match -> REJECTED (400 Unauthorized)
        mockMvc.perform(post("/api/blood-requests/" + reqId + "/matches/" + donorAMatchId + "/accept")
                        .header("Authorization", "Bearer " + donorBToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unauthorized: You can only accept matches assigned to your donor profile"));

        // 3. Normal donor tries to fulfill request -> REJECTED (403 Forbidden)
        mockMvc.perform(post("/api/blood-requests/" + reqId + "/fulfill")
                        .header("Authorization", "Bearer " + donorAToken))
                .andExpect(status().isForbidden());
    }
}
