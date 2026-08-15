package com.blooddonation.repository;

import com.blooddonation.model.BloodMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodMatchRepository extends JpaRepository<BloodMatch, Long> {

    List<BloodMatch> findByBloodRequestId(Long bloodRequestId);

    List<BloodMatch> findByBloodRequestIdOrderByMatchScoreDesc(Long bloodRequestId);

    List<BloodMatch> findByDonorId(Long donorId);

    Optional<BloodMatch> findByBloodRequestIdAndDonorId(Long bloodRequestId, Long donorId);

    List<BloodMatch> findByBloodRequestIdAndStatusIgnoreCase(Long bloodRequestId, String status);

    long countByBloodRequestIdAndStatusIgnoreCase(Long bloodRequestId, String status);

    boolean existsByBloodRequestIdAndDonorId(Long bloodRequestId, Long donorId);
}
