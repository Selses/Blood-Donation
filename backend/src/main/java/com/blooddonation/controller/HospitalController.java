package com.blooddonation.controller;

import com.blooddonation.dto.HospitalRequestDTO;
import com.blooddonation.dto.HospitalResponseDTO;
import com.blooddonation.service.HospitalService;
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
@RequestMapping("/api/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HOSPITAL', 'ADMIN')")
    public ResponseEntity<HospitalResponseDTO> createHospital(
            @Valid @RequestBody HospitalRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        HospitalResponseDTO created = hospitalService.createHospital(requestDTO, email);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HospitalResponseDTO>> getAllHospitals() {
        List<HospitalResponseDTO> list = hospitalService.getAllHospitals();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HospitalResponseDTO> getHospitalById(@PathVariable Long id) {
        HospitalResponseDTO hospital = hospitalService.getHospitalById(id);
        return ResponseEntity.ok(hospital);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOSPITAL', 'ADMIN')")
    public ResponseEntity<HospitalResponseDTO> updateHospital(
            @PathVariable Long id,
            @Valid @RequestBody HospitalRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String email = userDetails != null ? userDetails.getUsername() : null;
        HospitalResponseDTO updated = hospitalService.updateHospital(id, requestDTO, email, isAdmin);
        return ResponseEntity.ok(updated);
    }
}
