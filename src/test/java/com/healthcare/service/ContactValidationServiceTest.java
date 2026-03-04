package com.healthcare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.healthcare.model.PatientRawRecord;

class ContactValidationServiceTest {

    private ContactValidationService service;

    @BeforeEach
    void setUp() {
        service = new ContactValidationService();
    }

    // ── Helper ────────────────────────────────────────────

    private PatientRawRecord buildRecord(
            int row, String phone, String email) {
        PatientRawRecord r = new PatientRawRecord();
        r.setSheetRowNumber(row);
        r.setRawFirstName("John");
        r.setRawLastName("Smith");
        r.setRawPhone(phone);
        r.setRawEmail(email);
        return r;
    }

    // ── Tests ─────────────────────────────────────────────

    @Test
    @DisplayName("Valid phone and email — unchanged")
    void validateContacts_validBoth_unchanged() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2,
            "9876543210", "john@gmail.com"));

        // ACT
        service.validateContacts(records);

        // ASSERT
        assertThat(records.get(0).getRawPhone())
            .isEqualTo("9876543210");
        assertThat(records.get(0).getRawEmail())
            .isEqualTo("john@gmail.com");
    }

    @Test
    @DisplayName("Invalid phone marked INVALID")
    void validateContacts_invalidPhone_markedInvalid() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2,
            "123", "john@gmail.com"));

        // ACT
        service.validateContacts(records);

        // ASSERT
        assertThat(records.get(0).getRawPhone())
            .isEqualTo("INVALID");
        assertThat(records.get(0).getRawEmail())
            .isEqualTo("john@gmail.com");
    }

    @Test
    @DisplayName("Invalid email marked INVALID")
    void validateContacts_invalidEmail_markedInvalid() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2,
            "9876543210", "thomasmitchell"));

        // ACT
        service.validateContacts(records);

        // ASSERT
        assertThat(records.get(0).getRawPhone())
            .isEqualTo("9876543210");
        assertThat(records.get(0).getRawEmail())
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Uppercase email lowercased")
    void validateContacts_uppercaseEmail_lowercased() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2,
            "9876543210", "JOHN@GMAIL.COM"));

        // ACT
        service.validateContacts(records);

        // ASSERT
        assertThat(records.get(0).getRawEmail())
            .isEqualTo("john@gmail.com");
    }

    @Test
    @DisplayName("Both invalid — both marked INVALID")
    void validateContacts_bothInvalid_bothMarked() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2,
            "INVALID", "not-an-email"));

        // ACT
        service.validateContacts(records);

        // ASSERT
        assertThat(records.get(0).getRawPhone())
            .isEqualTo("INVALID");
        assertThat(records.get(0).getRawEmail())
            .isEqualTo("INVALID");
    }

    @Test
    @DisplayName("Multiple records all validated")
    void validateContacts_multipleRecords_allValidated() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2,
            "9876543210", "john@gmail.com"));
        records.add(buildRecord(3,
            "123", "not-an-email"));
        records.add(buildRecord(4,
            "8899001122", "JANE@YAHOO.COM"));

        // ACT
        service.validateContacts(records);

        // ASSERT
        assertThat(records.get(0).getRawPhone())
            .isEqualTo("9876543210");
        assertThat(records.get(1).getRawPhone())
            .isEqualTo("INVALID");
        assertThat(records.get(1).getRawEmail())
            .isEqualTo("INVALID");
        assertThat(records.get(2).getRawEmail())
            .isEqualTo("jane@yahoo.com");
    }

    @Test
    @DisplayName("Empty list returns empty")
    void validateContacts_emptyList_returnsEmpty() {

        List<PatientRawRecord> records = new ArrayList<>();
        assertThat(service.validateContacts(records))
            .isEmpty();
    }

    @Test
    @DisplayName("Null input throws NullPointerException")
    void validateContacts_nullInput_throwsException() {

        assertThatThrownBy(() ->
            service.validateContacts(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Returns same list reference")
    void validateContacts_returnsSameListReference() {

        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(2,
            "9876543210", "john@gmail.com"));

        assertThat(service.validateContacts(records))
            .isSameAs(records);
    }
}