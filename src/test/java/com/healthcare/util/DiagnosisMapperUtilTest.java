package com.healthcare.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DiagnosisMapperUtilTest {

    // ── Exact match tests ─────────────────────────────────

    @Test
    @DisplayName("Known ICD-10 code maps to disease name")
    void mapToDisease_knownCode_returnsDisease() {
        assertThat(DiagnosisMapperUtil.mapToDisease("I10"))
            .isEqualTo("Essential (primary) hypertension");
    }

    @Test
    @DisplayName("Lowercase code is uppercased before lookup")
    void mapToDisease_lowercaseCode_returnsDisease() {
        assertThat(DiagnosisMapperUtil.mapToDisease("i10"))
            .isEqualTo("Essential (primary) hypertension");
    }

    @Test
    @DisplayName("Code with leading/trailing spaces is trimmed")
    void mapToDisease_spacesAroundCode_returnsDisease() {
        assertThat(DiagnosisMapperUtil.mapToDisease("  I10  "))
            .isEqualTo("Essential (primary) hypertension");
    }

    // ── Prefix match tests ────────────────────────────────

    @Test
    @DisplayName("Code with subtype maps via prefix match")
    void mapToDisease_subtypeCode_returnsDiseaseViaPrefix() {
        // A0100 should match A010 or A01 prefix
        String result = DiagnosisMapperUtil
            .mapToDisease("A0100");
        assertThat(result).isNotEqualTo("UNKNOWN");
    }

    // ── Unknown / Invalid tests ───────────────────────────

    @Test
    @DisplayName("Unknown code returns UNKNOWN")
    void mapToDisease_unknownCode_returnsUnknown() {
        assertThat(DiagnosisMapperUtil.mapToDisease("ZZZ999"))
            .isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Null input returns UNKNOWN")
    void mapToDisease_null_returnsUnknown() {
        assertThat(DiagnosisMapperUtil.mapToDisease(null))
            .isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Empty string returns UNKNOWN")
    void mapToDisease_empty_returnsUnknown() {
        assertThat(DiagnosisMapperUtil.mapToDisease(""))
            .isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Blank string returns UNKNOWN")
    void mapToDisease_blank_returnsUnknown() {
        assertThat(DiagnosisMapperUtil.mapToDisease("   "))
            .isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Random text returns UNKNOWN")
    void mapToDisease_randomText_returnsUnknown() {
        assertThat(DiagnosisMapperUtil.mapToDisease("UNKNOWN"))
            .isEqualTo("UNKNOWN");
    }
}
