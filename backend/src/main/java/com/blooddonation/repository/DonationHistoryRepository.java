package com.blooddonation.repository;

import com.blooddonation.model.DonationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationHistoryRepository extends JpaRepository<DonationHistory, Long> {

    List<DonationHistory> findByDonorId(Long donorId);

    List<DonationHistory> findByHospitalId(Long hospitalId);

    List<DonationHistory> findByBloodBankId(Long bloodBankId);
}
