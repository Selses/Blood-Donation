package com.blooddonation.dto;

public class FulfillRequestDTO {

    private String source = "DONOR";
    private Long inventoryId;

    public FulfillRequestDTO() {
    }

    public FulfillRequestDTO(String source) {
        this.source = source != null ? source.trim().toUpperCase() : "DONOR";
    }

    public FulfillRequestDTO(String source, Long inventoryId) {
        this.source = source != null ? source.trim().toUpperCase() : "DONOR";
        this.inventoryId = inventoryId;
    }

    public String getSource() {
        return source != null ? source.trim().toUpperCase() : "DONOR";
    }

    public void setSource(String source) {
        this.source = source != null ? source.trim().toUpperCase() : "DONOR";
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }
}
