package com.blooddonation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hospitals", indexes = {
        @Index(name = "idx_hospital_city", columnList = "city"),
        @Index(name = "idx_hospital_license", columnList = "license_number", unique = true)
})
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    private String email;

    @Column(nullable = false)
    private String phone;

    private String address;

    @Column(nullable = false)
    private String city;

    private String state;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Hospital() {
    }

    public Hospital(String name, String phone, String email, String city, String address) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.city = city;
        this.address = address;
        this.licenseNumber = "LIC-" + System.currentTimeMillis();
        this.isVerified = false;
    }

    public Hospital(User user, String name, String licenseNumber, String email, String phone, String address, String city, String state, boolean isVerified) {
        this.user = user;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.isVerified = isVerified;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
