package com.blooddonation.repository;

import com.blooddonation.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(Long userId, boolean isRead);

    long countByUserIdAndIsRead(Long userId, boolean isRead);

    boolean existsByUserIdAndBloodRequestIdAndType(Long userId, Long bloodRequestId, String type);

    List<Notification> findByUserId(Long userId);

    List<Notification> findByDonorId(Long donorId);

    List<Notification> findByUserIdAndIsRead(Long userId, boolean isRead);

    List<Notification> findByDonorIdAndIsRead(Long donorId, boolean isRead);
}

