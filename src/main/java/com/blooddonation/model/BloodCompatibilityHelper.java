package com.blooddonation.model;

import java.util.*;

public class BloodCompatibilityHelper {

    private static final Map<String, List<String>> DONOR_TO_RECIPIENT_MAP = new HashMap<>();

    static {
        DONOR_TO_RECIPIENT_MAP.put("O-", Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
        DONOR_TO_RECIPIENT_MAP.put("O+", Arrays.asList("O+", "A+", "B+", "AB+"));
        DONOR_TO_RECIPIENT_MAP.put("A-", Arrays.asList("A+", "A-", "AB+", "AB-"));
        DONOR_TO_RECIPIENT_MAP.put("A+", Arrays.asList("A+", "AB+"));
        DONOR_TO_RECIPIENT_MAP.put("B-", Arrays.asList("B+", "B-", "AB+", "AB-"));
        DONOR_TO_RECIPIENT_MAP.put("B+", Arrays.asList("B+", "AB+"));
        DONOR_TO_RECIPIENT_MAP.put("AB-", Arrays.asList("AB+", "AB-"));
        DONOR_TO_RECIPIENT_MAP.put("AB+", Collections.singletonList("AB+"));
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
        List<String> compatibleGroups = getCompatibleRecipientBloodGroups(donorBloodGroup);
        return compatibleGroups.contains(recipientBloodGroup.trim().toUpperCase());
    }
}
