package com.blooddonation.service;

import com.blooddonation.dto.HospitalRequestDTO;
import com.blooddonation.dto.HospitalResponseDTO;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.exception.ValidationException;
import com.blooddonation.model.Hospital;
import com.blooddonation.model.User;
import com.blooddonation.repository.HospitalRepository;
import com.blooddonation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;

    public HospitalService(HospitalRepository hospitalRepository, UserRepository userRepository) {
        this.hospitalRepository = hospitalRepository;
        this.userRepository = userRepository;
    }

    public HospitalResponseDTO createHospital(HospitalRequestDTO dto, String userEmail) {
        if (dto.getLicenseNumber() != null && !dto.getLicenseNumber().isBlank()) {
            if (hospitalRepository.existsByLicenseNumber(dto.getLicenseNumber().trim())) {
                throw new ValidationException("Hospital with license number " + dto.getLicenseNumber() + " already exists");
            }
        }

        User user = null;
        if (userEmail != null && !userEmail.isBlank()) {
            user = userRepository.findByEmail(userEmail).orElse(null);
        }

        String license = (dto.getLicenseNumber() != null && !dto.getLicenseNumber().isBlank())
                ? dto.getLicenseNumber().trim()
                : "HOSP-LIC-" + System.currentTimeMillis();

        Hospital hospital = new Hospital(
                user,
                dto.getName().trim(),
                license,
                dto.getEmail() != null ? dto.getEmail().trim() : (user != null ? user.getEmail() : null),
                dto.getPhone().trim(),
                dto.getAddress(),
                dto.getCity().trim(),
                dto.getState(),
                false
        );

        Hospital saved = hospitalRepository.save(hospital);
        return HospitalResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<HospitalResponseDTO> getAllHospitals() {
        return hospitalRepository.findAll().stream()
                .map(HospitalResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HospitalResponseDTO getHospitalById(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital", "id", id));
        return HospitalResponseDTO.fromEntity(hospital);
    }

    @Transactional(readOnly = true)
    public Hospital getHospitalEntityById(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital", "id", id));
    }

    @Transactional(readOnly = true)
    public Hospital getHospitalEntityByUserEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return hospitalRepository.findByUserEmail(email).orElse(null);
    }

    public HospitalResponseDTO updateHospital(Long id, HospitalRequestDTO dto, String userEmail, boolean isAdmin) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital", "id", id));

        // Ownership verification if not admin
        if (!isAdmin && userEmail != null && !userEmail.isBlank()) {
            if (hospital.getUser() != null && !hospital.getUser().getEmail().equalsIgnoreCase(userEmail)) {
                throw new ValidationException("Unauthorized: You can only update your own hospital profile");
            }
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            hospital.setName(dto.getName().trim());
        }
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            hospital.setPhone(dto.getPhone().trim());
        }
        if (dto.getEmail() != null) {
            hospital.setEmail(dto.getEmail().trim());
        }
        if (dto.getAddress() != null) {
            hospital.setAddress(dto.getAddress().trim());
        }
        if (dto.getCity() != null && !dto.getCity().isBlank()) {
            hospital.setCity(dto.getCity().trim());
        }
        if (dto.getState() != null) {
            hospital.setState(dto.getState().trim());
        }

        Hospital updated = hospitalRepository.save(hospital);
        return HospitalResponseDTO.fromEntity(updated);
    }
}
