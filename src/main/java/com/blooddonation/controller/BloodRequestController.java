package com.blooddonation.controller;

import com.blooddonation.dto.BloodRequestDTO;
import com.blooddonation.dto.BloodRequestResponseDTO;
import com.blooddonation.service.BloodRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/blood-requests", "/api/requests"})
public class BloodRequestController {

    private final BloodRequestService bloodRequestService;

    public BloodRequestController(BloodRequestService bloodRequestService) {
        this.bloodRequestService = bloodRequestService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RECIPIENT', 'HOSPITAL', 'ADMIN')")
    public ResponseEntity<BloodRequestResponseDTO> createRequest(
            @Valid @RequestBody BloodRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        BloodRequestResponseDTO created = bloodRequestService.createRequest(requestDTO, email);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BloodRequestResponseDTO>> getAllRequests() {
        List<BloodRequestResponseDTO> requests = bloodRequestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BloodRequestResponseDTO> getRequestById(@PathVariable Long id) {
        BloodRequestResponseDTO request = bloodRequestService.getRequestById(id);
        return ResponseEntity.ok(request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HOSPITAL', 'BLOOD_BANK', 'RECIPIENT', 'ADMIN')")
    public ResponseEntity<BloodRequestResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        BloodRequestResponseDTO updated = bloodRequestService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('RECIPIENT', 'HOSPITAL', 'ADMIN')")
    public ResponseEntity<BloodRequestResponseDTO> cancelRequest(@PathVariable Long id) {
        BloodRequestResponseDTO cancelled = bloodRequestService.cancelRequest(id);
        return ResponseEntity.ok(cancelled);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECIPIENT', 'ADMIN')")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        bloodRequestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<BloodRequestResponseDTO>> searchRequests(
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String urgency) {
        List<BloodRequestResponseDTO> results = bloodRequestService.searchRequests(bloodGroup, city, status, urgency);
        return ResponseEntity.ok(results);
    }
}
