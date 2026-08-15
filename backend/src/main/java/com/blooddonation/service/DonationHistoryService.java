package com.blooddonation.service;

import com.blooddonation.dto.DonationHistoryDTO;
import com.blooddonation.dto.DonationHistoryResponseDTO;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.exception.ValidationException;
import com.blooddonation.model.BloodBank;
import com.blooddonation.model.BloodGroup;
import com.blooddonation.model.DonationHistory;
import com.blooddonation.model.Donor;
import com.blooddonation.model.Hospital;
import com.blooddonation.repository.BloodBankRepository;
import com.blooddonation.repository.DonationHistoryRepository;
import com.blooddonation.repository.DonorRepository;
import com.blooddonation.repository.HospitalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DonationHistoryService {

    private final DonationHistoryRepository donationHistoryRepository;
    private final DonorRepository donorRepository;
    private final HospitalRepository hospitalRepository;
    private final BloodBankRepository bloodBankRepository;

    public DonationHistoryService(DonationHistoryRepository donationHistoryRepository,
                                  DonorRepository donorRepository,
                                  HospitalRepository hospitalRepository,
                                  BloodBankRepository bloodBankRepository) {
        this.donationHistoryRepository = donationHistoryRepository;
        this.donorRepository = donorRepository;
        this.hospitalRepository = hospitalRepository;
        this.bloodBankRepository = bloodBankRepository;
    }

    public DonationHistoryResponseDTO recordDonation(DonationHistoryDTO dto) {
        if (dto == null) {
            throw new ValidationException("Donation history body cannot be null");
        }
        if (!BloodGroup.isValid(dto.getBloodGroup())) {
            throw new ValidationException("Invalid blood group: " + dto.getBloodGroup());
        }

        Donor donor = donorRepository.findById(dto.getDonorId())
                .orElseThrow(() -> new ResourceNotFoundException("Donor", "id", dto.getDonorId()));

        Hospital hospital = null;
        if (dto.getHospitalId() != null) {
            hospital = hospitalRepository.findById(dto.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital", "id", dto.getHospitalId()));
        }

        BloodBank bloodBank = null;
        if (dto.getBloodBankId() != null) {
            bloodBank = bloodBankRepository.findById(dto.getBloodBankId())
                    .orElseThrow(() -> new ResourceNotFoundException("BloodBank", "id", dto.getBloodBankId()));
        }

        LocalDate donationDate = dto.getDonationDate() != null ? dto.getDonationDate() : LocalDate.now();

        DonationHistory donation = new DonationHistory(
                donor,
                hospital,
                bloodBank,
                dto.getBloodGroup().trim().toUpperCase(),
                dto.getUnitsDonated() > 0 ? dto.getUnitsDonated() : 1,
                donationDate,
                dto.getStatus() != null ? dto.getStatus() : "COMPLETED",
                dto.getRemarks()
        );

        DonationHistory saved = donationHistoryRepository.save(donation);

        // Automatically update donor's lastDonationDate
        donor.setLastDonationDate(donationDate);
        donorRepository.save(donor);

        return DonationHistoryResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<DonationHistoryResponseDTO> getDonationHistoryForDonor(Long donorId) {
        if (!donorRepository.existsById(donorId)) {
            throw new ResourceNotFoundException("Donor", "id", donorId);
        }
        return donationHistoryRepository.findByDonorId(donorId).stream()
                .map(DonationHistoryResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DonationHistoryResponseDTO getDonationHistoryById(Long id) {
        DonationHistory history = donationHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DonationHistory", "id", id));
        return DonationHistoryResponseDTO.fromEntity(history);
    }
}
