package com.healthcare.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ContactValidatorUtilTest {

    // ── Phone tests ───────────────────────────────────────

    @Test
    @DisplayName("Valid 10 digit phone starting with 9 is accepted")
    void validatePhone_validPhone_returned() {
        assertThat(ContactValidatorUtil
            .validatePhone("9876543210"))
            .isEqualTo("9876543210");
    }

    @Test
    @DisplayName("Valid phone starting with 6 is accepted")
    void validatePhone_startingWith6_returned() {
        assertThat(ContactValidatorUtil
            .validatePhone("6789012345"))
            .isEqualTo("6789012345");
    }

    @Test
    @DisplayName("Phone with spaces trimmed before validation")
    void validatePhone_withSpaces_trimmedAndAccepted() {
        assertThat(ContactValidatorUtil
            .validatePhone("  9876543210  "))
            .isEqualTo("9876543210");
    }

    @Test
    @DisplayName("Phone starting with 5 is INVALID")
    void validatePhone_startingWith5_invalid() {
        assertThat(ContactValidatorUtil
            .validatePhone("5876543210"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Phone too short is INVALID")
    void validatePhone_tooShort_invalid() {
        assertThat(ContactValidatorUtil
            .validatePhone("123"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Phone too long is INVALID")
    void validatePhone_tooLong_invalid() {
        assertThat(ContactValidatorUtil
            .validatePhone("98765432101234"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Phone with letters is INVALID")
    void validatePhone_withLetters_invalid() {
        assertThat(ContactValidatorUtil
            .validatePhone("INVALID"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Blank phone is INVALID")
    void validatePhone_blank_invalid() {
        assertThat(ContactValidatorUtil
            .validatePhone(""))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Null phone is INVALID")
    void validatePhone_null_invalid() {
        assertThat(ContactValidatorUtil
            .validatePhone(null))
            .isEqualTo("INVALID");
    }

    @ParameterizedTest
    @DisplayName("Various invalid phones all return INVALID")
    @CsvSource({
        "123",
        "INVALID",
        "98765432101234",
        "5876543210",
        "abcdefghij"
    })
    void validatePhone_variousInvalid_allInvalid(String phone) {
        assertThat(ContactValidatorUtil.validatePhone(phone))
            .isEqualTo("INVALID");
    }

    // ── Email tests ───────────────────────────────────────

    @Test
    @DisplayName("Valid email returned lowercased")
    void validateEmail_validEmail_returnedLowercase() {
        assertThat(ContactValidatorUtil
            .validateEmail("john.smith@gmail.com"))
            .isEqualTo("john.smith@gmail.com");
    }

    @Test
    @DisplayName("Uppercase valid email lowercased")
    void validateEmail_uppercase_lowercased() {
        assertThat(ContactValidatorUtil
            .validateEmail("JOHN@GMAIL.COM"))
            .isEqualTo("john@gmail.com");
    }

    @Test
    @DisplayName("Email missing @ is INVALID")
    void validateEmail_missingAt_invalid() {
        assertThat(ContactValidatorUtil
            .validateEmail("thomasmitchell"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Email missing domain is INVALID")
    void validateEmail_missingDomain_invalid() {
        assertThat(ContactValidatorUtil
            .validateEmail("john@"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Email missing TLD is INVALID")
    void validateEmail_missingTld_invalid() {
        assertThat(ContactValidatorUtil
            .validateEmail("john@gmail"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Completely invalid email string is INVALID")
    void validateEmail_completelyInvalid_invalid() {
        assertThat(ContactValidatorUtil
            .validateEmail("not-an-email"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Blank email is INVALID")
    void validateEmail_blank_invalid() {
        assertThat(ContactValidatorUtil
            .validateEmail(""))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Null email is INVALID")
    void validateEmail_null_invalid() {
        assertThat(ContactValidatorUtil
            .validateEmail(null))
            .isEqualTo("INVALID");
    }
}