package com.blooddonation.model;

public enum UserRole {
    DONOR,
    RECIPIENT,
    HOSPITAL,
    BLOOD_BANK,
    ADMIN;

    public static boolean isValid(String role) {
        if (role == null) return false;
        try {
            valueOf(role.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static UserRole fromString(String role) {
        if (role == null) return null;
        return valueOf(role.trim().toUpperCase());
    }
}
