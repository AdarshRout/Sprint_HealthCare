package com.healthcare.util;

import java.util.regex.Pattern;

public class ContactValidatorUtil {

    private ContactValidatorUtil() {}

    // 10 digit number starting with 6, 7, 8 or 9
    // (Indian mobile number format)
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[6-9]\\d{9}$");

    // Standard email pattern
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile(
            "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$"
        );

    /**
     * Validates and cleans a phone number.
     * Valid: 10 digits starting with 6-9
     * Returns trimmed number if valid.
     * Returns "INVALID" if not valid.
     */
    public static String validatePhone(String raw) {

        // DEFENSIVE: null or blank
        if (raw == null || raw.trim().isEmpty()) {
            return "INVALID";
        }

        String trimmed = raw.trim();

        if (PHONE_PATTERN.matcher(trimmed).matches()) {
            return trimmed;
        }

        return "INVALID";
    }

    /**
     * Validates and cleans an email address.
     * Valid: contains @ and a valid domain
     * Returns lowercased email if valid.
     * Returns "INVALID" if not valid.
     */
    public static String validateEmail(String raw) {

        // DEFENSIVE: null or blank
        if (raw == null || raw.trim().isEmpty()) {
            return "INVALID";
        }

        String trimmed = raw.trim();

        if (EMAIL_PATTERN.matcher(trimmed).matches()) {
            // Normalize to lowercase
            return trimmed.toLowerCase();
        }

        return "INVALID";
    }
}