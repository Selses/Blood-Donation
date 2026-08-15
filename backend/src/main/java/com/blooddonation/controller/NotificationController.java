package com.blooddonation.controller;

import com.blooddonation.dto.NotificationResponseDTO;
import com.blooddonation.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsForUser(userDetails.getUsername());
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<NotificationResponseDTO> unread = notificationService.getUnreadNotificationsForUser(userDetails.getUsername());
        return ResponseEntity.ok(unread);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(Map.of("count", 0L));
        }
        long count = notificationService.getUnreadCountForUser(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        NotificationResponseDTO updated = notificationService.markAsRead(id, email);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        notificationService.markAllAsRead(email);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }
}
