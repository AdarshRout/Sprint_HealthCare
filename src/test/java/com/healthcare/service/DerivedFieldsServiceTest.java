package com.healthcare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.healthcare.model.PatientRawRecord;

class DerivedFieldsServiceTest {

    private DerivedFieldsService service;

    @BeforeEach
    void setUp() {
        service = new DerivedFieldsService();
    }

    // ── Helper ────────────────────────────────────────────

    private PatientRawRecord buildRecord(
            String weight,   String height,
            String systolic, String diastolic,
            String admDate,  String disDate) {

        PatientRawRecord r = new PatientRawRecord();
        r.setSheetRowNumber(2);
        r.setRawFirstName("John");
        r.setRawLastName("Smith");
        r.setRawPhone("9876543210");
        r.setRawEmail("john@test.com");
        r.setRawWeightKg(weight);
        r.setRawHeightCm(height);
        r.setRawSystolic(systolic);
        r.setRawDiastolic(diastolic);
        r.setRawAdmissionDate(admDate);
        r.setRawDischargeDate(disDate);
        return r;
    }

    // ── Tests ─────────────────────────────────────────────

    @Test
    @DisplayName("All valid inputs — all three fields calculated")
    void calculateDerivedFields_allValid_allCalculated() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "70", "175",
            "120", "80",
            "2024-01-10", "2024-01-15"));

        // ACT
        service.calculateDerivedFields(records);

        // ASSERT
        PatientRawRecord r = records.get(0);
        assertThat(r.getBmi()).isEqualTo("22.86");
        assertThat(r.getMap()).isEqualTo("93.33");
        assertThat(r.getLengthOfStayDays()).isEqualTo("5");
    }

    @Test
    @DisplayName("INVALID weight produces INVALID BMI")
    void calculateDerivedFields_invalidWeight_invalidBmi() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "INVALID", "175",
            "120", "80",
            "2024-01-10", "2024-01-15"));

        // ACT
        service.calculateDerivedFields(records);

        // ASSERT
        assertThat(records.get(0).getBmi())
            .isEqualTo("INVALID");
        // MAP and LOS should still be valid
        assertThat(records.get(0).getMap())
            .isEqualTo("93.33");
        assertThat(records.get(0).getLengthOfStayDays())
            .isEqualTo("5");
    }

    @Test
    @DisplayName("INVALID dates produce INVALID LOS")
    void calculateDerivedFields_invalidDates_invalidLos() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "70", "175",
            "120", "80",
            "INVALID", "INVALID"));

        // ACT
        service.calculateDerivedFields(records);

        // ASSERT
        assertThat(records.get(0).getLengthOfStayDays())
            .isEqualTo("INVALID");
        // BMI and MAP should still be valid
        assertThat(records.get(0).getBmi())
            .isEqualTo("22.86");
        assertThat(records.get(0).getMap())
            .isEqualTo("93.33");
    }

    @Test
    @DisplayName("All INVALID inputs — all derived fields INVALID")
    void calculateDerivedFields_allInvalid_allInvalid() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "INVALID", "INVALID",
            "INVALID", "INVALID",
            "INVALID", "INVALID"));

        // ACT
        service.calculateDerivedFields(records);

        // ASSERT
        PatientRawRecord r = records.get(0);
        assertThat(r.getBmi()).isEqualTo("INVALID");
        assertThat(r.getMap()).isEqualTo("INVALID");
        assertThat(r.getLengthOfStayDays()).isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Empty list returns empty")
    void calculateDerivedFields_emptyList_returnsEmpty() {

        List<PatientRawRecord> records = new ArrayList<>();
        assertThat(service.calculateDerivedFields(records))
            .isEmpty();
    }

    @Test
    @DisplayName("Null input throws NullPointerException")
    void calculateDerivedFields_nullInput_throwsException() {

        assertThatThrownBy(() ->
            service.calculateDerivedFields(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Returns same list reference")
    void calculateDerivedFields_returnsSameListReference() {

        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "70", "175", "120", "80",
            "2024-01-10", "2024-01-15"));

        assertThat(service.calculateDerivedFields(records))
            .isSameAs(records);
    }
}