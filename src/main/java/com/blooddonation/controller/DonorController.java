package com.blooddonation.controller;

import com.blooddonation.dto.BloodRequestResponseDTO;
import com.blooddonation.dto.DonationHistoryDTO;
import com.blooddonation.dto.DonationHistoryResponseDTO;
import com.blooddonation.dto.DonorRequestDTO;
import com.blooddonation.dto.DonorResponseDTO;
import com.blooddonation.service.DonationHistoryService;
import com.blooddonation.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donors")
public class DonorController {

    private final DonorService donorService;
    private final DonationHistoryService donationHistoryService;

    public DonorController(DonorService donorService, DonationHistoryService donationHistoryService) {
        this.donorService = donorService;
        this.donationHistoryService = donationHistoryService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<DonorResponseDTO> createDonor(
            @Valid @RequestBody DonorRequestDTO donorRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        DonorResponseDTO createdDonor = donorService.createDonor(donorRequestDTO, email);
        return new ResponseEntity<>(createdDonor, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<DonorResponseDTO> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        DonorResponseDTO donor = donorService.getDonorByUserEmail(userDetails.getUsername());
        return ResponseEntity.ok(donor);
    }

    @GetMapping
    public ResponseEntity<List<DonorResponseDTO>> getAllDonors() {
        List<DonorResponseDTO> donors = donorService.getAllDonors();
        return ResponseEntity.ok(donors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonorResponseDTO> getDonorById(@PathVariable Long id) {
        DonorResponseDTO donor = donorService.getDonorById(id);
        return ResponseEntity.ok(donor);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<DonorResponseDTO> updateDonor(
            @PathVariable Long id,
            @Valid @RequestBody DonorRequestDTO donorRequestDTO) {
        DonorResponseDTO updatedDonor = donorService.updateDonor(id, donorRequestDTO);
        return ResponseEntity.ok(updatedDonor);
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<DonorResponseDTO> updateAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {
        DonorResponseDTO updatedDonor = donorService.updateAvailability(id, available);
        return ResponseEntity.ok(updatedDonor);
    }

    @PatchMapping("/me/availability")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<DonorResponseDTO> updateMyAvailability(
            @RequestParam boolean available,
            @AuthenticationPrincipal UserDetails userDetails) {
        DonorResponseDTO myProfile = donorService.getDonorByUserEmail(userDetails.getUsername());
        DonorResponseDTO updated = donorService.updateAvailability(myProfile.getId(), available);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDonor(@PathVariable Long id) {
        donorService.deleteDonor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<DonorResponseDTO>> searchDonors(
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean available) {
        List<DonorResponseDTO> results = donorService.searchDonors(bloodGroup, city, available);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<DonationHistoryResponseDTO>> getDonationHistory(@PathVariable Long id) {
        List<DonationHistoryResponseDTO> history = donationHistoryService.getDonationHistoryForDonor(id);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/me/history")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<List<DonationHistoryResponseDTO>> getMyDonationHistory(@AuthenticationPrincipal UserDetails userDetails) {
        DonorResponseDTO myProfile = donorService.getDonorByUserEmail(userDetails.getUsername());
        List<DonationHistoryResponseDTO> history = donationHistoryService.getDonationHistoryForDonor(myProfile.getId());
        return ResponseEntity.ok(history);
    }

    @PostMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('HOSPITAL', 'BLOOD_BANK', 'ADMIN')")
    public ResponseEntity<DonationHistoryResponseDTO> recordDonation(
            @PathVariable Long id,
            @Valid @RequestBody DonationHistoryDTO historyDTO) {
        historyDTO.setDonorId(id);
        DonationHistoryResponseDTO response = donationHistoryService.recordDonation(historyDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/eligible-requests")
    public ResponseEntity<List<BloodRequestResponseDTO>> getEligibleRequests(@PathVariable Long id) {
        List<BloodRequestResponseDTO> requests = donorService.getEligibleRequestsForDonor(id);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/me/eligible-requests")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<List<BloodRequestResponseDTO>> getMyEligibleRequests(@AuthenticationPrincipal UserDetails userDetails) {
        DonorResponseDTO myProfile = donorService.getDonorByUserEmail(userDetails.getUsername());
        List<BloodRequestResponseDTO> requests = donorService.getEligibleRequestsForDonor(myProfile.getId());
        return ResponseEntity.ok(requests);
    }
}
