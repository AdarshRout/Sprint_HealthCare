package com.healthcare.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.OptionalDouble;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NumericValidatorUtilTest {

    // ── parseDouble tests ─────────────────────────────────

    @Test
    @DisplayName("Valid integer string parses correctly")
    void parseDouble_validInteger_returnsValue() {
        OptionalDouble result = 
            NumericValidatorUtil.parseDouble("120");
        assertThat(result.isPresent()).isTrue();
        assertThat(result.getAsDouble()).isEqualTo(120.0);
    }

    @Test
    @DisplayName("Valid decimal string parses correctly")
    void parseDouble_validDecimal_returnsValue() {
        OptionalDouble result = 
            NumericValidatorUtil.parseDouble("72.5");
        assertThat(result.isPresent()).isTrue();
        assertThat(result.getAsDouble()).isEqualTo(72.5);
    }

    @Test
    @DisplayName("N/A returns empty")
    void parseDouble_na_returnsEmpty() {
        assertThat(NumericValidatorUtil.parseDouble("N/A")
            .isEmpty()).isTrue();
    }

    @Test
    @DisplayName("null returns empty")
    void parseDouble_null_returnsEmpty() {
        assertThat(NumericValidatorUtil.parseDouble(null)
            .isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Empty string returns empty")
    void parseDouble_empty_returnsEmpty() {
        assertThat(NumericValidatorUtil.parseDouble("")
            .isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Alphabetic string returns empty")
    void parseDouble_alpha_returnsEmpty() {
        assertThat(NumericValidatorUtil.parseDouble("abc")
            .isEmpty()).isTrue();
    }

    @Test
    @DisplayName("null string returns empty")
    void parseDouble_nullString_returnsEmpty() {
        assertThat(NumericValidatorUtil.parseDouble("null")
            .isEmpty()).isTrue();
    }

    // ── isInRange tests ───────────────────────────────────

    @Test
    @DisplayName("Value within range returns true")
    void isInRange_withinRange_returnsTrue() {
        assertThat(NumericValidatorUtil.isInRange(120, 60, 250))
            .isTrue();
    }

    @Test
    @DisplayName("Value at minimum boundary returns true")
    void isInRange_atMinBoundary_returnsTrue() {
        assertThat(NumericValidatorUtil.isInRange(60, 60, 250))
            .isTrue();
    }

    @Test
    @DisplayName("Value at maximum boundary returns true")
    void isInRange_atMaxBoundary_returnsTrue() {
        assertThat(NumericValidatorUtil.isInRange(250, 60, 250))
            .isTrue();
    }

    @Test
    @DisplayName("Value below minimum returns false")
    void isInRange_belowMin_returnsFalse() {
        assertThat(NumericValidatorUtil.isInRange(59, 60, 250))
            .isFalse();
    }

    @Test
    @DisplayName("Value above maximum returns false")
    void isInRange_aboveMax_returnsFalse() {
        assertThat(NumericValidatorUtil.isInRange(251, 60, 250))
            .isFalse();
    }

    @Test
    @DisplayName("Negative value returns false")
    void isInRange_negative_returnsFalse() {
        assertThat(NumericValidatorUtil.isInRange(-186, 50, 250))
            .isFalse();
    }

    // ── validateNumeric tests ─────────────────────────────

    @Test
    @DisplayName("Valid value within range returned as-is")
    void validateNumeric_valid_returnsOriginal() {
        assertThat(NumericValidatorUtil
            .validateNumeric("120", 60, 250))
            .isEqualTo("120");
    }

    @Test
    @DisplayName("N/A returns INVALID")
    void validateNumeric_na_returnsInvalid() {
        assertThat(NumericValidatorUtil
            .validateNumeric("N/A", 60, 250))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Negative value returns INVALID")
    void validateNumeric_negative_returnsInvalid() {
        assertThat(NumericValidatorUtil
            .validateNumeric("-186", 50, 250))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Out of range value returns INVALID")
    void validateNumeric_outOfRange_returnsInvalid() {
        assertThat(NumericValidatorUtil
            .validateNumeric("900", 60, 250))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Blank string returns INVALID")
    void validateNumeric_blank_returnsInvalid() {
        assertThat(NumericValidatorUtil
            .validateNumeric("", 60, 250))
            .isEqualTo("INVALID");
    }
    
    @Test
    @DisplayName("Rs. prefix is stripped before parsing")
    void stripCurrencyPrefix_rsPrefix_stripped() {
        assertThat(NumericValidatorUtil
            .stripCurrencyPrefix("Rs.4500.00"))
            .isEqualTo("4500.00");
    }
    
    @Test
    @DisplayName("Lowercase rs. prefix is stripped")
    void stripCurrencyPrefix_lowercasePrefix_stripped() {
        assertThat(NumericValidatorUtil
            .stripCurrencyPrefix("rs.4500.00"))
            .isEqualTo("4500.00");
    }
    
    @Test
    @DisplayName("Value without prefix unchanged")
    void stripCurrencyPrefix_noPrefix_unchanged() {
        assertThat(NumericValidatorUtil
            .stripCurrencyPrefix("4500.00"))
            .isEqualTo("4500.00");
    }
    
    @Test
    @DisplayName("Null value returned as-is")
    void stripCurrencyPrefix_null_returnsNull() {
        assertThat(NumericValidatorUtil
            .stripCurrencyPrefix(null))
            .isNull();
    }

    @ParameterizedTest
    @DisplayName("Various invalid inputs all return INVALID")
    @CsvSource({
        "N/A",
        "null",
        "abc",
        "-999"
    })
    void validateNumeric_variousInvalid_allReturnInvalid(
            String input) {
        assertThat(NumericValidatorUtil
            .validateNumeric(input, 60, 250))
            .isEqualTo("INVALID");
    }

    
}
