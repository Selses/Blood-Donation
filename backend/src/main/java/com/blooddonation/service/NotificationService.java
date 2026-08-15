package com.blooddonation.service;

import com.blooddonation.dto.NotificationResponseDTO;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.exception.ValidationException;
import com.blooddonation.model.BloodRequest;
import com.blooddonation.model.Donor;
import com.blooddonation.model.Notification;
import com.blooddonation.model.NotificationType;
import com.blooddonation.model.User;
import com.blooddonation.repository.NotificationRepository;
import com.blooddonation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public Notification createNotification(User user, String title, String message, String type, BloodRequest relatedRequest) {
        return createNotification(user, null, title, message, type, relatedRequest);
    }

    public Notification createNotification(User user, Donor donor, String title, String message, String type, BloodRequest relatedRequest) {
        if (user == null) {
            return null; // Cannot create notification without recipient user
        }

        String notificationType = (type != null && !type.isBlank()) ? type : NotificationType.BLOOD_REQUEST.name();

        // Duplicate prevention check: don't create duplicate notifications for same user, request, and notification type
        if (relatedRequest != null && relatedRequest.getId() != null) {
            boolean exists = notificationRepository.existsByUserIdAndBloodRequestIdAndType(
                    user.getId(),
                    relatedRequest.getId(),
                    notificationType
            );
            if (exists) {
                return null;
            }
        }

        Notification notification = new Notification(user, donor, relatedRequest, title, message, notificationType);
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsForUser(String userEmail) {
        User user = getUserByEmail(userEmail);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(NotificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnreadNotificationsForUser(String userEmail) {
        User user = getUserByEmail(userEmail);
        return notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(user.getId(), false)
                .stream()
                .map(NotificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCountForUser(String userEmail) {
        User user = getUserByEmail(userEmail);
        return notificationRepository.countByUserIdAndIsRead(user.getId(), false);
    }

    public NotificationResponseDTO markAsRead(Long notificationId, String userEmail) {
        User user = getUserByEmail(userEmail);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (notification.getUser() == null || !notification.getUser().getId().equals(user.getId())) {
            throw new ValidationException("Unauthorized: You can only mark your own notifications as read");
        }

        notification.setRead(true);
        Notification updated = notificationRepository.save(notification);
        return NotificationResponseDTO.fromEntity(updated);
    }

    public void markAllAsRead(String userEmail) {
        User user = getUserByEmail(userEmail);
        List<Notification> unreadList = notificationRepository.findByUserIdAndIsRead(user.getId(), false);
        for (Notification n : unreadList) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unreadList);
    }

    private User getUserByEmail(String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new ValidationException("User email is required to access notifications");
        }
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
    }
}
