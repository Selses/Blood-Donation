package com.blooddonation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_requests", indexes = {
        @Index(name = "idx_request_blood_group", columnList = "blood_group"),
        @Index(name = "idx_request_city", columnList = "city"),
        @Index(name = "idx_request_status", columnList = "status"),
        @Index(name = "idx_request_urgency", columnList = "urgency"),
        @Index(name = "idx_request_search", columnList = "blood_group, city, status")
})
public class BloodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private Recipient recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "blood_group", nullable = false)
    private String bloodGroup;

    @Column(name = "hospital_name", nullable = false)
    private String hospitalName;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String urgency = "HIGH";

    @Column(name = "required_units", nullable = false)
    private int requiredUnits = 1;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public BloodRequest() {
    }

    public BloodRequest(String recipientName, String bloodGroup, String hospitalName, String city, String urgency, String status) {
        this.recipientName = recipientName;
        this.bloodGroup = bloodGroup;
        this.hospitalName = hospitalName;
        this.city = city;
        this.urgency = urgency != null ? urgency.toUpperCase() : "HIGH";
        this.status = status != null ? status.toUpperCase() : "PENDING";
        this.requiredUnits = 1;
        this.requestDate = LocalDateTime.now();
    }

    public BloodRequest(Recipient recipient, Hospital hospital, String recipientName, String bloodGroup, String hospitalName, String city, String urgency, int requiredUnits, String status) {
        this.recipient = recipient;
        this.hospital = hospital;
        this.recipientName = recipientName;
        this.bloodGroup = bloodGroup;
        this.hospitalName = hospitalName;
        this.city = city;
        this.urgency = urgency != null ? urgency.toUpperCase() : "HIGH";
        this.requiredUnits = requiredUnits > 0 ? requiredUnits : 1;
        this.status = status != null ? status.toUpperCase() : "PENDING";
        this.requestDate = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.requestDate == null) {
            this.requestDate = LocalDateTime.now();
        }
        if (this.status == null || this.status.isBlank()) {
            this.status = "PENDING";
        }
        if (this.urgency == null || this.urgency.isBlank()) {
            this.urgency = "HIGH";
        }
        if (this.requiredUnits <= 0) {
            this.requiredUnits = 1;
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

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency != null ? urgency.toUpperCase() : null;
    }

    public int getRequiredUnits() {
        return requiredUnits;
    }

    public void setRequiredUnits(int requiredUnits) {
        this.requiredUnits = requiredUnits;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status.toUpperCase() : null;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
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
