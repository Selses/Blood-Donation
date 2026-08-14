package com.blooddonation.dto;

import com.blooddonation.model.Donor;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DonorResponseDTO {

    private Long id;
    private Long userId;
    private String name;
    private String bloodGroup;
    private String city;
    private String phone;
    private boolean available;
    private LocalDate lastDonationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DonorResponseDTO() {
    }

    public DonorResponseDTO(Long id, Long userId, String name, String bloodGroup, String city, String phone, boolean available, LocalDate lastDonationDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.phone = phone;
        this.available = available;
        this.lastDonationDate = lastDonationDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static DonorResponseDTO fromEntity(Donor donor) {
        if (donor == null) return null;
        return new DonorResponseDTO(
                donor.getId(),
                donor.getUser() != null ? donor.getUser().getId() : null,
                donor.getName(),
                donor.getBloodGroup(),
                donor.getCity(),
                donor.getPhone(),
                donor.isAvailable(),
                donor.getLastDonationDate(),
                donor.getCreatedAt(),
                donor.getUpdatedAt()
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public LocalDate getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
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
