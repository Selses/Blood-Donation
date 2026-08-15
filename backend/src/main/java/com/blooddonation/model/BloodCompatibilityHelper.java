package com.blooddonation.model;

import java.util.*;

public class BloodCompatibilityHelper {

    // Recipient perspective: given recipient's blood group, who can donate to them?
    private static final Map<String, List<String>> RECIPIENT_TO_DONOR_MAP = new HashMap<>();

    // Donor perspective: given donor's blood group, who can receive their donation?
    private static final Map<String, List<String>> DONOR_TO_RECIPIENT_MAP = new HashMap<>();

    static {
        // Recipient compatibility matrix
        RECIPIENT_TO_DONOR_MAP.put("A+", Arrays.asList("A+", "A-", "O+", "O-"));
        RECIPIENT_TO_DONOR_MAP.put("A-", Arrays.asList("A-", "O-"));
        RECIPIENT_TO_DONOR_MAP.put("B+", Arrays.asList("B+", "B-", "O+", "O-"));
        RECIPIENT_TO_DONOR_MAP.put("B-", Arrays.asList("B-", "O-"));
        RECIPIENT_TO_DONOR_MAP.put("AB+", Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
        RECIPIENT_TO_DONOR_MAP.put("AB-", Arrays.asList("A-", "B-", "AB-", "O-"));
        RECIPIENT_TO_DONOR_MAP.put("O+", Arrays.asList("O+", "O-"));
        RECIPIENT_TO_DONOR_MAP.put("O-", Collections.singletonList("O-"));

        // Donor compatibility matrix
        DONOR_TO_RECIPIENT_MAP.put("O-", Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
        DONOR_TO_RECIPIENT_MAP.put("O+", Arrays.asList("O+", "A+", "B+", "AB+"));
        DONOR_TO_RECIPIENT_MAP.put("A-", Arrays.asList("A+", "A-", "AB+", "AB-"));
        DONOR_TO_RECIPIENT_MAP.put("A+", Arrays.asList("A+", "AB+"));
        DONOR_TO_RECIPIENT_MAP.put("B-", Arrays.asList("B+", "B-", "AB+", "AB-"));
        DONOR_TO_RECIPIENT_MAP.put("B+", Arrays.asList("B+", "AB+"));
        DONOR_TO_RECIPIENT_MAP.put("AB-", Arrays.asList("AB+", "AB-"));
        DONOR_TO_RECIPIENT_MAP.put("AB+", Collections.singletonList("AB+"));
    }

    public static List<String> getCompatibleDonorBloodGroups(String recipientBloodGroup) {
        if (recipientBloodGroup == null) {
            return Collections.emptyList();
        }
        return RECIPIENT_TO_DONOR_MAP.getOrDefault(recipientBloodGroup.trim().toUpperCase(), Collections.emptyList());
    }

    public static List<String> getCompatibleRecipientBloodGroups(String donorBloodGroup) {
        if (donorBloodGroup == null) {
            return Collections.emptyList();
        }
        return DONOR_TO_RECIPIENT_MAP.getOrDefault(donorBloodGroup.trim().toUpperCase(), Collections.emptyList());
    }

    public static boolean isDonorCompatibleWithRecipient(String donorBloodGroup, String recipientBloodGroup) {
        if (donorBloodGroup == null || recipientBloodGroup == null) {
            return false;
        }
        List<String> compatibleDonors = getCompatibleDonorBloodGroups(recipientBloodGroup);
        return compatibleDonors.contains(donorBloodGroup.trim().toUpperCase());
    }
}
