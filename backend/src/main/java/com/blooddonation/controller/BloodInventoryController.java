package com.blooddonation.controller;

import com.blooddonation.dto.BloodInventoryRequestDTO;
import com.blooddonation.dto.BloodInventoryResponseDTO;
import com.blooddonation.dto.InventoryAdjustmentDTO;
import com.blooddonation.service.BloodInventoryService;
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
@RequestMapping("/api/inventory")
public class BloodInventoryController {

    private final BloodInventoryService bloodInventoryService;

    public BloodInventoryController(BloodInventoryService bloodInventoryService) {
        this.bloodInventoryService = bloodInventoryService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HOSPITAL', 'BLOOD_BANK', 'ADMIN')")
    public ResponseEntity<BloodInventoryResponseDTO> createOrUpdateInventory(
            @Valid @RequestBody BloodInventoryRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String email = userDetails != null ? userDetails.getUsername() : null;
        BloodInventoryResponseDTO response = bloodInventoryService.createOrUpdateInventory(requestDTO, email, isAdmin);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HOSPITAL', 'BLOOD_BANK', 'ADMIN')")
    public ResponseEntity<List<BloodInventoryResponseDTO>> getMyInventory(
            @AuthenticationPrincipal UserDetails userDetails,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String email = userDetails != null ? userDetails.getUsername() : null;
        List<BloodInventoryResponseDTO> list = bloodInventoryService.getInventoryForUser(email, isAdmin);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/blood-group/{bloodGroup}")
    public ResponseEntity<List<BloodInventoryResponseDTO>> searchByBloodGroup(@PathVariable String bloodGroup) {
        List<BloodInventoryResponseDTO> list = bloodInventoryService.searchByBloodGroup(bloodGroup);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOSPITAL', 'BLOOD_BANK', 'ADMIN')")
    public ResponseEntity<BloodInventoryResponseDTO> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody BloodInventoryRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String email = userDetails != null ? userDetails.getUsername() : null;
        BloodInventoryResponseDTO updated = bloodInventoryService.updateInventory(id, requestDTO, email, isAdmin);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/add")
    @PreAuthorize("hasAnyRole('HOSPITAL', 'BLOOD_BANK', 'ADMIN')")
    public ResponseEntity<BloodInventoryResponseDTO> addUnits(
            @PathVariable Long id,
            @Valid @RequestBody InventoryAdjustmentDTO adjustmentDTO,
            @AuthenticationPrincipal UserDetails userDetails,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String email = userDetails != null ? userDetails.getUsername() : null;
        BloodInventoryResponseDTO updated = bloodInventoryService.addUnits(id, adjustmentDTO.getUnits(), email, isAdmin);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/remove")
    @PreAuthorize("hasAnyRole('HOSPITAL', 'BLOOD_BANK', 'ADMIN')")
    public ResponseEntity<BloodInventoryResponseDTO> removeUnits(
            @PathVariable Long id,
            @Valid @RequestBody InventoryAdjustmentDTO adjustmentDTO,
            @AuthenticationPrincipal UserDetails userDetails,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String email = userDetails != null ? userDetails.getUsername() : null;
        BloodInventoryResponseDTO updated = bloodInventoryService.removeUnits(id, adjustmentDTO.getUnits(), email, isAdmin);
        return ResponseEntity.ok(updated);
    }
}
