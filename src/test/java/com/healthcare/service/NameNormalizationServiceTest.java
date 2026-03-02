package com.healthcare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.healthcare.model.PatientRawRecord;

class NameNormalizationServiceTest {

    private NameNormalizationService service;

    @BeforeEach
    void setUp() {
        service = new NameNormalizationService();
    }

    // ── Helper ────────────────────────────────────────────

    private PatientRawRecord buildRecord(
            int row, String firstName, String lastName) {
        PatientRawRecord r = new PatientRawRecord();
        r.setSheetRowNumber(row);
        r.setRawFirstName(firstName);
        r.setRawLastName(lastName);
        r.setRawEmail("test@test.com");
        r.setRawPhone("9876543210");
        return r;
    }

    // ── Tests ─────────────────────────────────────────────

    @Test
    @DisplayName("Lowercase names are title-cased")
    void normalizeNames_lowercase_corrected() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "jonathan", "brown"));

        // ACT
        service.normalizeNames(records);

        // ASSERT
        assertThat(records.get(0).getRawFirstName())
            .isEqualTo("Jonathan");
        assertThat(records.get(0).getRawLastName())
            .isEqualTo("Brown");
    }

    @Test
    @DisplayName("Uppercase names are title-cased")
    void normalizeNames_uppercase_corrected() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "SHARON", "HERNANDEZ"));

        // ACT
        service.normalizeNames(records);

        // ASSERT
        assertThat(records.get(0).getRawFirstName())
            .isEqualTo("Sharon");
        assertThat(records.get(0).getRawLastName())
            .isEqualTo("Hernandez");
    }

    @Test
    @DisplayName("Names with spaces are trimmed and title-cased")
    void normalizeNames_withSpaces_trimmedAndCorrected() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "  michael  ", "  green  "));

        // ACT
        service.normalizeNames(records);

        // ASSERT
        assertThat(records.get(0).getRawFirstName())
            .isEqualTo("Michael");
        assertThat(records.get(0).getRawLastName())
            .isEqualTo("Green");
    }

    @Test
    @DisplayName("Already correct names are unchanged")
    void normalizeNames_alreadyCorrect_unchanged() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "John", "Smith"));

        // ACT
        service.normalizeNames(records);

        // ASSERT
        assertThat(records.get(0).getRawFirstName())
            .isEqualTo("John");
        assertThat(records.get(0).getRawLastName())
            .isEqualTo("Smith");
    }

    @Test
    @DisplayName("All records in list are normalized")
    void normalizeNames_multipleRecords_allNormalized() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "john",   "smith"));
        records.add(buildRecord(3, "JANE",   "DOE"));
        records.add(buildRecord(4, "  james", "BROWN"));

        // ACT
        service.normalizeNames(records);

        // ASSERT
        assertThat(records.get(0).getRawFirstName()).isEqualTo("John");
        assertThat(records.get(1).getRawFirstName()).isEqualTo("Jane");
        assertThat(records.get(2).getRawFirstName()).isEqualTo("James");
    }

    @Test
    @DisplayName("Empty list returns empty list")
    void normalizeNames_emptyList_returnsEmpty() {

        List<PatientRawRecord> records = new ArrayList<>();
        List<PatientRawRecord> result = 
            service.normalizeNames(records);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Null input throws NullPointerException")
    void normalizeNames_nullInput_throwsException() {

        assertThatThrownBy(() ->
            service.normalizeNames(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Returns same list — not a new list")
    void normalizeNames_returnsSameListReference() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "john", "smith"));

        // ACT
        List<PatientRawRecord> result = 
            service.normalizeNames(records);

        // ASSERT — must be the exact same object
        assertThat(result).isSameAs(records);
    }
}
