package com.blooddonation.dto;

import com.blooddonation.model.BloodRequest;
import java.time.LocalDateTime;

public class BloodRequestResponseDTO {

    private Long id;
    private String recipientName;
    private String bloodGroup;
    private String hospitalName;
    private String city;
    private String urgency;
    private int requiredUnits;
    private String status;
    private LocalDateTime requestDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BloodRequestResponseDTO() {
    }

    public BloodRequestResponseDTO(Long id, String recipientName, String bloodGroup, String hospitalName, String city, String urgency, int requiredUnits, String status, LocalDateTime requestDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.recipientName = recipientName;
        this.bloodGroup = bloodGroup;
        this.hospitalName = hospitalName;
        this.city = city;
        this.urgency = urgency;
        this.requiredUnits = requiredUnits;
        this.status = status;
        this.requestDate = requestDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static BloodRequestResponseDTO fromEntity(BloodRequest request) {
        if (request == null) return null;
        return new BloodRequestResponseDTO(
                request.getId(),
                request.getRecipientName(),
                request.getBloodGroup(),
                request.getHospitalName(),
                request.getCity(),
                request.getUrgency(),
                request.getRequiredUnits(),
                request.getStatus(),
                request.getRequestDate(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        this.urgency = urgency;
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
        this.status = status;
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
