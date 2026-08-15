package com.blooddonation.dto;

import com.blooddonation.model.Notification;
import java.time.LocalDateTime;

public class NotificationResponseDTO {

    private Long id;
    private String title;
    private String message;
    private String type;
    private Long relatedRequestId;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationResponseDTO() {
    }

    public NotificationResponseDTO(Long id, String title, String message, String type, Long relatedRequestId, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.relatedRequestId = relatedRequestId;
        this.read = read;
        this.createdAt = createdAt;
    }

    public static NotificationResponseDTO fromEntity(Notification notification) {
        if (notification == null) {
            return null;
        }
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getRelatedRequestId(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getRelatedRequestId() {
        return relatedRequestId;
    }

    public void setRelatedRequestId(Long relatedRequestId) {
        this.relatedRequestId = relatedRequestId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
