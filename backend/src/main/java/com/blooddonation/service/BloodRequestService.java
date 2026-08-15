package com.blooddonation.service;

import com.blooddonation.dto.BloodRequestDTO;
import com.blooddonation.dto.BloodRequestResponseDTO;
import com.blooddonation.dto.MatchResultDTO;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.exception.ValidationException;
import com.blooddonation.model.*;
import com.blooddonation.repository.BloodBankRepository;
import com.blooddonation.repository.BloodMatchRepository;
import com.blooddonation.repository.BloodRequestRepository;
import com.blooddonation.repository.DonorRepository;
import com.blooddonation.repository.HospitalRepository;
import com.blooddonation.repository.RecipientRepository;
import com.blooddonation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BloodRequestService {

    private final BloodRequestRepository bloodRequestRepository;
    private final RecipientRepository recipientRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final BloodBankRepository bloodBankRepository;
    private final DonorRepository donorRepository;
    private final BloodMatchRepository bloodMatchRepository;
    private final MatchingService matchingService;
    private final NotificationService notificationService;
    private final BloodInventoryService bloodInventoryService;

    public BloodRequestService(BloodRequestRepository bloodRequestRepository,
                               RecipientRepository recipientRepository,
                               UserRepository userRepository,
                               HospitalRepository hospitalRepository,
                               BloodBankRepository bloodBankRepository,
                               DonorRepository donorRepository,
                               BloodMatchRepository bloodMatchRepository,
                               MatchingService matchingService,
                               NotificationService notificationService,
                               BloodInventoryService bloodInventoryService) {
        this.bloodRequestRepository = bloodRequestRepository;
        this.recipientRepository = recipientRepository;
        this.userRepository = userRepository;
        this.hospitalRepository = hospitalRepository;
        this.bloodBankRepository = bloodBankRepository;
        this.donorRepository = donorRepository;
        this.bloodMatchRepository = bloodMatchRepository;
        this.matchingService = matchingService;
        this.notificationService = notificationService;
        this.bloodInventoryService = bloodInventoryService;
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
                "PENDING"
        );
        request.setRequiredUnits(requestDTO.getRequiredUnits() > 0 ? requestDTO.getRequiredUnits() : 1);

        if (recipientUserEmail != null && !recipientUserEmail.isBlank()) {
            Recipient recipient = recipientRepository.findByUserEmail(recipientUserEmail).orElse(null);
            if (recipient == null) {
                User user = userRepository.findByEmail(recipientUserEmail).orElse(null);
                if (user != null) {
                    recipient = recipientRepository.findByUserId(user.getId()).orElse(null);
                    if (recipient == null) {
                        // Auto-create Recipient entity linked to this user
                        recipient = new Recipient(
                                user,
                                requestDTO.getRecipientName(),
                                requestDTO.getBloodGroup(),
                                user.getPhone() != null ? user.getPhone() : "N/A",
                                requestDTO.getCity(),
                                requestDTO.getHospitalName(),
                                null
                        );
                        recipient = recipientRepository.save(recipient);
                    }
                }
            }
            request.setRecipient(recipient);
        }

        BloodRequest savedRequest = bloodRequestRepository.save(request);

        // Run matching to determine whether compatible donors exist
        List<MatchResultDTO> matches = matchingService.findMatchesForRequest(savedRequest.getId());
        if (!matches.isEmpty()) {
            savedRequest.setStatus(RequestStatus.MATCHED.name());
            savedRequest = bloodRequestRepository.save(savedRequest);
        }

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
        if (request.getBloodGroup() != null) {
            request.setBloodGroup(request.getBloodGroup().trim().toUpperCase());
        }
        if (request.getUrgency() != null) {
            request.setUrgency(request.getUrgency().trim().toUpperCase());
        }
        request.setStatus(RequestStatus.PENDING.name());
        BloodRequest saved = bloodRequestRepository.save(request);

        List<MatchResultDTO> matches = matchingService.findMatchesForRequest(saved.getId());
        if (!matches.isEmpty()) {
            saved.setStatus(RequestStatus.MATCHED.name());
            saved = bloodRequestRepository.save(saved);
        }
        return saved;
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

    public List<MatchResultDTO> findMatchesForRequest(Long requestId) {
        return matchingService.findMatchesForRequest(requestId);
    }

    public MatchResultDTO acceptMatch(Long requestId, Long matchId, String donorUserEmail) {
        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("BloodRequest", "id", requestId));

        BloodMatch match = bloodMatchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("BloodMatch", "id", matchId));

        if (!match.getBloodRequest().getId().equals(requestId)) {
            throw new ValidationException("Match ID " + matchId + " does not belong to Blood Request ID " + requestId);
        }

        // Ownership check for donor
        if (donorUserEmail != null && !donorUserEmail.isBlank()) {
            Donor authenticatedDonor = donorRepository.findByUserEmail(donorUserEmail)
                    .orElseThrow(() -> new ValidationException("Authenticated user has no associated Donor profile"));
            if (!match.getDonor().getId().equals(authenticatedDonor.getId())) {
                throw new ValidationException("Unauthorized: You can only accept matches assigned to your donor profile");
            }
        }

        // Check request status
        RequestStatus currentRequestStatus = RequestStatus.fromString(request.getStatus());
        if (currentRequestStatus == RequestStatus.ACCEPTED) {
            throw new ValidationException("This blood request has already been accepted by another donor.");
        }
        if (currentRequestStatus == RequestStatus.FULFILLED) {
            throw new ValidationException("Cannot accept a blood request that is already FULFILLED");
        }
        if (currentRequestStatus == RequestStatus.CANCELLED) {
            throw new ValidationException("Cannot accept a CANCELLED blood request");
        }
        if (currentRequestStatus != RequestStatus.MATCHED && currentRequestStatus != RequestStatus.PENDING) {
            throw new ValidationException("Blood request is not eligible for acceptance in status: " + currentRequestStatus);
        }

        // Check match status
        MatchStatus currentMatchStatus = MatchStatus.fromString(match.getStatus());
        if (currentMatchStatus == MatchStatus.ACCEPTED) {
            throw new ValidationException("This match is already accepted");
        }
        if (currentMatchStatus == MatchStatus.DECLINED || currentMatchStatus == MatchStatus.CANCELLED) {
            throw new ValidationException("Cannot accept a match with status: " + currentMatchStatus);
        }

        // Update match and request
        match.setStatus(MatchStatus.ACCEPTED.name());
        bloodMatchRepository.save(match);

        request.setStatus(RequestStatus.ACCEPTED.name());
        bloodRequestRepository.save(request);

        // Notify Recipient if recipient user exists
        notifyRecipientIfPresent(request, "Donor Accepted", "A compatible donor has accepted your blood request.", NotificationType.DONOR_ACCEPTED.name());

        return MatchResultDTO.fromEntity(match);
    }

    public MatchResultDTO declineMatch(Long requestId, Long matchId, String donorUserEmail) {
        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("BloodRequest", "id", requestId));

        BloodMatch match = bloodMatchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("BloodMatch", "id", matchId));

        if (!match.getBloodRequest().getId().equals(requestId)) {
            throw new ValidationException("Match ID " + matchId + " does not belong to Blood Request ID " + requestId);
        }

        // Ownership check for donor
        if (donorUserEmail != null && !donorUserEmail.isBlank()) {
            Donor authenticatedDonor = donorRepository.findByUserEmail(donorUserEmail)
                    .orElseThrow(() -> new ValidationException("Authenticated user has no associated Donor profile"));
            if (!match.getDonor().getId().equals(authenticatedDonor.getId())) {
                throw new ValidationException("Unauthorized: You can only decline matches assigned to your donor profile");
            }
        }

        match.setStatus(MatchStatus.DECLINED.name());
        bloodMatchRepository.save(match);

        // Check remaining active pending matches for this request
        long activePendingMatches = bloodMatchRepository.countByBloodRequestIdAndStatusIgnoreCase(requestId, MatchStatus.PENDING.name());
        long activeAcceptedMatches = bloodMatchRepository.countByBloodRequestIdAndStatusIgnoreCase(requestId, MatchStatus.ACCEPTED.name());

        if (activeAcceptedMatches == 0) {
            if (activePendingMatches == 0) {
                request.setStatus(RequestStatus.PENDING.name());
                bloodRequestRepository.save(request);
            } else {
                request.setStatus(RequestStatus.MATCHED.name());
                bloodRequestRepository.save(request);
            }
        }

        // Notify Recipient when donor declines
        notifyRecipientIfPresent(request, "Donor Update", "A matched donor declined the blood request. Other matching donors may still be available.", NotificationType.DONOR_DECLINED.name());

        return MatchResultDTO.fromEntity(match);
    }

    public BloodRequestResponseDTO fulfillRequest(Long requestId, String userEmail) {
        return fulfillRequest(requestId, null, userEmail);
    }

    public BloodRequestResponseDTO fulfillRequest(Long requestId, com.blooddonation.dto.FulfillRequestDTO fulfillDTO, String userEmail) {
        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("BloodRequest", "id", requestId));

        RequestStatus currentStatus = RequestStatus.fromString(request.getStatus());
        if (currentStatus != RequestStatus.ACCEPTED && currentStatus != RequestStatus.MATCHED && currentStatus != RequestStatus.PENDING) {
            throw new ValidationException("Only an active blood request can be fulfilled. Current status: " + currentStatus);
        }

        String source = (fulfillDTO != null && fulfillDTO.getSource() != null)
                ? fulfillDTO.getSource().trim().toUpperCase()
                : "DONOR";

        if ("INVENTORY".equalsIgnoreCase(source)) {
            // Inventory-based fulfillment
            int requiredUnits = request.getRequiredUnits() > 0 ? request.getRequiredUnits() : 1;
            Long hospitalId = null;
            Long bloodBankId = null;

            if (userEmail != null && !userEmail.isBlank()) {
                Hospital hospital = hospitalRepository.findByUserEmail(userEmail).orElse(null);
                if (hospital != null) {
                    hospitalId = hospital.getId();
                } else {
                    BloodBank bloodBank = bloodBankRepository.findByUserEmail(userEmail).orElse(null);
                    if (bloodBank != null) {
                        bloodBankId = bloodBank.getId();
                    }
                }
            }

            if (hospitalId == null && bloodBankId == null && request.getHospital() != null) {
                hospitalId = request.getHospital().getId();
            }

            // Deduct units from inventory atomically
            bloodInventoryService.deductInventoryUnits(hospitalId, bloodBankId, request.getBloodGroup(), requiredUnits);

        } else {
            // Donor-based fulfillment (Phase 6 rule: verify accepted match exists)
            if (currentStatus != RequestStatus.ACCEPTED) {
                throw new ValidationException("Only an accepted blood request can be fulfilled through donor. Current status: " + currentStatus);
            }
            long acceptedMatchesCount = bloodMatchRepository.countByBloodRequestIdAndStatusIgnoreCase(requestId, MatchStatus.ACCEPTED.name());
            if (acceptedMatchesCount == 0) {
                throw new ValidationException("Cannot fulfill request: No accepted donor match found for this request");
            }
        }

        request.setStatus(RequestStatus.FULFILLED.name());
        BloodRequest updated = bloodRequestRepository.save(request);

        // Notify Recipient upon fulfillment
        notifyRecipientIfPresent(request, "Blood Request Fulfilled", "Your blood request has been successfully fulfilled.", NotificationType.REQUEST_UPDATED.name());

        return BloodRequestResponseDTO.fromEntity(updated);
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

        // Notify recipient if status changed
        if (currentStatus != targetStatus) {
            notifyRecipientIfPresent(request, "Blood Request Updated", "Your blood request status has changed to " + targetStatus.name() + ".", NotificationType.REQUEST_UPDATED.name());
        }

        return BloodRequestResponseDTO.fromEntity(updated);
    }

    public BloodRequestResponseDTO cancelRequest(Long id) {
        return cancelRequest(id, null, false);
    }

    public BloodRequestResponseDTO cancelRequest(Long id, String userEmail, boolean isAdmin) {
        BloodRequest request = bloodRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BloodRequest", "id", id));

        // Ownership verification for recipients if not admin
        if (!isAdmin && userEmail != null && !userEmail.isBlank()) {
            if (request.getRecipient() != null) {
                String ownerEmail = null;
                if (request.getRecipient().getUser() != null) {
                    ownerEmail = request.getRecipient().getUser().getEmail();
                }
                if (ownerEmail != null && !ownerEmail.equalsIgnoreCase(userEmail)) {
                    throw new ValidationException("Unauthorized: You can only cancel your own blood request");
                }
            }
        }

        RequestStatus currentStatus = RequestStatus.fromString(request.getStatus());
        if (currentStatus == RequestStatus.FULFILLED) {
            throw new ValidationException("Cannot cancel a blood request that has already been FULFILLED");
        }
        if (currentStatus == RequestStatus.CANCELLED) {
            throw new ValidationException("Blood request is already CANCELLED");
        }

        request.setStatus(RequestStatus.CANCELLED.name());

        // Cancel pending/accepted matches
        List<BloodMatch> matches = bloodMatchRepository.findByBloodRequestId(id);
        for (BloodMatch m : matches) {
            if (!MatchStatus.DECLINED.name().equalsIgnoreCase(m.getStatus())) {
                m.setStatus(MatchStatus.CANCELLED.name());
                bloodMatchRepository.save(m);
            }
        }

        BloodRequest updated = bloodRequestRepository.save(request);

        notifyRecipientIfPresent(request, "Blood Request Updated", "Your blood request status has changed to CANCELLED.", NotificationType.REQUEST_UPDATED.name());

        return BloodRequestResponseDTO.fromEntity(updated);
    }

    private void notifyRecipientIfPresent(BloodRequest request, String title, String message, String type) {
        if (request != null && request.getRecipient() != null && request.getRecipient().getUser() != null) {
            notificationService.createNotification(
                    request.getRecipient().getUser(),
                    title,
                    message,
                    type,
                    request
            );
        }
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

