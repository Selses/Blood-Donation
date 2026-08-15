package com.blooddonation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_matches", indexes = {
        @Index(name = "idx_match_request_id", columnList = "blood_request_id"),
        @Index(name = "idx_match_donor_id", columnList = "donor_id"),
        @Index(name = "idx_match_status", columnList = "status"),
        @Index(name = "idx_match_req_donor", columnList = "blood_request_id, donor_id", unique = true)
})
public class BloodMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blood_request_id", nullable = false)
    private BloodRequest bloodRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @Column(name = "match_score", nullable = false)
    private int matchScore;

    @Column(name = "match_reason")
    private String matchReason;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public BloodMatch() {
    }

    public BloodMatch(BloodRequest bloodRequest, Donor donor, int matchScore, String matchReason, String status) {
        this.bloodRequest = bloodRequest;
        this.donor = donor;
        this.matchScore = matchScore;
        this.matchReason = matchReason;
        this.status = status != null ? status.toUpperCase() : "PENDING";
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null || this.status.isBlank()) {
            this.status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BloodRequest getBloodRequest() {
        return bloodRequest;
    }

    public void setBloodRequest(BloodRequest bloodRequest) {
        this.bloodRequest = bloodRequest;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status.toUpperCase() : null;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
