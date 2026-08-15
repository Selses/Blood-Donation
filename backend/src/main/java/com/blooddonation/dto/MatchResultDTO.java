package com.blooddonation.dto;

import com.blooddonation.model.BloodMatch;
import java.time.LocalDate;

public class MatchResultDTO {

    private Long matchId;
    private Long requestId;
    private Long donorId;
    private String donorName;
    private String bloodGroup;
    private String phone;
    private String city;
    private boolean available;
    private int matchScore;
    private String matchReason;
    private String urgency;
    private String status = "PENDING";
    private LocalDate lastDonationDate;

    public MatchResultDTO() {
    }

    public MatchResultDTO(Long requestId, Long donorId, String donorName, String bloodGroup, String phone, String city, boolean available, int matchScore, String matchReason, String urgency, LocalDate lastDonationDate) {
        this.requestId = requestId;
        this.donorId = donorId;
        this.donorName = donorName;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.city = city;
        this.available = available;
        this.matchScore = matchScore;
        this.matchReason = matchReason;
        this.urgency = urgency;
        this.status = "PENDING";
        this.lastDonationDate = lastDonationDate;
    }

    public MatchResultDTO(Long matchId, Long requestId, Long donorId, String donorName, String bloodGroup, String phone, String city, boolean available, int matchScore, String matchReason, String urgency, String status, LocalDate lastDonationDate) {
        this.matchId = matchId;
        this.requestId = requestId;
        this.donorId = donorId;
        this.donorName = donorName;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.city = city;
        this.available = available;
        this.matchScore = matchScore;
        this.matchReason = matchReason;
        this.urgency = urgency;
        this.status = status != null ? status : "PENDING";
        this.lastDonationDate = lastDonationDate;
    }

    public static MatchResultDTO fromEntity(BloodMatch match) {
        if (match == null) return null;
        return new MatchResultDTO(
                match.getId(),
                match.getBloodRequest() != null ? match.getBloodRequest().getId() : null,
                match.getDonor() != null ? match.getDonor().getId() : null,
                match.getDonor() != null ? match.getDonor().getName() : null,
                match.getDonor() != null ? match.getDonor().getBloodGroup() : null,
                match.getDonor() != null ? match.getDonor().getPhone() : null,
                match.getDonor() != null ? match.getDonor().getCity() : null,
                match.getDonor() != null && match.getDonor().isAvailable(),
                match.getMatchScore(),
                match.getMatchReason(),
                match.getBloodRequest() != null ? match.getBloodRequest().getUrgency() : null,
                match.getStatus(),
                match.getDonor() != null ? match.getDonor().getLastDonationDate() : null
        );
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public void setMatchReason(String matchReason) {
        this.matchReason = matchReason;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }
}
