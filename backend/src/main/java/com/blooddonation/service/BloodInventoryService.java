package com.blooddonation.service;

import com.blooddonation.dto.BloodInventoryRequestDTO;
import com.blooddonation.dto.BloodInventoryResponseDTO;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.exception.ValidationException;
import com.blooddonation.model.*;
import com.blooddonation.repository.BloodBankRepository;
import com.blooddonation.repository.BloodInventoryRepository;
import com.blooddonation.repository.HospitalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BloodInventoryService {

    private final BloodInventoryRepository bloodInventoryRepository;
    private final HospitalRepository hospitalRepository;
    private final BloodBankRepository bloodBankRepository;

    public BloodInventoryService(BloodInventoryRepository bloodInventoryRepository,
                                 HospitalRepository hospitalRepository,
                                 BloodBankRepository bloodBankRepository) {
        this.bloodInventoryRepository = bloodInventoryRepository;
        this.hospitalRepository = hospitalRepository;
        this.bloodBankRepository = bloodBankRepository;
    }

    public BloodInventoryResponseDTO createOrUpdateInventory(BloodInventoryRequestDTO dto, String userEmail, boolean isAdmin) {
        validateBloodInventoryRequest(dto);

        Hospital hospital = null;
        BloodBank bloodBank = null;

        // Resolve organization from user profile or explicit DTO IDs (for admin)
        if (dto.getHospitalId() != null) {
            hospital = hospitalRepository.findById(dto.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital", "id", dto.getHospitalId()));
        } else if (dto.getBloodBankId() != null) {
            bloodBank = bloodBankRepository.findById(dto.getBloodBankId())
                    .orElseThrow(() -> new ResourceNotFoundException("BloodBank", "id", dto.getBloodBankId()));
        } else if (userEmail != null && !userEmail.isBlank()) {
            hospital = hospitalRepository.findByUserEmail(userEmail).orElse(null);
            if (hospital == null) {
                bloodBank = bloodBankRepository.findByUserEmail(userEmail).orElse(null);
            }
        }

        if (hospital == null && bloodBank == null) {
            throw new ValidationException("An inventory record must belong to a Hospital or Blood Bank");
        }

        String bloodGroup = dto.getBloodGroup().trim().toUpperCase();
        BloodInventory inventory;

        if (hospital != null) {
            // Check ownership if not admin
            if (!isAdmin && userEmail != null && hospital.getUser() != null) {
                if (!hospital.getUser().getEmail().equalsIgnoreCase(userEmail)) {
                    throw new ValidationException("Unauthorized: Cannot create inventory for another hospital");
                }
            }
            Optional<BloodInventory> existingOpt = bloodInventoryRepository.findByHospitalIdAndBloodGroupIgnoreCase(hospital.getId(), bloodGroup);
            if (existingOpt.isPresent()) {
                inventory = existingOpt.get();
                inventory.setAvailableUnits(dto.getAvailableUnits());
                inventory.setLastUpdated(LocalDateTime.now());
            } else {
                inventory = new BloodInventory(hospital, null, bloodGroup, dto.getAvailableUnits());
            }
        } else {
            // Check ownership if not admin
            if (!isAdmin && userEmail != null && bloodBank.getUser() != null) {
                if (!bloodBank.getUser().getEmail().equalsIgnoreCase(userEmail)) {
                    throw new ValidationException("Unauthorized: Cannot create inventory for another blood bank");
                }
            }
            Optional<BloodInventory> existingOpt = bloodInventoryRepository.findByBloodBankIdAndBloodGroupIgnoreCase(bloodBank.getId(), bloodGroup);
            if (existingOpt.isPresent()) {
                inventory = existingOpt.get();
                inventory.setAvailableUnits(dto.getAvailableUnits());
                inventory.setLastUpdated(LocalDateTime.now());
            } else {
                inventory = new BloodInventory(null, bloodBank, bloodGroup, dto.getAvailableUnits());
            }
        }

        BloodInventory saved = bloodInventoryRepository.save(inventory);
        return BloodInventoryResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<BloodInventoryResponseDTO> getInventoryForUser(String userEmail, boolean isAdmin) {
        if (isAdmin) {
            return bloodInventoryRepository.findAll().stream()
                    .map(BloodInventoryResponseDTO::fromEntity)
                    .collect(Collectors.toList());
        }

        if (userEmail == null || userEmail.isBlank()) {
            return Collections.emptyList();
        }

        Optional<Hospital> hospitalOpt = hospitalRepository.findByUserEmail(userEmail);
        if (hospitalOpt.isPresent()) {
            return bloodInventoryRepository.findByHospitalId(hospitalOpt.get().getId()).stream()
                    .map(BloodInventoryResponseDTO::fromEntity)
                    .collect(Collectors.toList());
        }

        Optional<BloodBank> bloodBankOpt = bloodBankRepository.findByUserEmail(userEmail);
        if (bloodBankOpt.isPresent()) {
            return bloodInventoryRepository.findByBloodBankId(bloodBankOpt.get().getId()).stream()
                    .map(BloodInventoryResponseDTO::fromEntity)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public List<BloodInventoryResponseDTO> searchByBloodGroup(String bloodGroup) {
        if (!BloodGroup.isValid(bloodGroup)) {
            throw new ValidationException("Invalid blood group: " + bloodGroup + ". Allowed: A+, A-, B+, B-, AB+, AB-, O+, O-");
        }
        return bloodInventoryRepository.findByBloodGroupIgnoreCase(bloodGroup.trim().toUpperCase()).stream()
                .map(BloodInventoryResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public BloodInventoryResponseDTO addUnits(Long inventoryId, int units, String userEmail, boolean isAdmin) {
        if (units <= 0) {
            throw new ValidationException("Units to add must be greater than zero");
        }

        BloodInventory inventory = bloodInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("BloodInventory", "id", inventoryId));

        validateInventoryOwnership(inventory, userEmail, isAdmin);

        inventory.setAvailableUnits(inventory.getAvailableUnits() + units);
        inventory.setLastUpdated(LocalDateTime.now());
        BloodInventory updated = bloodInventoryRepository.save(inventory);
        return BloodInventoryResponseDTO.fromEntity(updated);
    }

    public BloodInventoryResponseDTO removeUnits(Long inventoryId, int units, String userEmail, boolean isAdmin) {
        if (units <= 0) {
            throw new ValidationException("Units to remove must be greater than zero");
        }

        BloodInventory inventory = bloodInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("BloodInventory", "id", inventoryId));

        validateInventoryOwnership(inventory, userEmail, isAdmin);

        if (inventory.getAvailableUnits() < units) {
            throw new ValidationException("Insufficient inventory: Available " + inventory.getAvailableUnits() + " units, requested removal of " + units + " units");
        }

        inventory.setAvailableUnits(inventory.getAvailableUnits() - units);
        inventory.setLastUpdated(LocalDateTime.now());
        BloodInventory updated = bloodInventoryRepository.save(inventory);
        return BloodInventoryResponseDTO.fromEntity(updated);
    }

    public BloodInventoryResponseDTO updateInventory(Long id, BloodInventoryRequestDTO dto, String userEmail, boolean isAdmin) {
        validateBloodInventoryRequest(dto);

        BloodInventory inventory = bloodInventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BloodInventory", "id", id));

        validateInventoryOwnership(inventory, userEmail, isAdmin);

        inventory.setBloodGroup(dto.getBloodGroup().trim().toUpperCase());
        inventory.setAvailableUnits(dto.getAvailableUnits());
        inventory.setLastUpdated(LocalDateTime.now());

        BloodInventory updated = bloodInventoryRepository.save(inventory);
        return BloodInventoryResponseDTO.fromEntity(updated);
    }

    public void deductInventoryUnits(Long hospitalId, Long bloodBankId, String bloodGroup, int units) {
        if (units <= 0) {
            throw new ValidationException("Units to deduct must be greater than zero");
        }

        Optional<BloodInventory> inventoryOpt;
        if (hospitalId != null) {
            inventoryOpt = bloodInventoryRepository.findByHospitalIdAndBloodGroupIgnoreCase(hospitalId, bloodGroup);
        } else if (bloodBankId != null) {
            inventoryOpt = bloodInventoryRepository.findByBloodBankIdAndBloodGroupIgnoreCase(bloodBankId, bloodGroup);
        } else {
            throw new ValidationException("Must provide Hospital or Blood Bank to deduct inventory");
        }

        BloodInventory inventory = inventoryOpt.orElseThrow(() -> new ValidationException("No inventory found for blood group " + bloodGroup));

        if (inventory.getAvailableUnits() < units) {
            throw new ValidationException("Insufficient blood inventory: Required " + units + " units, but only " + inventory.getAvailableUnits() + " available");
        }

        inventory.setAvailableUnits(inventory.getAvailableUnits() - units);
        inventory.setLastUpdated(LocalDateTime.now());
        bloodInventoryRepository.save(inventory);
    }

    private void validateInventoryOwnership(BloodInventory inventory, String userEmail, boolean isAdmin) {
        if (isAdmin) {
            return;
        }
        if (userEmail == null || userEmail.isBlank()) {
            throw new ValidationException("Authentication required to modify inventory");
        }

        if (inventory.getHospital() != null && inventory.getHospital().getUser() != null) {
            if (!inventory.getHospital().getUser().getEmail().equalsIgnoreCase(userEmail)) {
                throw new ValidationException("Unauthorized: You do not own this hospital inventory");
            }
        } else if (inventory.getBloodBank() != null && inventory.getBloodBank().getUser() != null) {
            if (!inventory.getBloodBank().getUser().getEmail().equalsIgnoreCase(userEmail)) {
                throw new ValidationException("Unauthorized: You do not own this blood bank inventory");
            }
        }
    }

    private void validateBloodInventoryRequest(BloodInventoryRequestDTO dto) {
        if (dto == null) {
            throw new ValidationException("Blood inventory request body cannot be null");
        }
        if (!BloodGroup.isValid(dto.getBloodGroup())) {
            throw new ValidationException("Invalid blood group: " + dto.getBloodGroup() + ". Allowed: A+, A-, B+, B-, AB+, AB-, O+, O-");
        }
        if (dto.getAvailableUnits() < 0) {
            throw new ValidationException("Available units cannot be negative");
        }
    }
}
