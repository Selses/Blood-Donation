package com.blooddonation.repository;

import com.blooddonation.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DatabaseLayerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private RecipientRepository recipientRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private BloodBankRepository bloodBankRepository;

    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    @Autowired
    private DonationHistoryRepository donationHistoryRepository;

    @Autowired
    private BloodInventoryRepository bloodInventoryRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    @DisplayName("Should successfully persist and query all 9 domain entities with relationships")
    void testAllEntitiesAndRelationships() {
        // 1. User
        User donorUser = new User("John Doe", "john@example.com", "secret123", "9876543210", UserRole.DONOR);
        User savedUser = userRepository.save(donorUser);
        assertNotNull(savedUser.getId());
        assertEquals("john@example.com", savedUser.getEmail());

        // 2. Hospital
        Hospital hospital = new Hospital(null, "Apollo Hospital", "HOSP-LIC-1001", "info@apollo.com", "044-1234567", "Greams Road", "Chennai", "Tamil Nadu", true);
        Hospital savedHospital = hospitalRepository.save(hospital);
        assertNotNull(savedHospital.getId());

        // 3. Blood Bank
        BloodBank bloodBank = new BloodBank(savedHospital, "Apollo Blood Bank", "BB-LIC-501", "044-7654321", "bb@apollo.com", "Greams Road", "Chennai");
        BloodBank savedBloodBank = bloodBankRepository.save(bloodBank);
        assertNotNull(savedBloodBank.getId());
        assertEquals(savedHospital.getId(), savedBloodBank.getHospital().getId());

        // 4. Donor
        Donor donor = new Donor(savedUser, "John Doe", "O+", "Chennai", "9876543210", true, LocalDate.now().minusMonths(4));
        Donor savedDonor = donorRepository.save(donor);
        assertNotNull(savedDonor.getId());
        assertEquals(savedUser.getId(), savedDonor.getUser().getId());

        // 5. Recipient
        Recipient recipient = new Recipient(null, "Meena", "O+", "9123456780", "Chennai", "Apollo Hospital", "9876543210");
        Recipient savedRecipient = recipientRepository.save(recipient);
        assertNotNull(savedRecipient.getId());

        // 6. Blood Request
        BloodRequest request = new BloodRequest(savedRecipient, savedHospital, "Meena", "O+", "Apollo Hospital", "Chennai", "HIGH", 2, "PENDING");
        BloodRequest savedRequest = bloodRequestRepository.save(request);
        assertNotNull(savedRequest.getId());
        assertEquals("PENDING", savedRequest.getStatus());
        assertEquals(2, savedRequest.getRequiredUnits());

        // 7. Donation History
        DonationHistory donation = new DonationHistory(savedDonor, savedHospital, savedBloodBank, "O+", 1, LocalDate.now(), "COMPLETED", "Voluntary donation");
        DonationHistory savedDonation = donationHistoryRepository.save(donation);
        assertNotNull(savedDonation.getId());
        assertEquals(savedDonor.getId(), savedDonation.getDonor().getId());

        // 8. Blood Inventory
        BloodInventory inventory = new BloodInventory(savedHospital, savedBloodBank, "O+", 15);
        BloodInventory savedInventory = bloodInventoryRepository.save(inventory);
        assertNotNull(savedInventory.getId());
        assertEquals(15, savedInventory.getAvailableUnits());

        // 9. Notification
        Notification notification = new Notification(savedUser, savedDonor, savedRequest, "Emergency Blood Request", "Urgent O+ blood needed at Apollo Hospital", "EMERGENCY_REQUEST");
        Notification savedNotification = notificationRepository.save(notification);
        assertNotNull(savedNotification.getId());
        assertFalse(savedNotification.isRead());
        assertEquals(savedDonor.getId(), savedNotification.getDonor().getId());
    }

    @Test
    @DisplayName("Should test custom repository queries for search and filtering")
    void testCustomRepositoryQueries() {
        Donor donor = new Donor("Ravi", "A+", "Trichy", "9876543211", true);
        donorRepository.save(donor);

        List<Donor> results = donorRepository.findByBloodGroupIgnoreCaseAndCityIgnoreCaseAndAvailable("A+", "Trichy", true);
        assertFalse(results.isEmpty());
        assertEquals("Ravi", results.get(0).getName());

        BloodRequest request = new BloodRequest("Sita", "B+", "City Hospital", "Madurai", "CRITICAL", "PENDING");
        bloodRequestRepository.save(request);

        List<BloodRequest> pendingRequests = bloodRequestRepository.findByStatusIgnoreCase("PENDING");
        assertFalse(pendingRequests.isEmpty());
    }
}
