package com.healthcare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.healthcare.model.PatientRawRecord;

class DiagnosisMappingServiceTest {

    private DiagnosisMappingService service;

    @BeforeEach
    void setUp() {
        service = new DiagnosisMappingService();
    }

    // ── Helper ────────────────────────────────────────────

    private PatientRawRecord buildRecord(
            int row, String diagnosisCode) {
        PatientRawRecord r = new PatientRawRecord();
        r.setSheetRowNumber(row);
        r.setRawFirstName("John");
        r.setRawLastName("Smith");
        r.setRawPhone("9876543210");
        r.setRawEmail("john@test.com");
        r.setRawDiagnosis(diagnosisCode);
        return r;
    }

    // ── Tests ─────────────────────────────────────────────

    @Test
    @DisplayName("Known code mapped to disease name")
    void mapDiagnosis_knownCode_mappedToDisease() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "I10"));

        // ACT
        service.mapDiagnosis(records);

        // ASSERT
        assertThat(records.get(0).getRawDiagnosis())
            .isEqualTo("Essential (primary) hypertension");
    }

    @Test
    @DisplayName("Unknown code becomes UNKNOWN")
    void mapDiagnosis_unknownCode_becomesUnknown() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "ZZZ999"));

        // ACT
        service.mapDiagnosis(records);

        // ASSERT
        assertThat(records.get(0).getRawDiagnosis())
            .isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Lowercase code still maps correctly")
    void mapDiagnosis_lowercaseCode_mappedCorrectly() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "i10"));

        // ACT
        service.mapDiagnosis(records);

        // ASSERT
        assertThat(records.get(0).getRawDiagnosis())
            .isEqualTo("Essential (primary) hypertension");
    }

    @Test
    @DisplayName("Multiple records all mapped correctly")
    void mapDiagnosis_multipleRecords_allMapped() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "I10"));
        records.add(buildRecord(3, "ZZZ"));
        records.add(buildRecord(4, "i10"));

        // ACT
        service.mapDiagnosis(records);

        // ASSERT
        assertThat(records.get(0).getRawDiagnosis())
            .isEqualTo("Essential (primary) hypertension");
        assertThat(records.get(1).getRawDiagnosis())
            .isEqualTo("UNKNOWN");
        assertThat(records.get(2).getRawDiagnosis())
            .isEqualTo("Essential (primary) hypertension");
    }

    @Test
    @DisplayName("Empty list returns empty")
    void mapDiagnosis_emptyList_returnsEmpty() {

        List<PatientRawRecord> records = new ArrayList<>();
        assertThat(service.mapDiagnosis(records)).isEmpty();
    }

    @Test
    @DisplayName("Null input throws NullPointerException")
    void mapDiagnosis_nullInput_throwsException() {

        assertThatThrownBy(() ->
            service.mapDiagnosis(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Returns same list reference")
    void mapDiagnosis_returnsSameListReference() {

        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "I10"));

        assertThat(service.mapDiagnosis(records))
            .isSameAs(records);
    }
}
