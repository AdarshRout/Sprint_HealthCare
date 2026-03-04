package com.healthcare.constant;

public enum AdmissionStatus {

    ADMITTED("Admitted"),
    DISCHARGED("Discharged"),
    OUTPATIENT("Outpatient"),
    INPATIENT("Inpatient"),
    REFERRED("Referred"),
    UNKNOWN("UNKNOWN");

    private final String displayName;

    AdmissionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AdmissionStatus fromRaw(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return UNKNOWN;
        }
        switch (raw.trim().toLowerCase()) {
            case "admitted":
            case "admit":
            case "admttd":      return ADMITTED;
            case "discharged":
            case "dischrgd":    return DISCHARGED;
            case "outpatient":
            case "out-patient": return OUTPATIENT;
            case "inpatient":
            case "in-patient":  return INPATIENT;
            case "referred":
            case "refrd":       return REFERRED;
            default:            return UNKNOWN;
        }
    }
}