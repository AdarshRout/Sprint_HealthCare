package com.healthcare.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DerivedFieldsUtilTest {

    // ── BMI tests ─────────────────────────────────────────

    @Test
    @DisplayName("Valid weight and height calculates BMI correctly")
    void calculateBmi_validInputs_returnsCorrectBmi() {
        // 70kg, 175cm → BMI = 70 / (1.75)² = 22.86
        assertThat(DerivedFieldsUtil.calculateBmi("70", "175"))
            .isEqualTo("22.86");
    }

    @Test
    @DisplayName("INVALID weight returns INVALID BMI")
    void calculateBmi_invalidWeight_returnsInvalid() {
        assertThat(DerivedFieldsUtil.calculateBmi("INVALID", "175"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("INVALID height returns INVALID BMI")
    void calculateBmi_invalidHeight_returnsInvalid() {
        assertThat(DerivedFieldsUtil.calculateBmi("70", "INVALID"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Both INVALID returns INVALID BMI")
    void calculateBmi_bothInvalid_returnsInvalid() {
        assertThat(DerivedFieldsUtil
            .calculateBmi("INVALID", "INVALID"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Null weight returns INVALID BMI")
    void calculateBmi_nullWeight_returnsInvalid() {
        assertThat(DerivedFieldsUtil.calculateBmi(null, "175"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Zero height returns INVALID BMI")
    void calculateBmi_zeroHeight_returnsInvalid() {
        assertThat(DerivedFieldsUtil.calculateBmi("70", "0"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Implausible BMI above 70 returns INVALID")
    void calculateBmi_implausibleHigh_returnsInvalid() {
        // 200kg, 50cm → BMI = 800 (impossible)
        assertThat(DerivedFieldsUtil.calculateBmi("200", "50"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("BMI result has 2 decimal places")
    void calculateBmi_result_hasTwoDecimalPlaces() {
        String result = DerivedFieldsUtil
            .calculateBmi("70", "175");
        assertThat(result).matches("\\d+\\.\\d{2}");
    }

    // ── MAP tests ─────────────────────────────────────────

    @Test
    @DisplayName("Valid systolic and diastolic calculates MAP")
    void calculateMap_validInputs_returnsCorrectMap() {
        // systolic=120, diastolic=80
        // MAP = 80 + (120-80)/3 = 80 + 13.33 = 93.33
        assertThat(DerivedFieldsUtil.calculateMap("120", "80"))
            .isEqualTo("93.33");
    }

    @Test
    @DisplayName("INVALID systolic returns INVALID MAP")
    void calculateMap_invalidSystolic_returnsInvalid() {
        assertThat(DerivedFieldsUtil
            .calculateMap("INVALID", "80"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("INVALID diastolic returns INVALID MAP")
    void calculateMap_invalidDiastolic_returnsInvalid() {
        assertThat(DerivedFieldsUtil
            .calculateMap("120", "INVALID"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("MAP result has 2 decimal places")
    void calculateMap_result_hasTwoDecimalPlaces() {
        String result = DerivedFieldsUtil
            .calculateMap("120", "80");
        assertThat(result).matches("\\d+\\.\\d{2}");
    }

    @Test
    @DisplayName("Null systolic returns INVALID MAP")
    void calculateMap_nullSystolic_returnsInvalid() {
        assertThat(DerivedFieldsUtil.calculateMap(null, "80"))
            .isEqualTo("INVALID");
    }

    // ── LOS tests ─────────────────────────────────────────

    @Test
    @DisplayName("Valid dates calculates LOS correctly")
    void calculateLos_validDates_returnsCorrectDays() {
        assertThat(DerivedFieldsUtil
            .calculateLos("2024-01-10", "2024-01-15"))
            .isEqualTo("5");
    }

    @Test
    @DisplayName("Same day admission and discharge returns 0")
    void calculateLos_sameDay_returnsZero() {
        assertThat(DerivedFieldsUtil
            .calculateLos("2024-01-10", "2024-01-10"))
            .isEqualTo("0");
    }

    @Test
    @DisplayName("Discharge before admission returns INVALID")
    void calculateLos_dischargeBeforeAdmission_returnsInvalid() {
        assertThat(DerivedFieldsUtil
            .calculateLos("2024-01-15", "2024-01-10"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("INVALID admission date returns INVALID LOS")
    void calculateLos_invalidAdmission_returnsInvalid() {
        assertThat(DerivedFieldsUtil
            .calculateLos("INVALID", "2024-01-15"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("INVALID discharge date returns INVALID LOS")
    void calculateLos_invalidDischarge_returnsInvalid() {
        assertThat(DerivedFieldsUtil
            .calculateLos("2024-01-10", "INVALID"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Stay longer than 365 days returns INVALID")
    void calculateLos_longerThan365Days_returnsInvalid() {
        assertThat(DerivedFieldsUtil
            .calculateLos("2020-01-01", "2022-01-01"))
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Null admission date returns INVALID")
    void calculateLos_nullAdmission_returnsInvalid() {
        assertThat(DerivedFieldsUtil
            .calculateLos(null, "2024-01-15"))
            .isEqualTo("INVALID");
    }
}