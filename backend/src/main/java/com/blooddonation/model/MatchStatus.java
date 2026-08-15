package com.blooddonation.model;

public enum MatchStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    CANCELLED;

    public static boolean isValid(String status) {
        if (status == null) return false;
        try {
            valueOf(status.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static MatchStatus fromString(String status) {
        if (status == null) return null;
        return valueOf(status.trim().toUpperCase());
    }
}
