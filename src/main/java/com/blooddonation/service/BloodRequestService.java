package com.blooddonation.service;

import com.blooddonation.dto.BloodRequestDTO;
import com.blooddonation.dto.BloodRequestResponseDTO;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.exception.ValidationException;
import com.blooddonation.model.BloodGroup;
import com.blooddonation.model.BloodRequest;
import com.blooddonation.model.RequestStatus;
import com.blooddonation.model.UrgencyLevel;
import com.blooddonation.repository.BloodRequestRepository;
import com.blooddonation.repository.HospitalRepository;
import com.blooddonation.repository.RecipientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class BloodRequestService {

    private final BloodRequestRepository bloodRequestRepository;
    private final RecipientRepository recipientRepository;
    private final HospitalRepository hospitalRepository;

    public BloodRequestService(BloodRequestRepository bloodRequestRepository,
                               RecipientRepository recipientRepository,
                               HospitalRepository hospitalRepository) {
        this.bloodRequestRepository = bloodRequestRepository;
        this.recipientRepository = recipientRepository;
        this.hospitalRepository = hospitalRepository;
    }

    public BloodRequestResponseDTO createRequest(BloodRequestDTO requestDTO) {
        return createRequest(requestDTO, null);
    }

    public BloodRequestResponseDTO createRequest(BloodRequestDTO requestDTO, String recipientUserEmail) {
        validateBloodRequest(requestDTO);

        BloodRequest request = new BloodRequest(
                requestDTO.getRecipientName().trim(),
                requestDTO.getBloodGroup().trim().toUpperCase(),
                requestDTO.getHospitalName().trim(),
                requestDTO.getCity().trim(),
                requestDTO.getUrgency() != null ? requestDTO.getUrgency().trim().toUpperCase() : "HIGH",
                requestDTO.getStatus() != null ? requestDTO.getStatus().trim().toUpperCase() : "PENDING"
        );
        request.setRequiredUnits(requestDTO.getRequiredUnits() > 0 ? requestDTO.getRequiredUnits() : 1);

        if (recipientUserEmail != null && !recipientUserEmail.isBlank()) {
            recipientRepository.findByUserEmail(recipientUserEmail).ifPresent(request::setRecipient);
        }

        BloodRequest savedRequest = bloodRequestRepository.save(request);
        return BloodRequestResponseDTO.fromEntity(savedRequest);
    }

    // Overloaded method for backward compatibility
    public BloodRequest createRequest(BloodRequest request) {
        if (request == null) {
            throw new ValidationException("Blood request body cannot be null");
        }
        if (!BloodGroup.isValid(request.getBloodGroup())) {
            throw new ValidationException("Invalid blood group: " + request.getBloodGroup());
        }
        if (request.getUrgency() != null && !UrgencyLevel.isValid(request.getUrgency())) {
            throw new ValidationException("Invalid urgency level: " + request.getUrgency());
        }
        if (request.getStatus() != null && !RequestStatus.isValid(request.getStatus())) {
            throw new ValidationException("Invalid request status: " + request.getStatus());
        }
        if (request.getBloodGroup() != null) {
            request.setBloodGroup(request.getBloodGroup().trim().toUpperCase());
        }
        if (request.getUrgency() != null) {
            request.setUrgency(request.getUrgency().trim().toUpperCase());
        }
        if (request.getStatus() != null) {
            request.setStatus(request.getStatus().trim().toUpperCase());
        }
        return bloodRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<BloodRequestResponseDTO> getAllRequests() {
        return bloodRequestRepository.findAll().stream()
                .map(BloodRequestResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BloodRequestResponseDTO getRequestById(Long id) {
        BloodRequest request = bloodRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BloodRequest", "id", id));
        return BloodRequestResponseDTO.fromEntity(request);
    }

    public BloodRequestResponseDTO updateStatus(Long id, String newStatusStr) {
        if (!RequestStatus.isValid(newStatusStr)) {
            throw new ValidationException("Invalid request status: " + newStatusStr + ". Allowed values: PENDING, MATCHED, ACCEPTED, FULFILLED, CANCELLED");
        }

        BloodRequest request = bloodRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BloodRequest", "id", id));

        RequestStatus currentStatus = RequestStatus.fromString(request.getStatus());
        RequestStatus targetStatus = RequestStatus.fromString(newStatusStr);

        validateStatusTransition(currentStatus, targetStatus);

        request.setStatus(targetStatus.name());
        BloodRequest updated = bloodRequestRepository.save(request);
        return BloodRequestResponseDTO.fromEntity(updated);
    }

    public BloodRequestResponseDTO cancelRequest(Long id) {
        BloodRequest request = bloodRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BloodRequest", "id", id));

        RequestStatus currentStatus = RequestStatus.fromString(request.getStatus());
        if (currentStatus == RequestStatus.FULFILLED) {
            throw new ValidationException("Cannot cancel a blood request that has already been FULFILLED");
        }
        if (currentStatus == RequestStatus.CANCELLED) {
            throw new ValidationException("Blood request is already CANCELLED");
        }

        request.setStatus(RequestStatus.CANCELLED.name());
        BloodRequest updated = bloodRequestRepository.save(request);
        return BloodRequestResponseDTO.fromEntity(updated);
    }

    public void deleteRequest(Long id) {
        if (!bloodRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("BloodRequest", "id", id);
        }
        bloodRequestRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<BloodRequestResponseDTO> searchRequests(String bloodGroup, String city, String status, String urgency) {
        if (status != null) {
            return bloodRequestRepository.findByStatusIgnoreCase(status)
                    .stream().map(BloodRequestResponseDTO::fromEntity).collect(Collectors.toList());
        } else if (bloodGroup != null) {
            return bloodRequestRepository.findByBloodGroupIgnoreCase(bloodGroup)
                    .stream().map(BloodRequestResponseDTO::fromEntity).collect(Collectors.toList());
        } else if (city != null) {
            return bloodRequestRepository.findByCityIgnoreCase(city)
                    .stream().map(BloodRequestResponseDTO::fromEntity).collect(Collectors.toList());
        } else if (urgency != null) {
            return bloodRequestRepository.findByUrgencyIgnoreCase(urgency)
                    .stream().map(BloodRequestResponseDTO::fromEntity).collect(Collectors.toList());
        }
        return getAllRequests();
    }

    @Transactional(readOnly = true)
    public List<BloodRequestResponseDTO> getRequestsByRecipientEmail(String email) {
        return bloodRequestRepository.findByRecipientUserEmail(email).stream()
                .map(BloodRequestResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private void validateStatusTransition(RequestStatus current, RequestStatus target) {
        if (current == target) {
            return;
        }

        if (current == RequestStatus.FULFILLED) {
            throw new ValidationException("Cannot change status of a FULFILLED blood request");
        }
        if (current == RequestStatus.CANCELLED) {
            throw new ValidationException("Cannot change status of a CANCELLED blood request");
        }

        switch (current) {
            case PENDING:
                if (target != RequestStatus.MATCHED && target != RequestStatus.CANCELLED) {
                    throw new ValidationException("Invalid status transition from PENDING to " + target + ". Allowed: MATCHED, CANCELLED");
                }
                break;
            case MATCHED:
                if (target != RequestStatus.ACCEPTED && target != RequestStatus.CANCELLED && target != RequestStatus.PENDING) {
                    throw new ValidationException("Invalid status transition from MATCHED to " + target + ". Allowed: ACCEPTED, PENDING, CANCELLED");
                }
                break;
            case ACCEPTED:
                if (target != RequestStatus.FULFILLED && target != RequestStatus.CANCELLED) {
                    throw new ValidationException("Invalid status transition from ACCEPTED to " + target + ". Allowed: FULFILLED, CANCELLED");
                }
                break;
            default:
                throw new ValidationException("Unsupported status transition from " + current + " to " + target);
        }
    }

    private void validateBloodRequest(BloodRequestDTO dto) {
        if (dto == null) {
            throw new ValidationException("Blood request body cannot be null");
        }
        if (!BloodGroup.isValid(dto.getBloodGroup())) {
            throw new ValidationException("Invalid blood group: " + dto.getBloodGroup() + ". Allowed values: A+, A-, B+, B-, AB+, AB-, O+, O-");
        }
        if (dto.getUrgency() != null && !UrgencyLevel.isValid(dto.getUrgency())) {
            throw new ValidationException("Invalid urgency level: " + dto.getUrgency() + ". Allowed values: LOW, MEDIUM, HIGH, CRITICAL");
        }
        if (dto.getStatus() != null && !RequestStatus.isValid(dto.getStatus())) {
            throw new ValidationException("Invalid request status: " + dto.getStatus() + ". Allowed values: PENDING, MATCHED, ACCEPTED, FULFILLED, CANCELLED");
        }
        if (dto.getRequiredUnits() <= 0) {
            throw new ValidationException("Required units must be at least 1");
        }
    }
}
