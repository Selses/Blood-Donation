package com.blooddonation.repository;

import com.blooddonation.model.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipientRepository extends JpaRepository<Recipient, Long> {

    Optional<Recipient> findByUserId(Long userId);

    Optional<Recipient> findByUserEmail(String email);

    List<Recipient> findByBloodGroupIgnoreCase(String bloodGroup);

    List<Recipient> findByCityIgnoreCase(String city);
}
