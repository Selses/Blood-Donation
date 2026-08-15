package com.blooddonation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InventoryAdjustmentDTO {

    @NotNull(message = "Units is required")
    @Min(value = 1, message = "Units must be at least 1")
    private Integer units;

    public InventoryAdjustmentDTO() {
    }

    public InventoryAdjustmentDTO(Integer units) {
        this.units = units;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }
}
