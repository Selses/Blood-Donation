package com.blooddonation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class HospitalRequestDTO {

    @NotBlank(message = "Hospital name is required")
    private String name;

    private String licenseNumber;

    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "Phone must be a valid number between 7 and 15 characters")
    private String phone;

    private String address;

    @NotBlank(message = "City is required")
    private String city;

    private String state;

    public HospitalRequestDTO() {
    }

    public HospitalRequestDTO(String name, String licenseNumber, String email, String phone, String address, String city, String state) {
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
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
}
