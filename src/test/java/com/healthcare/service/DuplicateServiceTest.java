package com.healthcare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.healthcare.model.PatientRawRecord;

class DuplicateServiceTest {

    private DuplicateService service;

    @BeforeEach
    void setUp() {
        service = new DuplicateService();
    }

    // ── Helper ────────────────────────────────────────────

    /**
     * Creates a minimal PatientRawRecord for testing.
     * Only sets the fields used in fingerprint.
     */
    private PatientRawRecord buildRecord(
            int row,
            String firstName,
            String lastName,
            String dob,
            String phone) {

        PatientRawRecord r = new PatientRawRecord();
        r.setSheetRowNumber(row);
        r.setRawFirstName(firstName);
        r.setRawLastName(lastName);
        r.setRawDob(dob);
        r.setRawPhone(phone);
        r.setRawEmail("test@test.com");
        return r;
    }

    // ── Tests ─────────────────────────────────────────────

    @Test
    @DisplayName("No duplicates — all records returned")
    void removeDuplicates_noDuplicates_returnsAll() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "John",  "Smith", "1990-01-01", "9876543210"));
        records.add(buildRecord(3, "Jane",  "Doe",   "1985-05-15", "9123456789"));
        records.add(buildRecord(4, "James", "Brown", "1972-08-22", "8899001122"));

        // ACT
        List<PatientRawRecord> result = 
            service.removeDuplicates(records);

        // ASSERT
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("Exact duplicate removed — only first kept")
    void removeDuplicates_exactDuplicate_removesSecond() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "John", "Smith", "1990-01-01", "9876543210"));
        records.add(buildRecord(3, "John", "Smith", "1990-01-01", "9876543210"));

        // ACT
        List<PatientRawRecord> result = 
            service.removeDuplicates(records);

        // ASSERT
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSheetRowNumber()).isEqualTo(2);
    }

    @Test
    @DisplayName("Case difference in name treated as duplicate")
    void removeDuplicates_caseVariant_treatedAsDuplicate() {

        // ARRANGE — same person, different name casing
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "john",  "smith", "1990-01-01", "9876543210"));
        records.add(buildRecord(3, "JOHN",  "SMITH", "1990-01-01", "9876543210"));
        records.add(buildRecord(4, "John",  "Smith", "1990-01-01", "9876543210"));

        // ACT
        List<PatientRawRecord> result = 
            service.removeDuplicates(records);

        // ASSERT
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Same name and DOB but different phone — NOT duplicate")
    void removeDuplicates_differentPhone_notDuplicate() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "John", "Smith", "1990-01-01", "9876543210"));
        records.add(buildRecord(3, "John", "Smith", "1990-01-01", "9111111111"));

        // ACT
        List<PatientRawRecord> result = 
            service.removeDuplicates(records);

        // ASSERT
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Empty list returns empty list")
    void removeDuplicates_emptyList_returnsEmpty() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();

        // ACT
        List<PatientRawRecord> result = 
            service.removeDuplicates(records);

        // ASSERT
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Null input throws NullPointerException")
    void removeDuplicates_nullInput_throwsException() {

        assertThatThrownBy(() -> 
            service.removeDuplicates(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Original list is not modified")
    void removeDuplicates_doesNotModifyOriginalList() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "John", "Smith", "1990-01-01", "9876543210"));
        records.add(buildRecord(3, "John", "Smith", "1990-01-01", "9876543210"));
        int originalSize = records.size();

        // ACT
        service.removeDuplicates(records);

        // ASSERT — original list must be untouched
        assertThat(records).hasSize(originalSize);
    }

    @Test
    @DisplayName("Multiple duplicates — only unique records kept")
    void removeDuplicates_multipleDuplicates_keepsUnique() {

        // ARRANGE — 5 records, 2 unique
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2, "John", "Smith", "1990-01-01", "9876543210"));
        records.add(buildRecord(3, "Jane", "Doe",   "1985-05-15", "9123456789"));
        records.add(buildRecord(4, "john", "smith", "1990-01-01", "9876543210"));
        records.add(buildRecord(5, "JANE", "DOE",   "1985-05-15", "9123456789"));
        records.add(buildRecord(6, "John", "Smith", "1990-01-01", "9876543210"));

        // ACT
        List<PatientRawRecord> result = 
            service.removeDuplicates(records);

        // ASSERT
        assertThat(result).hasSize(2);
    }
}