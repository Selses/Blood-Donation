package com.blooddonation.model;

public enum NotificationType {
    BLOOD_REQUEST,
    MATCH_FOUND,
    DONOR_ACCEPTED,
    DONOR_DECLINED,
    REQUEST_UPDATED,
    EMERGENCY;

    public static boolean isValid(String typeStr) {
        if (typeStr == null) {
            return false;
        }
        for (NotificationType t : values()) {
            if (t.name().equalsIgnoreCase(typeStr.trim())) {
                return true;
            }
        }
        return false;
    }

    public static NotificationType fromString(String typeStr) {
        if (typeStr == null) {
            return null;
        }
        for (NotificationType t : values()) {
            if (t.name().equalsIgnoreCase(typeStr.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown notification type: " + typeStr);
    }
}
