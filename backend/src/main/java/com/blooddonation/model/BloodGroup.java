package com.blooddonation.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum BloodGroup {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-");

    private final String code;

    BloodGroup(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    private static final Set<String> VALID_CODES = Arrays.stream(values())
            .map(BloodGroup::getCode)
            .collect(Collectors.toSet());

    public static boolean isValid(String code) {
        if (code == null) return false;
        return VALID_CODES.contains(code.trim().toUpperCase());
    }

    public static BloodGroup fromCode(String code) {
        if (code == null) return null;
        String normalized = code.trim().toUpperCase();
        for (BloodGroup bg : values()) {
            if (bg.code.equalsIgnoreCase(normalized)) {
                return bg;
            }
        }
        throw new IllegalArgumentException("Invalid blood group: " + code);
    }
}
