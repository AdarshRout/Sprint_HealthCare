package com.healthcare.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NameNormalizerUtilTest {

    @Test
    @DisplayName("Lowercase name converted to title case")
    void toTitleCase_lowercase_returnsCapitalized() {
        assertThat(NameNormalizerUtil.toTitleCase("john"))
            .isEqualTo("John");
    }

    @Test
    @DisplayName("Uppercase name converted to title case")
    void toTitleCase_uppercase_returnsCapitalized() {
        assertThat(NameNormalizerUtil.toTitleCase("JOHN"))
            .isEqualTo("John");
    }

    @Test
    @DisplayName("Mixed case name converted to title case")
    void toTitleCase_mixedCase_returnsCapitalized() {
        assertThat(NameNormalizerUtil.toTitleCase("jOhN"))
            .isEqualTo("John");
    }

    @Test
    @DisplayName("Leading and trailing spaces are trimmed")
    void toTitleCase_withSpaces_returnsTrimmed() {
        assertThat(NameNormalizerUtil.toTitleCase("  john  "))
            .isEqualTo("John");
    }

    @Test
    @DisplayName("Null input returns empty string")
    void toTitleCase_null_returnsEmpty() {
        assertThat(NameNormalizerUtil.toTitleCase(null))
            .isEqualTo("");
    }

    @Test
    @DisplayName("Empty string returns empty string")
    void toTitleCase_empty_returnsEmpty() {
        assertThat(NameNormalizerUtil.toTitleCase(""))
            .isEqualTo("");
    }

    @Test
    @DisplayName("Blank string returns empty string")
    void toTitleCase_blank_returnsEmpty() {
        assertThat(NameNormalizerUtil.toTitleCase("   "))
            .isEqualTo("");
    }

    @Test
    @DisplayName("Already correct title case stays the same")
    void toTitleCase_alreadyCorrect_unchanged() {
        assertThat(NameNormalizerUtil.toTitleCase("John"))
            .isEqualTo("John");
    }

    @ParameterizedTest
    @DisplayName("Various inputs all produce correct title case")
    @CsvSource({
        "john,     John",
        "JOHN,     John",
        "jOhN,     John",
        "smith,    Smith",
        "SMITH,    Smith",
        "garcia,   Garcia",
        "WILLIAMS, Williams"
    })
    void toTitleCase_variousInputs_alwaysTitleCase(
            String input, String expected) {
        assertThat(NameNormalizerUtil.toTitleCase(input.trim()))
            .isEqualTo(expected.trim());
    }
}
