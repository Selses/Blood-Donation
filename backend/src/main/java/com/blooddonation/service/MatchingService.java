package com.blooddonation.service;

import com.blooddonation.dto.MatchResultDTO;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.exception.ValidationException;
import com.blooddonation.model.*;
import com.blooddonation.repository.BloodMatchRepository;
import com.blooddonation.repository.BloodRequestRepository;
import com.blooddonation.repository.DonorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MatchingService {

    private final BloodRequestRepository bloodRequestRepository;
    private final DonorRepository donorRepository;
    private final BloodMatchRepository bloodMatchRepository;
    private final NotificationService notificationService;

    public MatchingService(BloodRequestRepository bloodRequestRepository,
                           DonorRepository donorRepository,
                           BloodMatchRepository bloodMatchRepository,
                           NotificationService notificationService) {
        this.bloodRequestRepository = bloodRequestRepository;
        this.donorRepository = donorRepository;
        this.bloodMatchRepository = bloodMatchRepository;
        this.notificationService = notificationService;
    }

    public List<MatchResultDTO> findMatchesForRequest(Long requestId) {
        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("BloodRequest", "id", requestId));

        if (!BloodGroup.isValid(request.getBloodGroup())) {
            throw new ValidationException("Invalid blood group in blood request: " + request.getBloodGroup());
        }

        String recipientGroup = request.getBloodGroup().trim().toUpperCase();
        List<String> compatibleDonorGroups = BloodCompatibilityHelper.getCompatibleDonorBloodGroups(recipientGroup);

        if (compatibleDonorGroups.isEmpty()) {
            return Collections.emptyList();
        }

        // Query active, available donors belonging to compatible blood groups
        List<Donor> availableDonors = donorRepository.findByBloodGroupInAndAvailableTrue(compatibleDonorGroups);

        if (availableDonors.isEmpty()) {
            return Collections.emptyList();
        }

        String requestCity = request.getCity() != null ? request.getCity().trim() : "";
        String urgency = request.getUrgency() != null ? request.getUrgency().toUpperCase() : "HIGH";

        List<MatchCalculation> matchCalculations = availableDonors.stream()
                .map(donor -> calculateMatch(donor, request, requestCity, urgency))
                .sorted(Comparator.comparingInt(MatchCalculation::getScore).reversed())
                .collect(Collectors.toList());

        List<MatchResultDTO> results = new ArrayList<>();
        for (MatchCalculation calc : matchCalculations) {
            Donor donor = calc.getDonor();
            Optional<BloodMatch> existingMatchOpt = bloodMatchRepository.findByBloodRequestIdAndDonorId(request.getId(), donor.getId());
            BloodMatch bloodMatch;
            if (existingMatchOpt.isPresent()) {
                bloodMatch = existingMatchOpt.get();
                bloodMatch.setMatchScore(calc.getScore());
                bloodMatch.setMatchReason(calc.getReason());
            } else {
                bloodMatch = new BloodMatch(
                        request,
                        donor,
                        calc.getScore(),
                        calc.getReason(),
                        MatchStatus.PENDING.name()
                );
            }
            bloodMatch = bloodMatchRepository.save(bloodMatch);

            // Notify donor if donor has associated User account
            if (donor.getUser() != null) {
                boolean isEmergency = "CRITICAL".equalsIgnoreCase(urgency) || "HIGH".equalsIgnoreCase(urgency);
                String notificationType = isEmergency ? NotificationType.EMERGENCY.name() : NotificationType.MATCH_FOUND.name();
                String title = isEmergency ? "CRITICAL Blood Request" : "Emergency Blood Request";
                String cityStr = (request.getCity() != null && !request.getCity().isBlank()) ? " in " + request.getCity().trim() : "";
                String message = isEmergency
                        ? "URGENT: A critical " + request.getBloodGroup() + " blood request matching your blood group is available" + cityStr + "."
                        : "A compatible " + request.getBloodGroup() + " blood request has been found" + cityStr + ".";

                notificationService.createNotification(
                        donor.getUser(),
                        donor,
                        title,
                        message,
                        notificationType,
                        request
                );
            }

            MatchResultDTO dto = new MatchResultDTO(
                    bloodMatch.getId(),
                    request.getId(),
                    donor.getId(),
                    donor.getName(),
                    donor.getBloodGroup(),
                    donor.getPhone(),
                    donor.getCity(),
                    donor.isAvailable(),
                    calc.getScore(),
                    calc.getReason(),
                    urgency,
                    bloodMatch.getStatus(),
                    donor.getLastDonationDate()
            );
            results.add(dto);
        }

        return results;
    }

    private MatchCalculation calculateMatch(Donor donor, BloodRequest request, String requestCity, String urgency) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        // 1. Blood Group Compatibility (+50)
        score += 50;
        reasons.add("Compatible blood group (" + donor.getBloodGroup() + ")");

        // 2. Available Donor (+20)
        if (donor.isAvailable()) {
            score += 20;
            reasons.add("available donor");
        }

        // 3. Location Priority (+20)
        boolean sameCity = donor.getCity() != null && donor.getCity().trim().equalsIgnoreCase(requestCity);
        if (sameCity) {
            score += 20;
            reasons.add("same city (" + donor.getCity().trim() + ")");
        } else {
            reasons.add("nearby city (" + (donor.getCity() != null ? donor.getCity().trim() : "Unknown") + ")");
        }

        // 4. Eligibility Check (+10)
        boolean eligible = isDonorEligible(donor);
        if (eligible) {
            score += 10;
            reasons.add("medically eligible");
        }

        String matchReason = String.join(", ", reasons);
        return new MatchCalculation(donor, score, matchReason);
    }

    private boolean isDonorEligible(Donor donor) {
        if (donor.getLastDonationDate() == null) {
            return true; // First time or eligible by default
        }
        // Standard safe donation interval: at least 90 days since last donation
        return !donor.getLastDonationDate().isAfter(LocalDate.now().minusDays(90));
    }

    private static class MatchCalculation {
        private final Donor donor;
        private final int score;
        private final String reason;

        public MatchCalculation(Donor donor, int score, String reason) {
            this.donor = donor;
            this.score = score;
            this.reason = reason;
        }

        public Donor getDonor() {
            return donor;
        }

        public int getScore() {
            return score;
        }

        public String getReason() {
            return reason;
        }
    }
}
