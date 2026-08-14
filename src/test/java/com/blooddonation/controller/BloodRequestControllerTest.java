package com.blooddonation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BloodRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "RECIPIENT")
    void shouldCreateBloodRequestViaRequestsEndpoint() throws Exception {
        mockMvc.perform(post("/api/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "recipientName": "Bob",
                          "bloodGroup": "A-",
                          "hospitalName": "Kenyatta Hospital",
                          "city": "Nairobi",
                          "urgency": "High",
                          "status": "Pending"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recipientName").value("Bob"))
                .andExpect(jsonPath("$.bloodGroup").value("A-"));
    }

    @Test
    @WithMockUser(roles = "RECIPIENT")
    void shouldCreateBloodRequestViaBloodRequestsEndpoint() throws Exception {
        mockMvc.perform(post("/api/blood-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "recipientName": "Arun",
                          "bloodGroup": "O+",
                          "hospitalName": "Trichy Government Hospital",
                          "city": "Trichy",
                          "urgency": "HIGH",
                          "requiredUnits": 2,
                          "status": "PENDING"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recipientName").value("Arun"))
                .andExpect(jsonPath("$.bloodGroup").value("O+"))
                .andExpect(jsonPath("$.requiredUnits").value(2));
    }

    @Test
    @WithMockUser(roles = "RECIPIENT")
    void shouldRejectBloodRequestWithInvalidBloodGroup() throws Exception {
        mockMvc.perform(post("/api/blood-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "recipientName": "Invalid Request",
                          "bloodGroup": "INVALID",
                          "hospitalName": "City Hospital",
                          "city": "Trichy",
                          "urgency": "HIGH"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldGetAllBloodRequests() throws Exception {
        mockMvc.perform(get("/api/blood-requests"))
                .andExpect(status().isOk());
    }
}
