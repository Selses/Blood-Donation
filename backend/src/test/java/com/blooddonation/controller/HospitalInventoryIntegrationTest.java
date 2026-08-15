package com.blooddonation.controller;

import com.blooddonation.dto.*;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HospitalInventoryIntegrationTest {

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
    @DisplayName("Hospital and Blood Bank Management: Create, Get, and Update Profiles")
    void shouldManageHospitalAndBloodBankProfiles() throws Exception {
        String hospitalToken = getAuthToken("Trichy GH User", "tgh.admin@test.com", UserRole.HOSPITAL);
        String bloodBankToken = getAuthToken("Trichy BB User", "tbb.admin@test.com", UserRole.BLOOD_BANK);

        // 1. Create Hospital
        HospitalRequestDTO hospDTO = new HospitalRequestDTO(
                "Trichy Government Hospital", "HOSP-001", "hospital@example.com", "0431-1234567", "Trichy, Tamil Nadu", "Trichy", "Tamil Nadu"
        );
        MvcResult hospResult = mockMvc.perform(post("/api/hospitals")
                        .header("Authorization", "Bearer " + hospitalToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Trichy Government Hospital"))
                .andExpect(jsonPath("$.city").value("Trichy"))
                .andReturn();

        Long hospId = objectMapper.readTree(hospResult.getResponse().getContentAsString()).get("id").asLong();

        // 2. Create Blood Bank linked to Hospital
        BloodBankRequestDTO bbDTO = new BloodBankRequestDTO(
                hospId, "Trichy Blood Bank", "BB-001", "9876501234", "bloodbank@example.com", "Trichy, Tamil Nadu", "Trichy"
        );
        MvcResult bbResult = mockMvc.perform(post("/api/blood-banks")
                        .header("Authorization", "Bearer " + bloodBankToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bbDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Trichy Blood Bank"))
                .andExpect(jsonPath("$.hospitalId").value(hospId))
                .andReturn();

        Long bbId = objectMapper.readTree(bbResult.getResponse().getContentAsString()).get("id").asLong();

        // 3. Public get
        mockMvc.perform(get("/api/hospitals/" + hospId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Trichy Government Hospital"));

        mockMvc.perform(get("/api/blood-banks/" + bbId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Trichy Blood Bank"));
    }

    @Test
    @DisplayName("Inventory Management: Create, Search by Blood Group, Add/Remove units, Validation")
    void shouldManageInventoryUnitsCorrectly() throws Exception {
        String hospitalToken = getAuthToken("Apollo Hospital", "apollo@test.com", UserRole.HOSPITAL);

        // Create hospital profile
        HospitalRequestDTO hospDTO = new HospitalRequestDTO(
                "Apollo Trichy", "APOLLO-001", "apollo@example.com", "0431-7654321", "Trichy", "Trichy", "Tamil Nadu"
        );
        mockMvc.perform(post("/api/hospitals")
                        .header("Authorization", "Bearer " + hospitalToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospDTO)))
                .andExpect(status().isCreated());

        // 1. Create inventory O+ with 10 units
        BloodInventoryRequestDTO invDTO = new BloodInventoryRequestDTO("O+", 10);
        MvcResult invResult = mockMvc.perform(post("/api/inventory")
                        .header("Authorization", "Bearer " + hospitalToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bloodGroup").value("O+"))
                .andExpect(jsonPath("$.availableUnits").value(10))
                .andReturn();

        Long invId = objectMapper.readTree(invResult.getResponse().getContentAsString()).get("id").asLong();

        // 2. Search by blood group
        mockMvc.perform(get("/api/inventory/blood-group/O+"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + invId + ")].availableUnits").value(10));

        // 3. Invalid blood group search
        mockMvc.perform(get("/api/inventory/blood-group/XYZ"))
                .andExpect(status().isBadRequest());

        // 4. Add 5 units
        mockMvc.perform(patch("/api/inventory/" + invId + "/add")
                        .header("Authorization", "Bearer " + hospitalToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new InventoryAdjustmentDTO(5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableUnits").value(15));

        // 5. Remove 3 units
        mockMvc.perform(patch("/api/inventory/" + invId + "/remove")
                        .header("Authorization", "Bearer " + hospitalToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new InventoryAdjustmentDTO(3))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableUnits").value(12));

        // 6. Attempt removing more than available (e.g. 20 units) -> Validation rejection (400)
        mockMvc.perform(patch("/api/inventory/" + invId + "/remove")
                        .header("Authorization", "Bearer " + hospitalToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new InventoryAdjustmentDTO(20))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Inventory-based Request Fulfillment: Sufficient Stock -> Deduct Units + FULFILLED + Notification")
    void shouldFulfillRequestFromInventorySuccessfully() throws Exception {
        String hospToken = getAuthToken("Kauvery Hospital", "kauvery@test.com", UserRole.HOSPITAL);
        String recipToken = getAuthToken("Recipient Arun", "arun.inv@test.com", UserRole.RECIPIENT);

        // 1. Create Hospital profile
        HospitalRequestDTO hospDTO = new HospitalRequestDTO(
                "Kauvery Hospital", "KAUVERY-001", "kauvery@example.com", "0431-9998887", "Trichy", "Trichy", "Tamil Nadu"
        );
        mockMvc.perform(post("/api/hospitals")
                        .header("Authorization", "Bearer " + hospToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospDTO)))
                .andExpect(status().isCreated());

        // 2. Set up O+ inventory = 10 units
        BloodInventoryRequestDTO invDTO = new BloodInventoryRequestDTO("O+", 10);
        MvcResult invResult = mockMvc.perform(post("/api/inventory")
                        .header("Authorization", "Bearer " + hospToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long invId = objectMapper.readTree(invResult.getResponse().getContentAsString()).get("id").asLong();

        // 3. Recipient creates blood request for O+, required units = 3
        BloodRequestDTO requestDTO = new BloodRequestDTO(
                "Arun", "O+", "Kauvery Hospital", "Trichy", "HIGH", 3, "PENDING"
        );
        MvcResult reqResult = mockMvc.perform(post("/api/blood-requests")
                        .header("Authorization", "Bearer " + recipToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long reqId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).get("id").asLong();

        // 4. Hospital fulfills using INVENTORY source
        FulfillRequestDTO fulfillDTO = new FulfillRequestDTO("INVENTORY", invId);
        mockMvc.perform(post("/api/blood-requests/" + reqId + "/fulfill")
                        .header("Authorization", "Bearer " + hospToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fulfillDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FULFILLED"));

        // 5. Verify Inventory reduced from 10 to 7 (10 - 3 = 7)
        mockMvc.perform(get("/api/inventory")
                        .header("Authorization", "Bearer " + hospToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.bloodGroup == 'O+')].availableUnits").value(7));

        // 6. Verify Recipient received notification
        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + recipToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("REQUEST_UPDATED"))
                .andExpect(jsonPath("$[0].title").value("Blood Request Fulfilled"));
    }

    @Test
    @DisplayName("Insufficient Inventory Fulfillment: Rejected, Inventory Unchanged, Request Unchanged")
    void shouldRejectFulfillmentWhenInsufficientInventory() throws Exception {
        String hospToken = getAuthToken("City Care Hospital", "citycare@test.com", UserRole.HOSPITAL);
        String recipToken = getAuthToken("Patient Low", "patient.low@test.com", UserRole.RECIPIENT);

        // 1. Create Hospital profile
        HospitalRequestDTO hospDTO = new HospitalRequestDTO(
                "City Care", "CITY-001", "citycare@example.com", "0431-2223334", "Trichy", "Trichy", "Tamil Nadu"
        );
        mockMvc.perform(post("/api/hospitals")
                        .header("Authorization", "Bearer " + hospToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospDTO)))
                .andExpect(status().isCreated());

        // 2. Set up O+ inventory = 2 units
        BloodInventoryRequestDTO invDTO = new BloodInventoryRequestDTO("O+", 2);
        mockMvc.perform(post("/api/inventory")
                        .header("Authorization", "Bearer " + hospToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invDTO)))
                .andExpect(status().isCreated());

        // 3. Recipient creates blood request for O+, required units = 5
        BloodRequestDTO requestDTO = new BloodRequestDTO(
                "Patient Low", "O+", "City Care", "Trichy", "HIGH", 5, "PENDING"
        );
        MvcResult reqResult = mockMvc.perform(post("/api/blood-requests")
                        .header("Authorization", "Bearer " + recipToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long reqId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).get("id").asLong();

        // 4. Attempt fulfillment using INVENTORY -> Rejection (400 Bad Request)
        FulfillRequestDTO fulfillDTO = new FulfillRequestDTO("INVENTORY");
        mockMvc.perform(post("/api/blood-requests/" + reqId + "/fulfill")
                        .header("Authorization", "Bearer " + hospToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fulfillDTO)))
                .andExpect(status().isBadRequest());

        // 5. Verify inventory is STILL 2 units (not deducted)
        mockMvc.perform(get("/api/inventory")
                        .header("Authorization", "Bearer " + hospToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.bloodGroup == 'O+')].availableUnits").value(2));

        // 6. Verify request is NOT FULFILLED
        mockMvc.perform(get("/api/blood-requests/" + reqId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(org.hamcrest.Matchers.not("FULFILLED")));
    }

    @Test
    @DisplayName("Donor vs Inventory Fulfillment: Donor fulfillment does not deduct inventory stock")
    void shouldNotDeductInventoryDuringDonorFulfillment() throws Exception {
        String hospToken = getAuthToken("General Hospital", "gen.hosp@test.com", UserRole.HOSPITAL);
        String recipToken = getAuthToken("Recipient Donor Flow", "recip.donorflow@test.com", UserRole.RECIPIENT);
        String donorToken = getAuthToken("Donor Ravi", "donor.ravi@test.com", UserRole.DONOR);

        // 1. Hospital setup & inventory = 10 units O+
        HospitalRequestDTO hospDTO = new HospitalRequestDTO(
                "General Hospital", "GEN-001", "gen.hosp@example.com", "0431-5556667", "Trichy", "Trichy", "Tamil Nadu"
        );
        mockMvc.perform(post("/api/hospitals")
                        .header("Authorization", "Bearer " + hospToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospDTO)))
                .andExpect(status().isCreated());

        BloodInventoryRequestDTO invDTO = new BloodInventoryRequestDTO("O+", 10);
        mockMvc.perform(post("/api/inventory")
                        .header("Authorization", "Bearer " + hospToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invDTO)))
                .andExpect(status().isCreated());

        // 2. Donor setup
        createDonor(donorToken, "Donor Ravi", "O+", "Trichy", true);

        // 3. Create Blood Request
        BloodRequestDTO requestDTO = new BloodRequestDTO(
                "Patient X", "O+", "General Hospital", "Trichy", "HIGH", 1, "PENDING"
        );
        MvcResult reqResult = mockMvc.perform(post("/api/blood-requests")
                        .header("Authorization", "Bearer " + recipToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long reqId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).get("id").asLong();

        // 4. Donor accepts match
        MvcResult matchesResult = mockMvc.perform(get("/api/blood-requests/" + reqId + "/matches"))
                .andExpect(status().isOk())
                .andReturn();
        Long matchId = objectMapper.readTree(matchesResult.getResponse().getContentAsString()).get(0).get("matchId").asLong();

        mockMvc.perform(post("/api/blood-requests/" + reqId + "/matches/" + matchId + "/accept")
                        .header("Authorization", "Bearer " + donorToken))
                .andExpect(status().isOk());

        // 5. Hospital fulfills via DONOR source (default)
        mockMvc.perform(post("/api/blood-requests/" + reqId + "/fulfill")
                        .header("Authorization", "Bearer " + hospToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FULFILLED"));

        // 6. Verify inventory remains UNCHANGED at 10 units
        mockMvc.perform(get("/api/inventory")
                        .header("Authorization", "Bearer " + hospToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.bloodGroup == 'O+')].availableUnits").value(10));
    }

    @Test
    @DisplayName("Role Authorization & Cross-organization Security Checks")
    void shouldEnforceRoleAndCrossOrganizationSecurity() throws Exception {
        String donorToken = getAuthToken("Donor Sec", "donor.sec@test.com", UserRole.DONOR);
        String hospAToken = getAuthToken("Hosp A", "hospA@test.com", UserRole.HOSPITAL);
        String hospBToken = getAuthToken("Hosp B", "hospB@test.com", UserRole.HOSPITAL);

        // 1. Donor cannot create hospital or inventory -> 403 Forbidden
        HospitalRequestDTO hospDTO = new HospitalRequestDTO("Hosp Test", "LIC-X", "t@t.com", "9876543210", "A", "Trichy", "TN");
        mockMvc.perform(post("/api/hospitals")
                        .header("Authorization", "Bearer " + donorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospDTO)))
                .andExpect(status().isForbidden());

        BloodInventoryRequestDTO invDTO = new BloodInventoryRequestDTO("O+", 5);
        mockMvc.perform(post("/api/inventory")
                        .header("Authorization", "Bearer " + donorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invDTO)))
                .andExpect(status().isForbidden());

        // 2. Hospital A creates inventory
        mockMvc.perform(post("/api/hospitals")
                        .header("Authorization", "Bearer " + hospAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospDTO)))
                .andExpect(status().isCreated());

        MvcResult invAResult = mockMvc.perform(post("/api/inventory")
                        .header("Authorization", "Bearer " + hospAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long invAId = objectMapper.readTree(invAResult.getResponse().getContentAsString()).get("id").asLong();

        // 3. Hospital B creates its profile
        HospitalRequestDTO hospBDTO = new HospitalRequestDTO("Hosp B", "LIC-Y", "tb@t.com", "9876543211", "B", "Trichy", "TN");
        mockMvc.perform(post("/api/hospitals")
                        .header("Authorization", "Bearer " + hospBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospBDTO)))
                .andExpect(status().isCreated());

        // 4. Hospital B attempts to modify Hospital A's inventory -> REJECTED (400 Unauthorized)
        mockMvc.perform(patch("/api/inventory/" + invAId + "/add")
                        .header("Authorization", "Bearer " + hospBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new InventoryAdjustmentDTO(5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unauthorized: You do not own this hospital inventory"));
    }
}
