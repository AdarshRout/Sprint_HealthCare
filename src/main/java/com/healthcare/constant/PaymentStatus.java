package com.healthcare.constant;

public enum PaymentStatus {

    PAID("Paid"),
    PENDING("Pending"),
    PARTIAL("Partial"),
    WAIVED("Waived"),
    INSURANCE("Insurance"),
    UNKNOWN("UNKNOWN");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentStatus fromRaw(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return UNKNOWN;
        }
        switch (raw.trim().toLowerCase()) {
            case "paid":
            case "piad":
            case "paid in full":    return PAID;
            case "pending":
            case "pend":            return PENDING;
            case "partial":         return PARTIAL;
            case "waived":
            case "waved":           return WAIVED;
            case "insurance":
            case "insurnace":       return INSURANCE;
            default:                return UNKNOWN;
        }
    }
}