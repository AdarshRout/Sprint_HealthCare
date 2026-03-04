package com.healthcare.constant;

public enum InsuranceStatus {

    ACTIVE("Active"),
    EXPIRED("Expired"),
    NONE("None"),
    VERIFIED("Verified"),
    UNDER_REVIEW("Under Review"),
    UNKNOWN("UNKNOWN");

    private final String displayName;

    InsuranceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static InsuranceStatus fromRaw(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return UNKNOWN;
        }
        switch (raw.trim().toLowerCase()) {
            case "active":
            case "actvie":
            case "actve":       return ACTIVE;
            case "expired":
            case "exprd":       return EXPIRED;
            case "none":        return NONE;
            case "verified":
            case "verifed":     return VERIFIED;
            case "under review":
            case "under-review":return UNDER_REVIEW;
            default:            return UNKNOWN;
        }
    }
}