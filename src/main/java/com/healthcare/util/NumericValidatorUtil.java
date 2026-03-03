package com.healthcare.util;

import java.util.OptionalDouble;

public class NumericValidatorUtil {

    // Utility class — no instantiation
    private NumericValidatorUtil() {}

    /**
     * Safely parses a string to double.
     * Returns OptionalDouble.empty() if parsing fails.
     *
     * Examples:
     *   "120.5" → OptionalDouble.of(120.5)
     *   "N/A"   → OptionalDouble.empty()
     *   ""      → OptionalDouble.empty()
     *   null    → OptionalDouble.empty()
     *   "abc"   → OptionalDouble.empty()
     */
    public static OptionalDouble parseDouble(String value) {

        // DEFENSIVE: null or blank
        if (value == null || value.trim().isEmpty()) {
            return OptionalDouble.empty();
        }

        try {
            return OptionalDouble.of(
                Double.parseDouble(value.trim()));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    /**
     * Checks if a value is within valid range (inclusive).
     *
     * @param value the parsed double value
     * @param min   minimum valid value
     * @param max   maximum valid value
     * @return true if value is within range
     */
    public static boolean isInRange(
            double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * Validates a raw string field:
     * - Tries to parse it as a double
     * - Checks if it falls within valid range
     * - Returns original string if valid
     * - Returns "INVALID" if unparseable or out of range
     *
     * @param raw   the raw string value
     * @param min   minimum valid value
     * @param max   maximum valid value
     * @return original value if valid, "INVALID" otherwise
     */
    public static String validateNumeric(
            String raw, double min, double max) {

        OptionalDouble parsed = parseDouble(raw);

        // Could not parse → INVALID
        if (parsed.isEmpty()) {
            return "INVALID";
        }

        // Out of range → INVALID
        if (!isInRange(parsed.getAsDouble(), min, max)) {
            return "INVALID";
        }

        // Valid — return trimmed original
        return raw.trim();
    }

    /**
     * Strips common currency prefixes before parsing.
     * Examples:
     *   "Rs.4500.00"  → "4500.00"
     *   "rs.4500"     → "4500"
     *   "RS.4500"     → "4500"
     *   "4500.00"     → "4500.00" (unchanged)
     *   "N/A"         → "N/A"    (unchanged — will fail parse anyway)
     */
    public static String stripCurrencyPrefix(String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }
        // Remove Rs. or rs. or RS. prefix (case insensitive)
        return value.trim().replaceAll("(?i)^rs\\.","");
    }
}
