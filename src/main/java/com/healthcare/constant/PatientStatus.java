package com.healthcare.constant;

public enum PatientStatus {

    CRITICAL("Critical"),
    STABLE("Stable"),
    ACTIVE("Active"),
    DISCHARGED("Discharged"),
    PENDING("Pending"),
    UNKNOWN("UNKNOWN");

    private final String displayName;

    PatientStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PatientStatus fromRaw(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return UNKNOWN;
        }
        switch (raw.trim().toLowerCase()) {
            case "critical":    return CRITICAL;
            case "stable":      return STABLE;
            case "active":      return ACTIVE;
            case "discharged":  return DISCHARGED;
            case "pending":     return PENDING;
            default:            return UNKNOWN;
        }
    }
}