package com.blooddonation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_inventory", indexes = {
        @Index(name = "idx_inventory_blood_group", columnList = "blood_group"),
        @Index(name = "idx_inventory_hospital", columnList = "hospital_id"),
        @Index(name = "idx_inventory_blood_bank", columnList = "blood_bank_id")
})
public class BloodInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blood_bank_id")
    private BloodBank bloodBank;

    @Column(name = "blood_group", nullable = false)
    private String bloodGroup;

    @Column(name = "available_units", nullable = false)
    private int availableUnits = 0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public BloodInventory() {
    }

    public BloodInventory(Hospital hospital, BloodBank bloodBank, String bloodGroup, int availableUnits) {
        this.hospital = hospital;
        this.bloodBank = bloodBank;
        this.bloodGroup = bloodGroup;
        this.availableUnits = Math.max(0, availableUnits);
        this.lastUpdated = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getAvailableUnits() {
        return availableUnits;
    }

    public void setAvailableUnits(int availableUnits) {
        this.availableUnits = Math.max(0, availableUnits);
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
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
