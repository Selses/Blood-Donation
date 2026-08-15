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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NotificationIntegrationTest {

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
    @DisplayName("Complete Notification Flow: Emergency Match -> Donor Notification -> Accept -> Recipient Notification -> Fulfill -> Recipient Notification")
    void shouldExecuteCompleteNotificationFlow() throws Exception {
        String recipientToken = getAuthToken("Arun Kumar", "arun.notif@test.com", UserRole.RECIPIENT);
        String donorToken = getAuthToken("Ravi", "ravi.notif@test.com", UserRole.DONOR);
        String hospitalToken = getAuthToken("Trichy GH", "tgh.notif@test.com", UserRole.HOSPITAL);

        // 1. Create donor Ravi (O+, Trichy)
        createDonor(donorToken, "Ravi", "O+", "Trichy", true);

        // 2. Arun creates CRITICAL blood request for O+ in Trichy
        BloodRequestDTO requestDTO = new BloodRequestDTO(
                "Arun", "O+", "Trichy Government Hospital", "Trichy", "CRITICAL", 1, "PENDING"
        );

        MvcResult reqResult = mockMvc.perform(post("/api/blood-requests")
                        .header("Authorization", "Bearer " + recipientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long reqId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).get("id").asLong();

        // 3. Ravi checks his notifications -> Should have 1 unread EMERGENCY notification
        mockMvc.perform(get("/api/notifications/unread/count")
                        .header("Authorization", "Bearer " + donorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));

        MvcResult donorNotifResult = mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + donorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("EMERGENCY"))
                .andExpect(jsonPath("$[0].title").value("CRITICAL Blood Request"))
                .andExpect(jsonPath("$[0].relatedRequestId").value(reqId))
                .andExpect(jsonPath("$[0].read").value(false))
                .andReturn();

        Long notifId = objectMapper.readTree(donorNotifResult.getResponse().getContentAsString()).get(0).get("id").asLong();

        // 4. Ravi marks his notification as read
        mockMvc.perform(patch("/api/notifications/" + notifId + "/read")
                        .header("Authorization", "Bearer " + donorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));

        mockMvc.perform(get("/api/notifications/unread/count")
                        .header("Authorization", "Bearer " + donorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0));

        // 5. Get matches and Ravi accepts the match
        MvcResult matchesResult = mockMvc.perform(get("/api/blood-requests/" + reqId + "/matches"))
                .andExpect(status().isOk())
                .andReturn();

        Long matchId = objectMapper.readTree(matchesResult.getResponse().getContentAsString()).get(0).get("matchId").asLong();

        mockMvc.perform(post("/api/blood-requests/" + reqId + "/matches/" + matchId + "/accept")
                        .header("Authorization", "Bearer " + donorToken))
                .andExpect(status().isOk());

        // 6. Arun (Recipient) checks notifications -> Should have DONOR_ACCEPTED notification
        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + recipientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("DONOR_ACCEPTED"))
                .andExpect(jsonPath("$[0].title").value("Donor Accepted"))
                .andExpect(jsonPath("$[0].relatedRequestId").value(reqId));

        // 7. Hospital fulfills the request
        mockMvc.perform(post("/api/blood-requests/" + reqId + "/fulfill")
                        .header("Authorization", "Bearer " + hospitalToken))
                .andExpect(status().isOk());

        // 8. Arun checks notifications -> Should now have 2 notifications, latest is REQUEST_UPDATED (Blood Request Fulfilled)
        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + recipientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type").value("REQUEST_UPDATED"))
                .andExpect(jsonPath("$[0].title").value("Blood Request Fulfilled"));

        // 9. Arun marks all as read
        mockMvc.perform(patch("/api/notifications/read-all")
                        .header("Authorization", "Bearer " + recipientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All notifications marked as read"));

        mockMvc.perform(get("/api/notifications/unread/count")
                        .header("Authorization", "Bearer " + recipientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    @DisplayName("Notification isolation and security: User A cannot see or mark User B's notifications")
    void shouldEnforceNotificationSecurityAndIsolation() throws Exception {
        String userAToken = getAuthToken("User A", "usera@test.com", UserRole.DONOR);
        String userBToken = getAuthToken("User B", "userb@test.com", UserRole.DONOR);
        String recipToken = getAuthToken("Recip", "recip.sec@test.com", UserRole.RECIPIENT);

        createDonor(userAToken, "User A", "B+", "Trichy", true);

        // Recipient creates blood request for B+
        BloodRequestDTO requestDTO = new BloodRequestDTO(
                "Recip", "B+", "Trichy GH", "Trichy", "LOW", 1, "PENDING"
        );

        mockMvc.perform(post("/api/blood-requests")
                        .header("Authorization", "Bearer " + recipToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());

        // User A has 1 notification (MATCH_FOUND because urgency is LOW)
        MvcResult userANotifs = mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("MATCH_FOUND"))
                .andReturn();

        Long userANotifId = objectMapper.readTree(userANotifs.getResponse().getContentAsString()).get(0).get("id").asLong();

        // User B has 0 notifications
        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // User B attempts to mark User A's notification as read -> REJECTED (400)
        mockMvc.perform(patch("/api/notifications/" + userANotifId + "/read")
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unauthorized: You can only mark your own notifications as read"));
    }

    @Test
    @DisplayName("Donor decline notification: Recipient is notified when donor declines")
    void shouldNotifyRecipientWhenDonorDeclines() throws Exception {
        String recipientToken = getAuthToken("Recip Dec", "recip.dec@test.com", UserRole.RECIPIENT);
        String donorToken = getAuthToken("Donor Dec", "donor.dec@test.com", UserRole.DONOR);

        createDonor(donorToken, "Donor Dec", "A+", "Trichy", true);

        BloodRequestDTO requestDTO = new BloodRequestDTO(
                "Recip Dec", "A+", "Trichy GH", "Trichy", "HIGH", 1, "PENDING"
        );

        MvcResult reqResult = mockMvc.perform(post("/api/blood-requests")
                        .header("Authorization", "Bearer " + recipientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long reqId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).get("id").asLong();

        MvcResult matchesResult = mockMvc.perform(get("/api/blood-requests/" + reqId + "/matches"))
                .andExpect(status().isOk())
                .andReturn();

        Long matchId = objectMapper.readTree(matchesResult.getResponse().getContentAsString()).get(0).get("matchId").asLong();

        // Donor declines
        mockMvc.perform(post("/api/blood-requests/" + reqId + "/matches/" + matchId + "/decline")
                        .header("Authorization", "Bearer " + donorToken))
                .andExpect(status().isOk());

        // Recipient receives DONOR_DECLINED notification
        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + recipientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("DONOR_DECLINED"))
                .andExpect(jsonPath("$[0].title").value("Donor Update"));
    }

    @Test
    @DisplayName("Duplicate Prevention & Unread Endpoint: Matching multiple times does not spam notifications and unread filter works")
    void shouldPreventDuplicateNotificationsAndFilterUnread() throws Exception {
        String recipientToken = getAuthToken("Recip Dup", "recip.dup@test.com", UserRole.RECIPIENT);
        String donorToken = getAuthToken("Donor Dup", "donor.dup@test.com", UserRole.DONOR);

        createDonor(donorToken, "Donor Dup", "AB+", "Trichy", true);

        BloodRequestDTO requestDTO = new BloodRequestDTO(
                "Recip Dup", "AB+", "Trichy GH", "Trichy", "HIGH", 1, "PENDING"
        );

        MvcResult reqResult = mockMvc.perform(post("/api/blood-requests")
                        .header("Authorization", "Bearer " + recipientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long reqId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).get("id").asLong();

        // Query matches again repeatedly
        mockMvc.perform(get("/api/blood-requests/" + reqId + "/matches"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/blood-requests/" + reqId + "/matches"))
                .andExpect(status().isOk());

        // Donor should still only have 1 notification (not 3)
        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + donorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Test GET /api/notifications/unread
        mockMvc.perform(get("/api/notifications/unread")
                        .header("Authorization", "Bearer " + donorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].read").value(false));

        // Mark as read
        mockMvc.perform(patch("/api/notifications/read-all")
                        .header("Authorization", "Bearer " + donorToken))
                .andExpect(status().isOk());

        // Now /unread returns empty list
        mockMvc.perform(get("/api/notifications/unread")
                        .header("Authorization", "Bearer " + donorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
