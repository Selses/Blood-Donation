package com.blooddonation.dto;

import com.blooddonation.model.BloodInventory;
import java.time.LocalDateTime;

public class BloodInventoryResponseDTO {

    private Long id;
    private String bloodGroup;
    private int availableUnits;
    private Long hospitalId;
    private String hospitalName;
    private Long bloodBankId;
    private String bloodBankName;
    private LocalDateTime lastUpdated;
    private LocalDateTime updatedAt;

    public BloodInventoryResponseDTO() {
    }

    public BloodInventoryResponseDTO(Long id, String bloodGroup, int availableUnits, Long hospitalId, String hospitalName, Long bloodBankId, String bloodBankName, LocalDateTime lastUpdated, LocalDateTime updatedAt) {
        this.id = id;
        this.bloodGroup = bloodGroup;
        this.availableUnits = availableUnits;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.bloodBankId = bloodBankId;
        this.bloodBankName = bloodBankName;
        this.lastUpdated = lastUpdated;
        this.updatedAt = updatedAt;
    }

    public static BloodInventoryResponseDTO fromEntity(BloodInventory inventory) {
        if (inventory == null) {
            return null;
        }
        Long hospId = inventory.getHospital() != null ? inventory.getHospital().getId() : null;
        String hospName = inventory.getHospital() != null ? inventory.getHospital().getName() : null;
        Long bbId = inventory.getBloodBank() != null ? inventory.getBloodBank().getId() : null;
        String bbName = inventory.getBloodBank() != null ? inventory.getBloodBank().getName() : null;

        return new BloodInventoryResponseDTO(
                inventory.getId(),
                inventory.getBloodGroup(),
                inventory.getAvailableUnits(),
                hospId,
                hospName,
                bbId,
                bbName,
                inventory.getLastUpdated(),
                inventory.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public Long getBloodBankId() {
        return bloodBankId;
    }

    public void setBloodBankId(Long bloodBankId) {
        this.bloodBankId = bloodBankId;
    }

    public String getBloodBankName() {
        return bloodBankName;
    }

    public void setBloodBankName(String bloodBankName) {
        this.bloodBankName = bloodBankName;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
