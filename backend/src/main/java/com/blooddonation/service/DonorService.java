package com.blooddonation.service;

import com.blooddonation.dto.BloodRequestResponseDTO;
import com.blooddonation.dto.DonorRequestDTO;
import com.blooddonation.dto.DonorResponseDTO;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.exception.ValidationException;
import com.blooddonation.model.BloodCompatibilityHelper;
import com.blooddonation.model.BloodGroup;
import com.blooddonation.model.Donor;
import com.blooddonation.model.User;
import com.blooddonation.repository.BloodRequestRepository;
import com.blooddonation.repository.DonorRepository;
import com.blooddonation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DonorService {

    private final DonorRepository donorRepository;
    private final UserRepository userRepository;
    private final BloodRequestRepository bloodRequestRepository;

    public DonorService(DonorRepository donorRepository,
                        UserRepository userRepository,
                        BloodRequestRepository bloodRequestRepository) {
        this.donorRepository = donorRepository;
        this.userRepository = userRepository;
        this.bloodRequestRepository = bloodRequestRepository;
    }

    public DonorResponseDTO createDonor(DonorRequestDTO requestDTO) {
        return createDonor(requestDTO, null);
    }

    public DonorResponseDTO createDonor(DonorRequestDTO requestDTO, String userEmail) {
        validateDonorRequest(requestDTO);

        User user = null;
        if (userEmail != null && !userEmail.isBlank()) {
            user = userRepository.findByEmail(userEmail).orElse(null);
        } else if (requestDTO.getUserId() != null) {
            user = userRepository.findById(requestDTO.getUserId()).orElse(null);
        }

        Donor donor = new Donor(
                user,
                requestDTO.getName().trim(),
                requestDTO.getBloodGroup().trim().toUpperCase(),
                requestDTO.getCity().trim(),
                requestDTO.getPhone().trim(),
                requestDTO.isAvailable(),
                requestDTO.getLastDonationDate()
        );

        Donor savedDonor = donorRepository.save(donor);
        return DonorResponseDTO.fromEntity(savedDonor);
    }

    // Overloaded method for backward compatibility
    public Donor createDonor(Donor donor) {
        if (donor == null) {
            throw new ValidationException("Donor body cannot be null");
        }
        if (!BloodGroup.isValid(donor.getBloodGroup())) {
            throw new ValidationException("Invalid blood group: " + donor.getBloodGroup());
        }
        donor.setBloodGroup(donor.getBloodGroup().trim().toUpperCase());
        return donorRepository.save(donor);
    }

    @Transactional(readOnly = true)
    public List<DonorResponseDTO> getAllDonors() {
        return donorRepository.findAll().stream()
                .map(DonorResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DonorResponseDTO getDonorById(Long id) {
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donor", "id", id));
        return DonorResponseDTO.fromEntity(donor);
    }

    @Transactional(readOnly = true)
    public DonorResponseDTO getDonorByUserEmail(String email) {
        Donor donor = donorRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Donor profile for email: " + email));
        return DonorResponseDTO.fromEntity(donor);
    }

    public DonorResponseDTO updateDonor(Long id, DonorRequestDTO requestDTO) {
        validateDonorRequest(requestDTO);
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donor", "id", id));

        donor.setName(requestDTO.getName().trim());
        donor.setBloodGroup(requestDTO.getBloodGroup().trim().toUpperCase());
        donor.setCity(requestDTO.getCity().trim());
        donor.setPhone(requestDTO.getPhone().trim());
        donor.setAvailable(requestDTO.isAvailable());
        if (requestDTO.getLastDonationDate() != null) {
            donor.setLastDonationDate(requestDTO.getLastDonationDate());
        }

        Donor updatedDonor = donorRepository.save(donor);
        return DonorResponseDTO.fromEntity(updatedDonor);
    }

    public DonorResponseDTO updateAvailability(Long id, boolean available) {
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donor", "id", id));
        donor.setAvailable(available);
        Donor updated = donorRepository.save(donor);
        return DonorResponseDTO.fromEntity(updated);
    }

    public void deleteDonor(Long id) {
        if (!donorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Donor", "id", id);
        }
        donorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<DonorResponseDTO> searchDonors(String bloodGroup, String city, Boolean available) {
        if (bloodGroup != null && city != null && available != null) {
            return donorRepository.findByBloodGroupIgnoreCaseAndCityIgnoreCaseAndAvailable(bloodGroup, city, available)
                    .stream().map(DonorResponseDTO::fromEntity).collect(Collectors.toList());
        } else if (bloodGroup != null) {
            return donorRepository.findByBloodGroupIgnoreCase(bloodGroup)
                    .stream().map(DonorResponseDTO::fromEntity).collect(Collectors.toList());
        } else if (city != null) {
            return donorRepository.findByCityIgnoreCase(city)
                    .stream().map(DonorResponseDTO::fromEntity).collect(Collectors.toList());
        } else if (available != null) {
            return donorRepository.findByAvailable(available)
                    .stream().map(DonorResponseDTO::fromEntity).collect(Collectors.toList());
        }
        return getAllDonors();
    }

    @Transactional(readOnly = true)
    public List<BloodRequestResponseDTO> getEligibleRequestsForDonor(Long donorId) {
        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new ResourceNotFoundException("Donor", "id", donorId));

        List<String> compatibleGroups = BloodCompatibilityHelper.getCompatibleRecipientBloodGroups(donor.getBloodGroup());
        if (compatibleGroups.isEmpty()) {
            return Collections.emptyList();
        }

        return bloodRequestRepository.findByBloodGroupInAndStatusIn(compatibleGroups, List.of("PENDING", "MATCHED"))
                .stream()
                .map(BloodRequestResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private void validateDonorRequest(DonorRequestDTO dto) {
        if (dto == null) {
            throw new ValidationException("Donor request body cannot be null");
        }
        if (!BloodGroup.isValid(dto.getBloodGroup())) {
            throw new ValidationException("Invalid blood group: " + dto.getBloodGroup() + ". Allowed values: A+, A-, B+, B-, AB+, AB-, O+, O-");
        }
    }
}
