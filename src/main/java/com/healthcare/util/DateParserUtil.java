package com.healthcare.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DateParserUtil {

    // Utility class — no instantiation
    private DateParserUtil() {}

    // Standard output format
    public static final DateTimeFormatter OUTPUT_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // All known input formats in your dataset
    private static final List<DateTimeFormatter> KNOWN_FORMATS =
        Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),   // 1957-09-18
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),   // 22/05/1970
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),   // 05/06/1952
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),   // 22-05-1970
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),   // 22.05.1970
            DateTimeFormatter.ofPattern("yyyyMMdd"),     // 19900304
            DateTimeFormatter.ofPattern("dd MMM yyyy"),  // 05 Jun 2023
            DateTimeFormatter.ofPattern("MMM dd yyyy"),  // Apr 25 1983
            DateTimeFormatter.ofPattern("MMM d yyyy"),   // Nov 12 1952
            DateTimeFormatter.ofPattern("d MMM yyyy")    // 14 Aug 2022
        );

    /**
     * Tries to parse a raw date string using all known formats.
     * Returns Optional.empty() if no format matches.
     *
     * @param rawDate raw date string from Google Sheets
     * @return Optional<LocalDate> — present if parsed, empty if failed
     */
    public static Optional<LocalDate> parse(String rawDate) {

        // DEFENSIVE: null or blank
        if (rawDate == null || rawDate.trim().isEmpty()) {
            return Optional.empty();
        }

        String trimmed = rawDate.trim();

        for (DateTimeFormatter formatter : KNOWN_FORMATS) {
            try {
                LocalDate date = LocalDate.parse(trimmed, formatter);
                return Optional.of(date);
            } catch (DateTimeParseException e) {
                // This format didn't match — try next one
            }
        }

        // No format matched
        return Optional.empty();
    }

    /**
     * Parses and formats a raw date string to yyyy-MM-dd.
     * Returns "INVALID" if no format matches.
     *
     * @param rawDate raw date string
     * @return formatted date string or "INVALID"
     */
    public static String parseAndFormat(String rawDate) {
        Optional<LocalDate> parsed = parse(rawDate);

        if (parsed.isEmpty()) {
            return "INVALID";
        }

        return parsed.get().format(OUTPUT_FORMAT);
    }
}
