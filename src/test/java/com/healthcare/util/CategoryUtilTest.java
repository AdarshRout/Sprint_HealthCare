package com.healthcare.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CategoryUtilTest {

    // ── BMI tests ─────────────────────────────────────────

    @ParameterizedTest
    @DisplayName("BMI categories assigned correctly")
    @CsvSource({
        "17.0, Underweight",
        "18.5, Normal",
        "22.0, Normal",
        "24.9, Normal",
        "25.0, Overweight",
        "27.5, Overweight",
        "29.9, Overweight",
        "30.0, Obese",
        "35.0, Obese"
    })
    void categorizeBmi_variousValues_correctCategory(
            String bmi, String expected) {
        assertThat(CategoryUtil.categorizeBmi(bmi))
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("INVALID BMI returns UNKNOWN")
    void categorizeBmi_invalid_returnsUnknown() {
        assertThat(CategoryUtil.categorizeBmi("INVALID"))
            .isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Null BMI returns UNKNOWN")
    void categorizeBmi_null_returnsUnknown() {
        assertThat(CategoryUtil.categorizeBmi(null))
            .isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Blank BMI returns UNKNOWN")
    void categorizeBmi_blank_returnsUnknown() {
        assertThat(CategoryUtil.categorizeBmi(""))
            .isEqualTo("UNKNOWN");
    }

    // ── Blood Sugar tests ─────────────────────────────────

    @ParameterizedTest
    @DisplayName("Blood sugar categories assigned correctly")
    @CsvSource({
        "65,  Low",
        "70,  Normal",
        "95,  Normal",
        "99,  Normal",
        "100, Pre-Diabetic",
        "115, Pre-Diabetic",
        "125, Pre-Diabetic",
        "126, Diabetic",
        "210, Diabetic"
    })
    void categorizeBloodSugar_variousValues_correctCategory(
            String sugar, String expected) {
        assertThat(CategoryUtil.categorizeBloodSugar(sugar))
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("INVALID blood sugar returns UNKNOWN")
    void categorizeBloodSugar_invalid_returnsUnknown() {
        assertThat(CategoryUtil.categorizeBloodSugar("INVALID"))
            .isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Null blood sugar returns UNKNOWN")
    void categorizeBloodSugar_null_returnsUnknown() {
        assertThat(CategoryUtil.categorizeBloodSugar(null))
            .isEqualTo("UNKNOWN");
    }

    // ── Cholesterol tests ─────────────────────────────────

    @ParameterizedTest
    @DisplayName("Cholesterol categories assigned correctly")
    @CsvSource({
        "150, Optimal",
        "199, Optimal",
        "200, Borderline",
        "220, Borderline",
        "239, Borderline",
        "240, High",
        "280, High"
    })
    void categorizeCholesterol_variousValues_correctCategory(
            String chol, String expected) {
        assertThat(CategoryUtil.categorizeСholesterol(chol))
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("INVALID cholesterol returns UNKNOWN")
    void categorizeCholesterol_invalid_returnsUnknown() {
        assertThat(CategoryUtil.categorizeСholesterol("INVALID"))
            .isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Null cholesterol returns UNKNOWN")
    void categorizeCholesterol_null_returnsUnknown() {
        assertThat(CategoryUtil.categorizeСholesterol(null))
            .isEqualTo("UNKNOWN");
    }

    // ── Age Group tests ───────────────────────────────────

    @Test
    @DisplayName("Age 8 categorized as Child")
    void categorizeAge_child_returnsChild() {
        // 8 years ago
        String dob = java.time.LocalDate.now()
            .minusYears(8).toString();
        assertThat(CategoryUtil.categorizeAge(dob))
            .isEqualTo("Child");
    }

    @Test
    @DisplayName("Age 15 categorized as Teenager")
    void categorizeAge_teenager_returnsTeen() {
        String dob = java.time.LocalDate.now()
            .minusYears(15).toString();
        assertThat(CategoryUtil.categorizeAge(dob))
            .isEqualTo("Teenager");
    }

    @Test
    @DisplayName("Age 35 categorized as Adult")
    void categorizeAge_adult_returnsAdult() {
        String dob = java.time.LocalDate.now()
            .minusYears(35).toString();
        assertThat(CategoryUtil.categorizeAge(dob))
            .isEqualTo("Adult");
    }

    @Test
    @DisplayName("Age 65 categorized as Senior")
    void categorizeAge_senior_returnsSenior() {
        String dob = java.time.LocalDate.now()
            .minusYears(65).toString();
        assertThat(CategoryUtil.categorizeAge(dob))
            .isEqualTo("Senior");
    }

    @Test
    @DisplayName("INVALID dob returns UNKNOWN age group")
    void categorizeAge_invalid_returnsUnknown() {
        assertThat(CategoryUtil.categorizeAge("INVALID"))
            .isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Null dob returns UNKNOWN age group")
    void categorizeAge_null_returnsUnknown() {
        assertThat(CategoryUtil.categorizeAge(null))
            .isEqualTo("UNKNOWN");
    }
}