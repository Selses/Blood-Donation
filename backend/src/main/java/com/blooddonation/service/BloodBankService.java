package com.blooddonation.service;

import com.blooddonation.dto.BloodBankRequestDTO;
import com.blooddonation.dto.BloodBankResponseDTO;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.exception.ValidationException;
import com.blooddonation.model.BloodBank;
import com.blooddonation.model.Hospital;
import com.blooddonation.model.User;
import com.blooddonation.repository.BloodBankRepository;
import com.blooddonation.repository.HospitalRepository;
import com.blooddonation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BloodBankService {

    private final BloodBankRepository bloodBankRepository;
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;

    public BloodBankService(BloodBankRepository bloodBankRepository,
                            HospitalRepository hospitalRepository,
                            UserRepository userRepository) {
        this.bloodBankRepository = bloodBankRepository;
        this.hospitalRepository = hospitalRepository;
        this.userRepository = userRepository;
    }

    public BloodBankResponseDTO createBloodBank(BloodBankRequestDTO dto, String userEmail) {
        if (dto.getLicenseNumber() != null && !dto.getLicenseNumber().isBlank()) {
            if (bloodBankRepository.existsByLicenseNumber(dto.getLicenseNumber().trim())) {
                throw new ValidationException("Blood bank with license number " + dto.getLicenseNumber() + " already exists");
            }
        }

        User user = null;
        if (userEmail != null && !userEmail.isBlank()) {
            user = userRepository.findByEmail(userEmail).orElse(null);
        }

        Hospital hospital = null;
        if (dto.getHospitalId() != null) {
            hospital = hospitalRepository.findById(dto.getHospitalId()).orElse(null);
        }

        String license = (dto.getLicenseNumber() != null && !dto.getLicenseNumber().isBlank())
                ? dto.getLicenseNumber().trim()
                : "BB-LIC-" + System.currentTimeMillis();

        BloodBank bloodBank = new BloodBank(
                user,
                hospital,
                dto.getName().trim(),
                license,
                dto.getPhone().trim(),
                dto.getEmail() != null ? dto.getEmail().trim() : (user != null ? user.getEmail() : null),
                dto.getAddress(),
                dto.getCity().trim()
        );

        BloodBank saved = bloodBankRepository.save(bloodBank);
        return BloodBankResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<BloodBankResponseDTO> getAllBloodBanks() {
        return bloodBankRepository.findAll().stream()
                .map(BloodBankResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BloodBankResponseDTO getBloodBankById(Long id) {
        BloodBank bloodBank = bloodBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BloodBank", "id", id));
        return BloodBankResponseDTO.fromEntity(bloodBank);
    }

    @Transactional(readOnly = true)
    public BloodBank getBloodBankEntityById(Long id) {
        return bloodBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BloodBank", "id", id));
    }

    @Transactional(readOnly = true)
    public BloodBank getBloodBankEntityByUserEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return bloodBankRepository.findByUserEmail(email).orElse(null);
    }

    public BloodBankResponseDTO updateBloodBank(Long id, BloodBankRequestDTO dto, String userEmail, boolean isAdmin) {
        BloodBank bloodBank = bloodBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BloodBank", "id", id));

        // Ownership verification if not admin
        if (!isAdmin && userEmail != null && !userEmail.isBlank()) {
            if (bloodBank.getUser() != null && !bloodBank.getUser().getEmail().equalsIgnoreCase(userEmail)) {
                throw new ValidationException("Unauthorized: You can only update your own blood bank profile");
            }
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            bloodBank.setName(dto.getName().trim());
        }
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            bloodBank.setPhone(dto.getPhone().trim());
        }
        if (dto.getEmail() != null) {
            bloodBank.setEmail(dto.getEmail().trim());
        }
        if (dto.getAddress() != null) {
            bloodBank.setAddress(dto.getAddress().trim());
        }
        if (dto.getCity() != null && !dto.getCity().isBlank()) {
            bloodBank.setCity(dto.getCity().trim());
        }
        if (dto.getHospitalId() != null) {
            hospitalRepository.findById(dto.getHospitalId()).ifPresent(bloodBank::setHospital);
        }

        BloodBank updated = bloodBankRepository.save(bloodBank);
        return BloodBankResponseDTO.fromEntity(updated);
    }
}
