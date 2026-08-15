package com.blooddonation.dto;

import com.blooddonation.model.BloodBank;
import java.time.LocalDateTime;

public class BloodBankResponseDTO {

    private Long id;
    private Long hospitalId;
    private String hospitalName;
    private String name;
    private String licenseNumber;
    private String phone;
    private String email;
    private String address;
    private String city;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BloodBankResponseDTO() {
    }

    public BloodBankResponseDTO(Long id, Long hospitalId, String hospitalName, String name, String licenseNumber, String phone, String email, String address, String city, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.city = city;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static BloodBankResponseDTO fromEntity(BloodBank bloodBank) {
        if (bloodBank == null) {
            return null;
        }
        Long hospId = bloodBank.getHospital() != null ? bloodBank.getHospital().getId() : null;
        String hospName = bloodBank.getHospital() != null ? bloodBank.getHospital().getName() : null;
        return new BloodBankResponseDTO(
                bloodBank.getId(),
                hospId,
                hospName,
                bloodBank.getName(),
                bloodBank.getLicenseNumber(),
                bloodBank.getPhone(),
                bloodBank.getEmail(),
                bloodBank.getAddress(),
                bloodBank.getCity(),
                bloodBank.getCreatedAt(),
                bloodBank.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
