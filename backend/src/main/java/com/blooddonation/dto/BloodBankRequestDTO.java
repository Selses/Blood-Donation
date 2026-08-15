package com.blooddonation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class BloodBankRequestDTO {

    private Long hospitalId;

    @NotBlank(message = "Blood bank name is required")
    private String name;

    private String licenseNumber;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "Phone must be a valid number between 7 and 15 characters")
    private String phone;

    private String email;

    private String address;

    @NotBlank(message = "City is required")
    private String city;

    public BloodBankRequestDTO() {
    }

    public BloodBankRequestDTO(String name, String phone, String email, String city, String address) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.city = city;
        this.address = address;
    }

    public BloodBankRequestDTO(Long hospitalId, String name, String licenseNumber, String phone, String email, String address, String city) {
        this.hospitalId = hospitalId;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.city = city;
    }

    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
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
}
