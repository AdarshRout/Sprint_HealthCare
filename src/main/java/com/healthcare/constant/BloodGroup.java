package com.healthcare.constant;

public enum BloodGroup {

    A_POS("A+"),
    A_NEG("A-"),
    B_POS("B+"),
    B_NEG("B-"),
    AB_POS("AB+"),
    AB_NEG("AB-"),
    O_POS("O+"),
    O_NEG("O-"),
    UNKNOWN("UNKNOWN");

    private final String displayName;

    BloodGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static BloodGroup fromRaw(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return UNKNOWN;
        }
        switch (raw.trim().toUpperCase()) {
            case "A+":  return A_POS;
            case "A-":  return A_NEG;
            case "B+":  return B_POS;
            case "B-":  return B_NEG;
            case "AB+": return AB_POS;
            case "AB-": return AB_NEG;
            case "O+":  return O_POS;
            case "O-":  return O_NEG;
            default:    return UNKNOWN;
        }
    }
}
