package com.blooddonation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class DonationHistoryDTO {

    @NotNull(message = "Donor ID is required")
    private Long donorId;

    private Long hospitalId;

    private Long bloodBankId;

    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group. Allowed values: A+, A-, B+, B-, AB+, AB-, O+, O-")
    private String bloodGroup;

    @Min(value = 1, message = "Units donated must be at least 1")
    private int unitsDonated = 1;

    private LocalDate donationDate;

    private String status = "COMPLETED";

    private String remarks;

    public DonationHistoryDTO() {
    }

    public DonationHistoryDTO(Long donorId, Long hospitalId, Long bloodBankId, String bloodGroup, int unitsDonated, LocalDate donationDate, String status, String remarks) {
        this.donorId = donorId;
        this.hospitalId = hospitalId;
        this.bloodBankId = bloodBankId;
        this.bloodGroup = bloodGroup;
        this.unitsDonated = unitsDonated;
        this.donationDate = donationDate;
        this.status = status;
        this.remarks = remarks;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
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
}
