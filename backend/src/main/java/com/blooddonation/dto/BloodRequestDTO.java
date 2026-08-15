package com.blooddonation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class BloodRequestDTO {

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group. Allowed values: A+, A-, B+, B-, AB+, AB-, O+, O-")
    private String bloodGroup;

    @NotBlank(message = "Hospital name is required")
    private String hospitalName;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Urgency level is required")
    @Pattern(regexp = "^(?i)(LOW|MEDIUM|HIGH|CRITICAL)$", message = "Invalid urgency level. Allowed values: LOW, MEDIUM, HIGH, CRITICAL")
    private String urgency;

    @Min(value = 1, message = "Required units must be at least 1")
    private int requiredUnits = 1;

    @Pattern(regexp = "^(?i)(PENDING|MATCHED|ACCEPTED|FULFILLED|CANCELLED)$", message = "Invalid status. Allowed values: PENDING, MATCHED, ACCEPTED, FULFILLED, CANCELLED")
    private String status = "PENDING";

    public BloodRequestDTO() {
    }

    public BloodRequestDTO(String recipientName, String bloodGroup, String hospitalName, String city, String urgency, String status) {
        this.recipientName = recipientName;
        this.bloodGroup = bloodGroup;
        this.hospitalName = hospitalName;
        this.city = city;
        this.urgency = urgency;
        this.status = status;
        this.requiredUnits = 1;
    }

    public BloodRequestDTO(String recipientName, String bloodGroup, String hospitalName, String city, String urgency, int requiredUnits, String status) {
        this.recipientName = recipientName;
        this.bloodGroup = bloodGroup;
        this.hospitalName = hospitalName;
        this.city = city;
        this.urgency = urgency;
        this.requiredUnits = requiredUnits > 0 ? requiredUnits : 1;
        this.status = status;
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
}
