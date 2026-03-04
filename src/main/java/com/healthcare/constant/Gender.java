package com.healthcare.constant;

public enum Gender {

    MALE("Male"),
    FEMALE("Female"),
    UNKNOWN("UNKNOWN");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Maps raw input to Gender enum.
     * Returns UNKNOWN if no match found.
     */
    public static Gender fromRaw(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return UNKNOWN;
        }
        switch (raw.trim().toLowerCase()) {
            case "m":
            case "male":    return MALE;
            case "f":
            case "female":  return FEMALE;
            default:        return UNKNOWN;
        }
    }
}
