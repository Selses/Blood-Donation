package com.blooddonation.dto;

import com.blooddonation.model.Hospital;
import java.time.LocalDateTime;

public class HospitalResponseDTO {

    private Long id;
    private String name;
    private String licenseNumber;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public HospitalResponseDTO() {
    }

    public HospitalResponseDTO(Long id, String name, String licenseNumber, String email, String phone, String address, String city, String state, boolean isVerified, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static HospitalResponseDTO fromEntity(Hospital hospital) {
        if (hospital == null) {
            return null;
        }
        return new HospitalResponseDTO(
                hospital.getId(),
                hospital.getName(),
                hospital.getLicenseNumber(),
                hospital.getEmail(),
                hospital.getPhone(),
                hospital.getAddress(),
                hospital.getCity(),
                hospital.getState(),
                hospital.isVerified(),
                hospital.getCreatedAt(),
                hospital.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
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
