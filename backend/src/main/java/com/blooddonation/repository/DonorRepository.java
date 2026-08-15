package com.blooddonation.repository;

import com.blooddonation.model.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {

    Optional<Donor> findByUserId(Long userId);

    Optional<Donor> findByUserEmail(String email);

    List<Donor> findByBloodGroupIgnoreCase(String bloodGroup);

    List<Donor> findByCityIgnoreCase(String city);

    List<Donor> findByAvailable(boolean available);

    List<Donor> findByBloodGroupIgnoreCaseAndCityIgnoreCaseAndAvailable(String bloodGroup, String city, boolean available);

    List<Donor> findByBloodGroupInAndAvailableTrue(List<String> bloodGroups);

    List<Donor> findByBloodGroupInAndCityIgnoreCaseAndAvailableTrue(List<String> bloodGroups, String city);
}
