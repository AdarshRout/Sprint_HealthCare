package com.healthcare.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DateParserUtilTest {

    // ── parse() tests ─────────────────────────────────────

    @Test
    @DisplayName("ISO format yyyy-MM-dd parsed correctly")
    void parse_isoFormat_returnsDate() {
        Optional<LocalDate> result =
            DateParserUtil.parse("1957-09-18");
        assertThat(result).isPresent();
        assertThat(result.get())
            .isEqualTo(LocalDate.of(1957, 9, 18));
    }

    @Test
    @DisplayName("Dot separated dd.MM.yyyy parsed correctly")
    void parse_dotSeparated_returnsDate() {
        Optional<LocalDate> result =
            DateParserUtil.parse("22.05.1970");
        assertThat(result).isPresent();
        assertThat(result.get())
            .isEqualTo(LocalDate.of(1970, 5, 22));
    }

    @Test
    @DisplayName("No separator yyyyMMdd parsed correctly")
    void parse_noSeparator_returnsDate() {
        Optional<LocalDate> result =
            DateParserUtil.parse("19900304");
        assertThat(result).isPresent();
        assertThat(result.get())
            .isEqualTo(LocalDate.of(1990, 3, 4));
    }

    @Test
    @DisplayName("Month name format MMM dd yyyy parsed correctly")
    void parse_monthNameFormat_returnsDate() {
        Optional<LocalDate> result =
            DateParserUtil.parse("Apr 25 1983");
        assertThat(result).isPresent();
        assertThat(result.get())
            .isEqualTo(LocalDate.of(1983, 4, 25));
    }

    @Test
    @DisplayName("Day month name format dd MMM yyyy parsed correctly")
    void parse_dayMonthNameFormat_returnsDate() {
        Optional<LocalDate> result =
            DateParserUtil.parse("14 Aug 2022");
        assertThat(result).isPresent();
        assertThat(result.get())
            .isEqualTo(LocalDate.of(2022, 8, 14));
    }

    @Test
    @DisplayName("Completely invalid string returns empty")
    void parse_invalidString_returnsEmpty() {
        Optional<LocalDate> result =
            DateParserUtil.parse("not-a-date");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Null returns empty")
    void parse_null_returnsEmpty() {
        assertThat(DateParserUtil.parse(null)).isEmpty();
    }

    @Test
    @DisplayName("Empty string returns empty")
    void parse_empty_returnsEmpty() {
        assertThat(DateParserUtil.parse("")).isEmpty();
    }

    @Test
    @DisplayName("Blank string returns empty")
    void parse_blank_returnsEmpty() {
        assertThat(DateParserUtil.parse("   ")).isEmpty();
    }

    // ── parseAndFormat() tests ────────────────────────────

    @Test
    @DisplayName("Valid date formatted to yyyy-MM-dd")
    void parseAndFormat_validDate_returnsFormatted() {
        assertThat(DateParserUtil.parseAndFormat("22.05.1970"))
            .isEqualTo("1970-05-22");
    }

    @Test
    @DisplayName("Already ISO format stays the same")
    void parseAndFormat_alreadyIso_unchanged() {
        assertThat(DateParserUtil.parseAndFormat("1957-09-18"))
            .isEqualTo("1957-09-18");
    }

    @Test
    @DisplayName("Invalid date returns INVALID")
    void parseAndFormat_invalid_returnsInvalid() {
        assertThat(DateParserUtil.parseAndFormat("not-a-date"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Null returns INVALID")
    void parseAndFormat_null_returnsInvalid() {
        assertThat(DateParserUtil.parseAndFormat(null))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Single digit day and month d/M/yyyy parsed correctly")
    void parse_singleDigitDayMonth_returnsDate() {
        Optional<LocalDate> result =
            DateParserUtil.parse("3/7/2020");
        assertThat(result).isPresent();
        assertThat(result.get())
            .isEqualTo(LocalDate.of(2020, 7, 3));
    }
    
    @Test
    @DisplayName("Single digit month M/d/yyyy parsed correctly")
    void parse_singleDigitMonth_returnsDate() {
        Optional<LocalDate> result =
            DateParserUtil.parse("4/20/1980");
        assertThat(result).isPresent();
        assertThat(result.get())
            .isEqualTo(LocalDate.of(1980, 4, 20));
    }

    @ParameterizedTest
    @DisplayName("All known formats parse to correct yyyy-MM-dd")
    @CsvSource({
        "1957-09-18,   1957-09-18",
        "22.05.1970,   1970-05-22",
        "19900304,     1990-03-04",
        "14 Aug 2022,  2022-08-14",
        "Apr 25 1983,  1983-04-25",
        "05 Jun 2023,  2023-06-05"
    })
    void parseAndFormat_allKnownFormats_correctOutput(
            String input, String expected) {
        assertThat(DateParserUtil.parseAndFormat(input.trim()))
            .isEqualTo(expected.trim());
    }
}
