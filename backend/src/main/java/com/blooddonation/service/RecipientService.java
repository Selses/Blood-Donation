package com.blooddonation.service;

import com.blooddonation.dto.BloodRequestResponseDTO;
import com.blooddonation.dto.RecipientRequestDTO;
import com.blooddonation.dto.RecipientResponseDTO;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.exception.ValidationException;
import com.blooddonation.model.BloodGroup;
import com.blooddonation.model.Recipient;
import com.blooddonation.model.User;
import com.blooddonation.repository.BloodRequestRepository;
import com.blooddonation.repository.RecipientRepository;
import com.blooddonation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecipientService {

    private final RecipientRepository recipientRepository;
    private final UserRepository userRepository;
    private final BloodRequestRepository bloodRequestRepository;

    public RecipientService(RecipientRepository recipientRepository,
                            UserRepository userRepository,
                            BloodRequestRepository bloodRequestRepository) {
        this.recipientRepository = recipientRepository;
        this.userRepository = userRepository;
        this.bloodRequestRepository = bloodRequestRepository;
    }

    public RecipientResponseDTO createRecipient(RecipientRequestDTO requestDTO, String userEmail) {
        validateRecipientRequest(requestDTO);

        User user = null;
        if (userEmail != null && !userEmail.isBlank()) {
            user = userRepository.findByEmail(userEmail).orElse(null);
        }

        Recipient recipient = new Recipient(
                user,
                requestDTO.getName().trim(),
                requestDTO.getBloodGroup().trim().toUpperCase(),
                requestDTO.getPhone().trim(),
                requestDTO.getCity().trim(),
                requestDTO.getHospitalName() != null ? requestDTO.getHospitalName().trim() : null,
                requestDTO.getEmergencyContact() != null ? requestDTO.getEmergencyContact().trim() : null
        );

        Recipient saved = recipientRepository.save(recipient);
        return RecipientResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public RecipientResponseDTO getRecipientById(Long id) {
        Recipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient", "id", id));
        return RecipientResponseDTO.fromEntity(recipient);
    }

    @Transactional(readOnly = true)
    public RecipientResponseDTO getRecipientByUserEmail(String email) {
        Recipient recipient = recipientRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient profile for email: " + email));
        return RecipientResponseDTO.fromEntity(recipient);
    }

    @Transactional(readOnly = true)
    public List<RecipientResponseDTO> getAllRecipients() {
        return recipientRepository.findAll().stream()
                .map(RecipientResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public RecipientResponseDTO updateRecipient(Long id, RecipientRequestDTO requestDTO) {
        validateRecipientRequest(requestDTO);

        Recipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient", "id", id));

        recipient.setName(requestDTO.getName().trim());
        recipient.setBloodGroup(requestDTO.getBloodGroup().trim().toUpperCase());
        recipient.setPhone(requestDTO.getPhone().trim());
        recipient.setCity(requestDTO.getCity().trim());
        if (requestDTO.getHospitalName() != null) {
            recipient.setHospitalName(requestDTO.getHospitalName().trim());
        }
        if (requestDTO.getEmergencyContact() != null) {
            recipient.setEmergencyContact(requestDTO.getEmergencyContact().trim());
        }

        Recipient updated = recipientRepository.save(recipient);
        return RecipientResponseDTO.fromEntity(updated);
    }

    public void deleteRecipient(Long id) {
        if (!recipientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recipient", "id", id);
        }
        recipientRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<BloodRequestResponseDTO> getRequestsForRecipient(Long recipientId) {
        if (!recipientRepository.existsById(recipientId)) {
            throw new ResourceNotFoundException("Recipient", "id", recipientId);
        }
        return bloodRequestRepository.findByRecipientId(recipientId).stream()
                .map(BloodRequestResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private void validateRecipientRequest(RecipientRequestDTO dto) {
        if (dto == null) {
            throw new ValidationException("Recipient request body cannot be null");
        }
        if (!BloodGroup.isValid(dto.getBloodGroup())) {
            throw new ValidationException("Invalid blood group: " + dto.getBloodGroup() + ". Allowed values: A+, A-, B+, B-, AB+, AB-, O+, O-");
        }
    }
}
