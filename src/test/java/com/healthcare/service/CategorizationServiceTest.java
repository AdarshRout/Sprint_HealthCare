package com.healthcare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.healthcare.model.PatientRawRecord;

class CategorizationServiceTest {

    private CategorizationService service;

    @BeforeEach
    void setUp() {
        service = new CategorizationService();
    }

    // ── Helper ────────────────────────────────────────────

    private PatientRawRecord buildRecord(
            String bmi,         String bloodSugar,
            String cholesterol, String dob) {

        PatientRawRecord r = new PatientRawRecord();
        r.setSheetRowNumber(2);
        r.setRawFirstName("John");
        r.setRawLastName("Smith");
        r.setRawPhone("9876543210");
        r.setRawEmail("john@test.com");
        r.setBmi(bmi);
        r.setRawBloodSugar(bloodSugar);
        r.setRawCholesterol(cholesterol);
        r.setRawDob(dob);
        return r;
    }

    // ── Tests ─────────────────────────────────────────────

    @Test
    @DisplayName("All valid fields — all categories assigned")
    void categorize_allValid_allCategoriesAssigned() {

        // ARRANGE
        String dob = java.time.LocalDate.now()
            .minusYears(35).toString();

        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "22.86", "95", "185", dob));

        // ACT
        service.categorize(records);

        // ASSERT
        PatientRawRecord r = records.get(0);
        assertThat(r.getBmiCategory())
            .isEqualTo("Normal");
        assertThat(r.getBloodSugarCategory())
            .isEqualTo("Normal");
        assertThat(r.getCholesterolCategory())
            .isEqualTo("Optimal");
        assertThat(r.getAgeGroup())
            .isEqualTo("Adult");
    }

    @Test
    @DisplayName("INVALID BMI produces UNKNOWN category")
    void categorize_invalidBmi_unknownCategory() {

        String dob = java.time.LocalDate.now()
            .minusYears(35).toString();

        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "INVALID", "95", "185", dob));

        service.categorize(records);

        assertThat(records.get(0).getBmiCategory())
            .isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Diabetic blood sugar categorized correctly")
    void categorize_diabeticSugar_diabeticCategory() {

        String dob = java.time.LocalDate.now()
            .minusYears(35).toString();

        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "22.86", "210", "185", dob));

        service.categorize(records);

        assertThat(records.get(0).getBloodSugarCategory())
            .isEqualTo("Diabetic");
    }

    @Test
    @DisplayName("High cholesterol categorized correctly")
    void categorize_highCholesterol_highCategory() {

        String dob = java.time.LocalDate.now()
            .minusYears(35).toString();

        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "22.86", "95", "260", dob));

        service.categorize(records);

        assertThat(records.get(0).getCholesterolCategory())
            .isEqualTo("High");
    }

    @Test
    @DisplayName("All INVALID fields — all categories UNKNOWN")
    void categorize_allInvalid_allUnknown() {

        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "INVALID", "INVALID", "INVALID", "INVALID"));

        service.categorize(records);

        PatientRawRecord r = records.get(0);
        assertThat(r.getBmiCategory()).isEqualTo("UNKNOWN");
        assertThat(r.getBloodSugarCategory())
            .isEqualTo("UNKNOWN");
        assertThat(r.getCholesterolCategory())
            .isEqualTo("UNKNOWN");
        assertThat(r.getAgeGroup()).isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Empty list returns empty")
    void categorize_emptyList_returnsEmpty() {
        assertThat(service.categorize(new ArrayList<>()))
            .isEmpty();
    }

    @Test
    @DisplayName("Null input throws NullPointerException")
    void categorize_nullInput_throwsException() {
        assertThatThrownBy(() ->
            service.categorize(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Returns same list reference")
    void categorize_returnsSameListReference() {

        String dob = java.time.LocalDate.now()
            .minusYears(35).toString();

        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "22.86", "95", "185", dob));

        assertThat(service.categorize(records))
            .isSameAs(records);
    }
}