package com.blooddonation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_banks", indexes = {
        @Index(name = "idx_blood_bank_city", columnList = "city")
})
public class BloodBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(nullable = false)
    private String phone;

    private String email;

    private String address;

    @Column(nullable = false)
    private String city;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public BloodBank() {
    }

    public BloodBank(String name, String phone, String email, String city, String address) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.city = city;
        this.address = address;
    }

    public BloodBank(User user, Hospital hospital, String name, String licenseNumber, String phone, String email, String address, String city) {
        this.user = user;
        this.hospital = hospital;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.city = city;
    }

    public BloodBank(Hospital hospital, String name, String licenseNumber, String phone, String email, String address, String city) {
        this.hospital = hospital;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.city = city;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
