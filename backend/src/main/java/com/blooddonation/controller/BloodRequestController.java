package com.blooddonation.controller;

import com.blooddonation.dto.BloodRequestDTO;
import com.blooddonation.dto.BloodRequestResponseDTO;
import com.blooddonation.dto.MatchResultDTO;
import com.blooddonation.service.BloodRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/{id}/matches")
    public ResponseEntity<List<MatchResultDTO>> getMatchesForRequest(@PathVariable Long id) {
        List<MatchResultDTO> matches = bloodRequestService.findMatchesForRequest(id);
        return ResponseEntity.ok(matches);
    }

    @PostMapping("/{requestId}/matches/{matchId}/accept")
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<MatchResultDTO> acceptMatch(
            @PathVariable Long requestId,
            @PathVariable Long matchId,
            @AuthenticationPrincipal UserDetails userDetails,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String email = (!isAdmin && userDetails != null) ? userDetails.getUsername() : null;
        MatchResultDTO matchResult = bloodRequestService.acceptMatch(requestId, matchId, email);
        return ResponseEntity.ok(matchResult);
    }

    @PostMapping("/{requestId}/matches/{matchId}/decline")
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<MatchResultDTO> declineMatch(
            @PathVariable Long requestId,
            @PathVariable Long matchId,
            @AuthenticationPrincipal UserDetails userDetails,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String email = (!isAdmin && userDetails != null) ? userDetails.getUsername() : null;
        MatchResultDTO matchResult = bloodRequestService.declineMatch(requestId, matchId, email);
        return ResponseEntity.ok(matchResult);
    }

    @PostMapping("/{id}/fulfill")
    @PreAuthorize("hasAnyRole('HOSPITAL', 'BLOOD_BANK', 'ADMIN')")
    public ResponseEntity<BloodRequestResponseDTO> fulfillRequest(
            @PathVariable Long id,
            @RequestBody(required = false) com.blooddonation.dto.FulfillRequestDTO fulfillDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        BloodRequestResponseDTO fulfilled = bloodRequestService.fulfillRequest(id, fulfillDTO, email);
        return ResponseEntity.ok(fulfilled);
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
    public ResponseEntity<BloodRequestResponseDTO> cancelRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String email = userDetails != null ? userDetails.getUsername() : null;
        BloodRequestResponseDTO cancelled = bloodRequestService.cancelRequest(id, email, isAdmin);
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

