package com.blooddonation.dto;

import com.blooddonation.model.DonationHistory;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DonationHistoryResponseDTO {

    private Long id;
    private Long donorId;
    private String donorName;
    private Long hospitalId;
    private String hospitalName;
    private Long bloodBankId;
    private String bloodBankName;
    private String bloodGroup;
    private int unitsDonated;
    private LocalDate donationDate;
    private String status;
    private String remarks;
    private LocalDateTime createdAt;

    public DonationHistoryResponseDTO() {
    }

    public DonationHistoryResponseDTO(Long id, Long donorId, String donorName, Long hospitalId, String hospitalName, Long bloodBankId, String bloodBankName, String bloodGroup, int unitsDonated, LocalDate donationDate, String status, String remarks, LocalDateTime createdAt) {
        this.id = id;
        this.donorId = donorId;
        this.donorName = donorName;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.bloodBankId = bloodBankId;
        this.bloodBankName = bloodBankName;
        this.bloodGroup = bloodGroup;
        this.unitsDonated = unitsDonated;
        this.donationDate = donationDate;
        this.status = status;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }

    public static DonationHistoryResponseDTO fromEntity(DonationHistory history) {
        if (history == null) return null;
        return new DonationHistoryResponseDTO(
                history.getId(),
                history.getDonor() != null ? history.getDonor().getId() : null,
                history.getDonor() != null ? history.getDonor().getName() : null,
                history.getHospital() != null ? history.getHospital().getId() : null,
                history.getHospital() != null ? history.getHospital().getName() : null,
                history.getBloodBank() != null ? history.getBloodBank().getId() : null,
                history.getBloodBank() != null ? history.getBloodBank().getName() : null,
                history.getBloodGroup(),
                history.getUnitsDonated(),
                history.getDonationDate(),
                history.getStatus(),
                history.getRemarks(),
                history.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
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
}
