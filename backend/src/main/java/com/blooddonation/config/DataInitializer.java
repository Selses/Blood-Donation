package com.blooddonation.config;

import com.blooddonation.model.*;
import com.blooddonation.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.init-demo-data", havingValue = "true", matchIfMissing = false)
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final DonorRepository donorRepository;
    private final RecipientRepository recipientRepository;
    private final HospitalRepository hospitalRepository;
    private final BloodBankRepository bloodBankRepository;
    private final BloodInventoryRepository bloodInventoryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
            DonorRepository donorRepository,
            RecipientRepository recipientRepository,
            HospitalRepository hospitalRepository,
            BloodBankRepository bloodBankRepository,
            BloodInventoryRepository bloodInventoryRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.donorRepository = donorRepository;
        this.recipientRepository = recipientRepository;
        this.hospitalRepository = hospitalRepository;
        this.bloodBankRepository = bloodBankRepository;
        this.bloodInventoryRepository = bloodInventoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initUsersAndProfiles();
    }

    private void initUsersAndProfiles() {
        String defaultPassword = passwordEncoder.encode("Password@123");

        // 1. Donor: Ravi
        if (!userRepository.existsByEmail("ravi.wf@test.com")) {
            User donorUser = new User("Ravi Kumar", "ravi.wf@test.com", defaultPassword, "9876543210", UserRole.DONOR);
            userRepository.save(donorUser);

            Donor donor = new Donor(donorUser, "Ravi Kumar", "O+", "Trichy", "9876543210", true,
                    LocalDate.now().minusMonths(4));
            donorRepository.save(donor);
            log.info("Initialized demo donor: ravi.wf@test.com");
        }

        // 2. Recipient: Arun
        if (!userRepository.existsByEmail("arun.wf@test.com")) {
            User recipientUser = new User("Arun Prakash", "arun.wf@test.com", defaultPassword, "9876543211",
                    UserRole.RECIPIENT);
            userRepository.save(recipientUser);

            Recipient recipient = new Recipient(recipientUser, "Arun Prakash", "O+", "9876543211", "Trichy",
                    "Trichy GH", "9876543299");
            recipientRepository.save(recipient);
            log.info("Initialized demo recipient: arun.wf@test.com");
        }

        // 3. Hospital: Trichy GH
        if (!userRepository.existsByEmail("tgh.wf@test.com")) {
            User hospitalUser = new User("Trichy GH Admin", "tgh.wf@test.com", defaultPassword, "9876543212",
                    UserRole.HOSPITAL);
            userRepository.save(hospitalUser);

            Hospital hospital = new Hospital(
                    hospitalUser,
                    "Trichy Government Hospital",
                    "LIC-TRICHY-001",
                    "tgh.wf@test.com",
                    "9876543212",
                    "Collector Office Road, Cantonment",
                    "Trichy",
                    "Tamil Nadu",
                    true);
            hospitalRepository.save(hospital);

            BloodBank bloodBank = new BloodBank(
                    hospitalUser,
                    hospital,
                    "Trichy GH Central Blood Bank",
                    "BB-LIC-001",
                    "9876543212",
                    "tgh.wf@test.com",
                    "Collector Office Road, Cantonment",
                    "Trichy");
            bloodBankRepository.save(bloodBank);

            // Initialize Stock Inventory
            List<String> bloodGroups = Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
            int[] defaultStock = { 12, 5, 18, 4, 8, 3, 25, 6 };
            for (int i = 0; i < bloodGroups.size(); i++) {
                BloodInventory inventory = new BloodInventory(hospital, bloodBank, bloodGroups.get(i), defaultStock[i]);
                bloodInventoryRepository.save(inventory);
            }
            log.info("Initialized demo hospital, blood bank & inventory: tgh.wf@test.com");
        }

        // 4. Admin User
        if (!userRepository.existsByEmail("admin@test.com")) {
            User adminUser = new User("System Administrator", "admin@test.com", defaultPassword, "9876543200",
                    UserRole.ADMIN);
            userRepository.save(adminUser);
            log.info("Initialized demo admin: admin@test.com");
        }
    }
}
