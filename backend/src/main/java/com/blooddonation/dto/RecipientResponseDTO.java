package com.blooddonation.dto;

import com.blooddonation.model.Recipient;
import java.time.LocalDateTime;

public class RecipientResponseDTO {

    private Long id;
    private Long userId;
    private String name;
    private String bloodGroup;
    private String phone;
    private String city;
    private String hospitalName;
    private String emergencyContact;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RecipientResponseDTO() {
    }

    public RecipientResponseDTO(Long id, Long userId, String name, String bloodGroup, String phone, String city, String hospitalName, String emergencyContact, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.city = city;
        this.hospitalName = hospitalName;
        this.emergencyContact = emergencyContact;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static RecipientResponseDTO fromEntity(Recipient recipient) {
        if (recipient == null) return null;
        return new RecipientResponseDTO(
                recipient.getId(),
                recipient.getUser() != null ? recipient.getUser().getId() : null,
                recipient.getName(),
                recipient.getBloodGroup(),
                recipient.getPhone(),
                recipient.getCity(),
                recipient.getHospitalName(),
                recipient.getEmergencyContact(),
                recipient.getCreatedAt(),
                recipient.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
