package com.blooddonation.repository;

import com.blooddonation.model.BloodInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodInventoryRepository extends JpaRepository<BloodInventory, Long> {

    List<BloodInventory> findByHospitalId(Long hospitalId);

    List<BloodInventory> findByBloodBankId(Long bloodBankId);

    List<BloodInventory> findByBloodGroupIgnoreCase(String bloodGroup);

    Optional<BloodInventory> findByHospitalIdAndBloodGroupIgnoreCase(Long hospitalId, String bloodGroup);

    Optional<BloodInventory> findByBloodBankIdAndBloodGroupIgnoreCase(Long bloodBankId, String bloodGroup);
}
