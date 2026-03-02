package com.healthcare.util;

public class NameNormalizerUtil {

    // Utility class — no instantiation
    private NameNormalizerUtil() {}

    /**
     * Converts a name to Title Case.
     * Examples:
     *   "john"    → "John"
     *   "JOHN"    → "John"
     *   "  john " → "John"
     *   null      → ""
     *   ""        → ""
     */
    public static String toTitleCase(String name) {

        // DEFENSIVE: null check
        if (name == null) {
            return "";
        }

        // DEFENSIVE: blank check
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        // Title case: first letter upper, rest lower
        return trimmed.substring(0, 1).toUpperCase()
            + trimmed.substring(1).toLowerCase();
    }
}
