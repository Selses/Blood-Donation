package com.blooddonation.repository;

import com.blooddonation.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    Optional<Hospital> findByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumber(String licenseNumber);

    List<Hospital> findByCityIgnoreCase(String city);

    List<Hospital> findByIsVerified(boolean isVerified);
}
