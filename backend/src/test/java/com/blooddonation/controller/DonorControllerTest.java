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
class DonorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "DONOR")
    void shouldCreateDonor() throws Exception {
        mockMvc.perform(post("/api/donors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Alice",
                          "bloodGroup": "O+",
                          "city": "Nairobi",
                          "phone": "0712345678",
                          "available": true
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.bloodGroup").value("O+"))
                .andExpect(jsonPath("$.city").value("Nairobi"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @WithMockUser(roles = "DONOR")
    void shouldRejectDonorWithInvalidBloodGroup() throws Exception {
        mockMvc.perform(post("/api/donors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Invalid Donor",
                          "bloodGroup": "XYZ",
                          "city": "Nairobi",
                          "phone": "0712345678",
                          "available": true
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldGetAllDonors() throws Exception {
        mockMvc.perform(get("/api/donors"))
                .andExpect(status().isOk());
    }
}
