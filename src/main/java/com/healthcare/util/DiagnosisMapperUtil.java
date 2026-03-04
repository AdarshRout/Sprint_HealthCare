package com.healthcare.util;

import com.healthcare.constant.DiagnosisMappings;

public class DiagnosisMapperUtil {

    private DiagnosisMapperUtil() {}

    /**
     * Maps an ICD-10 code to its disease description.
     *
     * Steps:
     *  1. Null/blank check → return "UNKNOWN"
     *  2. Uppercase and trim the code
     *  3. Try exact match first — "I10" → "Essential Hypertension"
     *  4. Try prefix match — "M54.5" matches "M54"
     *  5. No match → return "UNKNOWN"
     *
     * @param rawCode raw ICD-10 code from sheet
     * @return disease description or "UNKNOWN"
     */
    public static String mapToDisease(String rawCode) {

        // DEFENSIVE: null or blank
        if (rawCode == null || rawCode.trim().isEmpty()) {
            return "UNKNOWN";
        }

        String code = rawCode.trim().toUpperCase();

        // Try exact match first
        if (DiagnosisMappings.getIcdMap().containsKey(code)) {
            return DiagnosisMappings.getIcdMap().get(code);
        }

        // Try prefix match for codes like "M54.5" → "M54"
        for (String key : DiagnosisMappings.getIcdMap().keySet()) {
            if (key.startsWith(code)) {
                return DiagnosisMappings.getIcdMap().get(key);
            }
        }

        // No match found
        return "UNKNOWN";
    }
}
