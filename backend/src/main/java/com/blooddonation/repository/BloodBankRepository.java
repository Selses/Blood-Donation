package com.blooddonation.repository;

import com.blooddonation.model.BloodBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodBankRepository extends JpaRepository<BloodBank, Long> {

    Optional<BloodBank> findByUserId(Long userId);

    Optional<BloodBank> findByUserEmail(String email);

    Optional<BloodBank> findByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumber(String licenseNumber);

    List<BloodBank> findByCityIgnoreCase(String city);

    List<BloodBank> findByHospitalId(Long hospitalId);
}

