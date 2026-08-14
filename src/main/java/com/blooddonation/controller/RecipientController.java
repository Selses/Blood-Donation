package com.blooddonation.controller;

import com.blooddonation.dto.BloodRequestResponseDTO;
import com.blooddonation.dto.RecipientRequestDTO;
import com.blooddonation.dto.RecipientResponseDTO;
import com.blooddonation.service.BloodRequestService;
import com.blooddonation.service.RecipientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipients")
public class RecipientController {

    private final RecipientService recipientService;
    private final BloodRequestService bloodRequestService;

    public RecipientController(RecipientService recipientService, BloodRequestService bloodRequestService) {
        this.recipientService = recipientService;
        this.bloodRequestService = bloodRequestService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RECIPIENT', 'ADMIN')")
    public ResponseEntity<RecipientResponseDTO> createRecipient(
            @Valid @RequestBody RecipientRequestDTO recipientDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        RecipientResponseDTO created = recipientService.createRecipient(recipientDTO, email);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('RECIPIENT')")
    public ResponseEntity<RecipientResponseDTO> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        RecipientResponseDTO recipient = recipientService.getRecipientByUserEmail(userDetails.getUsername());
        return ResponseEntity.ok(recipient);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipientResponseDTO> getRecipientById(@PathVariable Long id) {
        RecipientResponseDTO recipient = recipientService.getRecipientById(id);
        return ResponseEntity.ok(recipient);
    }

    @GetMapping
    public ResponseEntity<List<RecipientResponseDTO>> getAllRecipients() {
        List<RecipientResponseDTO> recipients = recipientService.getAllRecipients();
        return ResponseEntity.ok(recipients);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECIPIENT', 'ADMIN')")
    public ResponseEntity<RecipientResponseDTO> updateRecipient(
            @PathVariable Long id,
            @Valid @RequestBody RecipientRequestDTO recipientDTO) {
        RecipientResponseDTO updated = recipientService.updateRecipient(id, recipientDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRecipient(@PathVariable Long id) {
        recipientService.deleteRecipient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/requests")
    public ResponseEntity<List<BloodRequestResponseDTO>> getRecipientRequests(@PathVariable Long id) {
        List<BloodRequestResponseDTO> requests = recipientService.getRequestsForRecipient(id);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/me/requests")
    @PreAuthorize("hasRole('RECIPIENT')")
    public ResponseEntity<List<BloodRequestResponseDTO>> getMyRequests(@AuthenticationPrincipal UserDetails userDetails) {
        List<BloodRequestResponseDTO> requests = bloodRequestService.getRequestsByRecipientEmail(userDetails.getUsername());
        return ResponseEntity.ok(requests);
    }
}
