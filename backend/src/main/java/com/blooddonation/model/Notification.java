package com.blooddonation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_user", columnList = "user_id"),
        @Index(name = "idx_notification_donor", columnList = "donor_id"),
        @Index(name = "idx_notification_read", columnList = "is_read")
})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id")
    private Donor donor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blood_request_id")
    private BloodRequest bloodRequest;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    private String type = "EMERGENCY_REQUEST";

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Notification() {
    }

    public Notification(User user, BloodRequest bloodRequest, String title, String message, String type) {
        this.user = user;
        this.bloodRequest = bloodRequest;
        this.title = title;
        this.message = message;
        this.type = type != null ? type : NotificationType.BLOOD_REQUEST.name();
        this.isRead = false;
    }

    public Notification(User user, Donor donor, BloodRequest bloodRequest, String title, String message, String type) {
        this.user = user;
        this.donor = donor;
        this.bloodRequest = bloodRequest;
        this.title = title;
        this.message = message;
        this.type = type != null ? type : NotificationType.EMERGENCY.name();
        this.isRead = false;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
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

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }

    public BloodRequest getBloodRequest() {
        return bloodRequest;
    }

    public void setBloodRequest(BloodRequest bloodRequest) {
        this.bloodRequest = bloodRequest;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getRelatedRequestId() {
        return bloodRequest != null ? bloodRequest.getId() : null;
    }
}
