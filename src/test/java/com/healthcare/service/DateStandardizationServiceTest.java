package com.healthcare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.healthcare.model.PatientRawRecord;

class DateStandardizationServiceTest {

    private DateStandardizationService service;

    @BeforeEach
    void setUp() {
        service = new DateStandardizationService();
    }

    // ── Helper ────────────────────────────────────────────

    private PatientRawRecord buildRecord(
            String dob,
            String admissionDate,
            String dischargeDate) {

        PatientRawRecord r = new PatientRawRecord();
        r.setSheetRowNumber(2);
        r.setRawFirstName("John");
        r.setRawLastName("Smith");
        r.setRawPhone("9876543210");
        r.setRawEmail("john@test.com");
        r.setRawDob(dob);
        r.setRawAdmissionDate(admissionDate);
        r.setRawDischargeDate(dischargeDate);
        return r;
    }

    // ── Tests ─────────────────────────────────────────────

    @Test
    @DisplayName("All dates already ISO — nothing changes")
    void standardizeDates_alreadyIso_unchanged() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "1985-04-12", "2024-01-10", "2024-01-15"));

        // ACT
        service.standardizeDates(records);

        // ASSERT
        PatientRawRecord r = records.get(0);
        assertThat(r.getRawDob()).isEqualTo("1985-04-12");
        assertThat(r.getRawAdmissionDate()).isEqualTo("2024-01-10");
        assertThat(r.getRawDischargeDate()).isEqualTo("2024-01-15");
    }

    @Test
    @DisplayName("Dot format dob standardized to ISO")
    void standardizeDates_dotFormat_standardized() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "22.05.1970", "2024-01-10", "2024-01-15"));

        // ACT
        service.standardizeDates(records);

        // ASSERT
        assertThat(records.get(0).getRawDob())
            .isEqualTo("1970-05-22");
    }

    @Test
    @DisplayName("Month name format standardized to ISO")
    void standardizeDates_monthNameFormat_standardized() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "Apr 25 1983", "14 Aug 2022", "20 Aug 2022"));

        // ACT
        service.standardizeDates(records);

        // ASSERT
        PatientRawRecord r = records.get(0);
        assertThat(r.getRawDob()).isEqualTo("1983-04-25");
        assertThat(r.getRawAdmissionDate()).isEqualTo("2022-08-14");
        assertThat(r.getRawDischargeDate()).isEqualTo("2022-08-20");
    }

    @Test
    @DisplayName("Invalid date string becomes INVALID")
    void standardizeDates_invalidDate_markedInvalid() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "not-a-date", "2024-01-10", "2024-01-15"));

        // ACT
        service.standardizeDates(records);

        // ASSERT
        assertThat(records.get(0).getRawDob())
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("All three date fields fixed in one record")
    void standardizeDates_allThreeFields_allFixed() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "19900304", "14 Aug 2022", "20.08.2022"));

        // ACT
        service.standardizeDates(records);

        // ASSERT
        PatientRawRecord r = records.get(0);
        assertThat(r.getRawDob()).isEqualTo("1990-03-04");
        assertThat(r.getRawAdmissionDate()).isEqualTo("2022-08-14");
        assertThat(r.getRawDischargeDate()).isEqualTo("2022-08-20");
    }

    @Test
    @DisplayName("Empty list returns empty")
    void standardizeDates_emptyList_returnsEmpty() {

        List<PatientRawRecord> records = new ArrayList<>();
        assertThat(service.standardizeDates(records)).isEmpty();
    }

    @Test
    @DisplayName("Null input throws NullPointerException")
    void standardizeDates_nullInput_throwsException() {

        assertThatThrownBy(() ->
            service.standardizeDates(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Returns same list reference")
    void standardizeDates_returnsSameListReference() {

        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "1985-04-12", "2024-01-10", "2024-01-15"));

        assertThat(service.standardizeDates(records))
            .isSameAs(records);
    }
}