package com.blooddonation.model;

public enum UrgencyLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public static boolean isValid(String urgency) {
        if (urgency == null) return false;
        try {
            valueOf(urgency.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static UrgencyLevel fromString(String urgency) {
        if (urgency == null) return null;
        return valueOf(urgency.trim().toUpperCase());
    }
}
