package com.blooddonation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class BloodInventoryRequestDTO {

    @NotBlank(message = "Blood group is required")
    private String bloodGroup;

    @Min(value = 0, message = "Available units cannot be negative")
    private int availableUnits = 0;

    private Long hospitalId;
    private Long bloodBankId;

    public BloodInventoryRequestDTO() {
    }

    public BloodInventoryRequestDTO(String bloodGroup, int availableUnits) {
        this.bloodGroup = bloodGroup;
        this.availableUnits = availableUnits;
    }

    public BloodInventoryRequestDTO(String bloodGroup, int availableUnits, Long hospitalId, Long bloodBankId) {
        this.bloodGroup = bloodGroup;
        this.availableUnits = availableUnits;
        this.hospitalId = hospitalId;
        this.bloodBankId = bloodBankId;
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
        this.availableUnits = availableUnits;
    }

    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Long getBloodBankId() {
        return bloodBankId;
    }

    public void setBloodBankId(Long bloodBankId) {
        this.bloodBankId = bloodBankId;
    }
}
