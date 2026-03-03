package com.healthcare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.healthcare.model.PatientRawRecord;

class NumericFixServiceTest {

    private NumericFixService service;

    @BeforeEach
    void setUp() {
        service = new NumericFixService();
    }

    // ── Helper ────────────────────────────────────────────

    private PatientRawRecord buildRecord(
            String systolic, String diastolic,
            String weight,   String height,
            String sugar,    String cholesterol,
            String bill) {

        PatientRawRecord r = new PatientRawRecord();
        r.setSheetRowNumber(2);
        r.setRawFirstName("John");
        r.setRawLastName("Smith");
        r.setRawEmail("john@test.com");
        r.setRawPhone("9876543210");
        r.setRawSystolic(systolic);
        r.setRawDiastolic(diastolic);
        r.setRawWeightKg(weight);
        r.setRawHeightCm(height);
        r.setRawBloodSugar(sugar);
        r.setRawCholesterol(cholesterol);
        r.setRawBillAmount(bill);
        return r;
    }

    // ── Tests ─────────────────────────────────────────────

    @Test
    @DisplayName("All valid numeric fields — nothing changes")
    void fixNumericFields_allValid_nothingChanged() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "120", "80", "72.5", "175",
            "95", "185", "4500.00"));

        // ACT
        service.fixNumericFields(records);

        // ASSERT — all fields unchanged
        PatientRawRecord r = records.get(0);
        assertThat(r.getRawSystolic()).isEqualTo("120");
        assertThat(r.getRawDiastolic()).isEqualTo("80");
        assertThat(r.getRawWeightKg()).isEqualTo("72.5");
        assertThat(r.getRawHeightCm()).isEqualTo("175");
        assertThat(r.getRawBloodSugar()).isEqualTo("95");
        assertThat(r.getRawCholesterol()).isEqualTo("185");
        assertThat(r.getRawBillAmount()).isEqualTo("4500.00");
    }

    @Test
    @DisplayName("N/A in systolic becomes INVALID")
    void fixNumericFields_naInSystolic_markedInvalid() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "N/A", "80", "72.5", "175",
            "95", "185", "4500.00"));

        // ACT
        service.fixNumericFields(records);

        // ASSERT
        assertThat(records.get(0).getRawSystolic())
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Negative height becomes INVALID")
    void fixNumericFields_negativeHeight_markedInvalid() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "120", "80", "72.5", "-186",
            "95", "185", "4500.00"));

        // ACT
        service.fixNumericFields(records);

        // ASSERT
        assertThat(records.get(0).getRawHeightCm())
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Out of range systolic becomes INVALID")
    void fixNumericFields_outOfRangeSystolic_markedInvalid() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "900", "80", "72.5", "175",
            "95", "185", "4500.00"));

        // ACT
        service.fixNumericFields(records);

        // ASSERT
        assertThat(records.get(0).getRawSystolic())
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Multiple invalid fields — all marked INVALID")
    void fixNumericFields_multipleInvalid_allMarked() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "N/A", "null", "72.5", "-186",
            "abc", "185", "4500.00"));

        // ACT
        service.fixNumericFields(records);

        // ASSERT
        PatientRawRecord r = records.get(0);
        assertThat(r.getRawSystolic()).isEqualTo("INVALID");
        assertThat(r.getRawDiastolic()).isEqualTo("INVALID");
        assertThat(r.getRawHeightCm()).isEqualTo("INVALID");
        assertThat(r.getRawBloodSugar()).isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Empty list returns empty list")
    void fixNumericFields_emptyList_returnsEmpty() {

        List<PatientRawRecord> records = new ArrayList<>();
        List<PatientRawRecord> result =
            service.fixNumericFields(records);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Null input throws NullPointerException")
    void fixNumericFields_nullInput_throwsException() {

        assertThatThrownBy(() ->
            service.fixNumericFields(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Returns same list reference — in place modification")
    void fixNumericFields_returnsSameListReference() {

        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "120", "80", "72.5", "175",
            "95", "185", "4500.00"));

        List<PatientRawRecord> result =
            service.fixNumericFields(records);

        assertThat(result).isSameAs(records);
    }
}
