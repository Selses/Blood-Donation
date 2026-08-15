package com.blooddonation.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "donation_history", indexes = {
        @Index(name = "idx_donation_donor", columnList = "donor_id"),
        @Index(name = "idx_donation_date", columnList = "donation_date"),
        @Index(name = "idx_donation_blood_group", columnList = "blood_group")
})
public class DonationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blood_bank_id")
    private BloodBank bloodBank;

    @Column(name = "blood_group", nullable = false)
    private String bloodGroup;

    @Column(name = "units_donated", nullable = false)
    private int unitsDonated = 1;

    @Column(name = "donation_date", nullable = false)
    private LocalDate donationDate;

    @Column(nullable = false)
    private String status = "COMPLETED";

    private String remarks;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DonationHistory() {
    }

    public DonationHistory(Donor donor, Hospital hospital, BloodBank bloodBank, String bloodGroup, int unitsDonated, LocalDate donationDate, String status, String remarks) {
        this.donor = donor;
        this.hospital = hospital;
        this.bloodBank = bloodBank;
        this.bloodGroup = bloodGroup;
        this.unitsDonated = unitsDonated > 0 ? unitsDonated : 1;
        this.donationDate = donationDate != null ? donationDate : LocalDate.now();
        this.status = status != null ? status : "COMPLETED";
        this.remarks = remarks;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.donationDate == null) {
            this.donationDate = LocalDate.now();
        }
        if (this.status == null) {
            this.status = "COMPLETED";
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

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public BloodBank getBloodBank() {
        return bloodBank;
    }

    public void setBloodBank(BloodBank bloodBank) {
        this.bloodBank = bloodBank;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public int getUnitsDonated() {
        return unitsDonated;
    }

    public void setUnitsDonated(int unitsDonated) {
        this.unitsDonated = unitsDonated;
    }

    public LocalDate getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
