package com.blooddonation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RecipientRequestDTO {

    @NotBlank(message = "Recipient name is required")
    private String name;

    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group. Allowed values: A+, A-, B+, B-, AB+, AB-, O+, O-")
    private String bloodGroup;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")
    private String phone;

    @NotBlank(message = "City is required")
    private String city;

    private String hospitalName;

    private String emergencyContact;

    public RecipientRequestDTO() {
    }

    public RecipientRequestDTO(String name, String bloodGroup, String phone, String city, String hospitalName, String emergencyContact) {
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.city = city;
        this.hospitalName = hospitalName;
        this.emergencyContact = emergencyContact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
}
