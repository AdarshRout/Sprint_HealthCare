package com.healthcare.constant;

public enum Department {

    CARDIOLOGY("Cardiology"),
    ORTHOPEDICS("Orthopedics"),
    NEUROLOGY("Neurology"),
    DERMATOLOGY("Dermatology"),
    ONCOLOGY("Oncology"),
    GASTROENTEROLOGY("Gastroenterology"),
    PEDIATRICS("Pediatrics"),
    ENDOCRINOLOGY("Endocrinology"),
    GYNECOLOGY("Gynecology"),
    PSYCHIATRY("Psychiatry"),
    OPHTHALMOLOGY("Ophthalmology"),
    NEPHROLOGY("Nephrology"),
    PULMONOLOGY("Pulmonology"),
    UROLOGY("Urology"),
    UNKNOWN("UNKNOWN");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Department fromRaw(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return UNKNOWN;
        }
        switch (raw.trim().toUpperCase()) {
            case "CARDIOLOGY":
            case "CARD":            return CARDIOLOGY;
            case "ORTHOPEDICS":
            case "ORTH":            return ORTHOPEDICS;
            case "NEUROLOGY":
            case "NEUR":            return NEUROLOGY;
            case "DERMATOLOGY":
            case "DERM":            return DERMATOLOGY;
            case "ONCOLOGY":
            case "ONCO":            return ONCOLOGY;
            case "GASTROENTEROLOGY":
            case "GAST":            return GASTROENTEROLOGY;
            case "PEDIATRICS":
            case "PEDI":            return PEDIATRICS;
            case "ENDOCRINOLOGY":
            case "ENDO":            return ENDOCRINOLOGY;
            case "GYNECOLOGY":
            case "GYNE":            return GYNECOLOGY;
            case "PSYCHIATRY":
            case "PSYC":            return PSYCHIATRY;
            case "OPHTHALMOLOGY":
            case "OPHT":            return OPHTHALMOLOGY;
            case "NEPHROLOGY":
            case "NEPH":            return NEPHROLOGY;
            case "PULMONOLOGY":
            case "PULM":            return PULMONOLOGY;
            case "UROLOGY":
            case "UROL":            return UROLOGY;
            default:                return UNKNOWN;
        }
    }
}