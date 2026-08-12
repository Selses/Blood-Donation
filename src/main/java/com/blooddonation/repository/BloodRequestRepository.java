package com.blooddonation.repository;

import com.blooddonation.model.BloodRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {

    List<BloodRequest> findByRecipientId(Long recipientId);

    List<BloodRequest> findByRecipientUserEmail(String email);

    List<BloodRequest> findByStatusIgnoreCase(String status);

    List<BloodRequest> findByBloodGroupIgnoreCase(String bloodGroup);

    List<BloodRequest> findByBloodGroupInAndStatusIgnoreCase(List<String> bloodGroups, String status);

    List<BloodRequest> findByUrgencyIgnoreCase(String urgency);

    List<BloodRequest> findByCityIgnoreCase(String city);
}
