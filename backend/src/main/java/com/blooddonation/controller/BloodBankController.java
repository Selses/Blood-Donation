package com.blooddonation.controller;

import com.blooddonation.dto.BloodBankRequestDTO;
import com.blooddonation.dto.BloodBankResponseDTO;
import com.blooddonation.service.BloodBankService;
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
@RequestMapping("/api/blood-banks")
public class BloodBankController {

    private final BloodBankService bloodBankService;

    public BloodBankController(BloodBankService bloodBankService) {
        this.bloodBankService = bloodBankService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BLOOD_BANK', 'ADMIN')")
    public ResponseEntity<BloodBankResponseDTO> createBloodBank(
            @Valid @RequestBody BloodBankRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        BloodBankResponseDTO created = bloodBankService.createBloodBank(requestDTO, email);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BloodBankResponseDTO>> getAllBloodBanks() {
        List<BloodBankResponseDTO> list = bloodBankService.getAllBloodBanks();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BloodBankResponseDTO> getBloodBankById(@PathVariable Long id) {
        BloodBankResponseDTO bloodBank = bloodBankService.getBloodBankById(id);
        return ResponseEntity.ok(bloodBank);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BLOOD_BANK', 'ADMIN')")
    public ResponseEntity<BloodBankResponseDTO> updateBloodBank(
            @PathVariable Long id,
            @Valid @RequestBody BloodBankRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String email = userDetails != null ? userDetails.getUsername() : null;
        BloodBankResponseDTO updated = bloodBankService.updateBloodBank(id, requestDTO, email, isAdmin);
        return ResponseEntity.ok(updated);
    }
}
