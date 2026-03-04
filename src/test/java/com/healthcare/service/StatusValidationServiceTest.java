package com.healthcare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.healthcare.model.PatientRawRecord;

class StatusValidationServiceTest {

    private StatusValidationService service;

    @BeforeEach
    void setUp() {
        service = new StatusValidationService();
    }

    // ── Helper ────────────────────────────────────────────

    private PatientRawRecord buildRecord(
            String gender,      String bloodGroup,
            String department,  String admStatus,
            String payStatus,   String insStatus,
            String patStatus) {

        PatientRawRecord r = new PatientRawRecord();
        r.setSheetRowNumber(2);
        r.setRawFirstName("John");
        r.setRawLastName("Smith");
        r.setRawPhone("9876543210");
        r.setRawEmail("john@test.com");
        r.setRawGender(gender);
        r.setRawBloodGroup(bloodGroup);
        r.setRawDepartment(department);
        r.setRawAdmissionStatus(admStatus);
        r.setRawPaymentStatus(payStatus);
        r.setRawInsuranceStatus(insStatus);
        r.setRawPatientStatus(patStatus);
        return r;
    }

    // ── Tests ─────────────────────────────────────────────

    @Test
    @DisplayName("All valid fields — standardized correctly")
    void validateStatuses_allValid_standardized() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "MALE", "a+", "CARDIOLOGY",
            "admitted", "piad",
            "actvie", "STABLE"));

        // ACT
        service.validateStatuses(records);

        // ASSERT
        PatientRawRecord r = records.get(0);
        assertThat(r.getRawGender()).isEqualTo("Male");
        assertThat(r.getRawBloodGroup()).isEqualTo("A+");
        assertThat(r.getRawDepartment()).isEqualTo("Cardiology");
        assertThat(r.getRawAdmissionStatus()).isEqualTo("Admitted");
        assertThat(r.getRawPaymentStatus()).isEqualTo("Paid");
        assertThat(r.getRawInsuranceStatus()).isEqualTo("Active");
        assertThat(r.getRawPatientStatus()).isEqualTo("Stable");
    }

    @Test
    @DisplayName("Unknown values become UNKNOWN")
    void validateStatuses_unknownValues_becomeUnknown() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "?", "XXXX", "XXXX",
            "xyz", "xyz",
            "xyz", "xyz"));

        // ACT
        service.validateStatuses(records);

        // ASSERT
        PatientRawRecord r = records.get(0);
        assertThat(r.getRawGender()).isEqualTo("UNKNOWN");
        assertThat(r.getRawBloodGroup()).isEqualTo("UNKNOWN");
        assertThat(r.getRawDepartment()).isEqualTo("UNKNOWN");
        assertThat(r.getRawAdmissionStatus()).isEqualTo("UNKNOWN");
        assertThat(r.getRawPaymentStatus()).isEqualTo("UNKNOWN");
        assertThat(r.getRawInsuranceStatus()).isEqualTo("UNKNOWN");
        assertThat(r.getRawPatientStatus()).isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Blank values become UNKNOWN")
    void validateStatuses_blankValues_becomeUnknown() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "", "", "",
            "", "", "", ""));

        // ACT
        service.validateStatuses(records);

        // ASSERT
        PatientRawRecord r = records.get(0);
        assertThat(r.getRawGender()).isEqualTo("UNKNOWN");
        assertThat(r.getRawBloodGroup()).isEqualTo("UNKNOWN");
        assertThat(r.getRawDepartment()).isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Already correct values unchanged")
    void validateStatuses_alreadyCorrect_unchanged() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "Male", "A+", "Cardiology",
            "Admitted", "Paid",
            "Active", "Stable"));

        // ACT
        service.validateStatuses(records);

        // ASSERT
        PatientRawRecord r = records.get(0);
        assertThat(r.getRawGender()).isEqualTo("Male");
        assertThat(r.getRawBloodGroup()).isEqualTo("A+");
        assertThat(r.getRawDepartment()).isEqualTo("Cardiology");
    }

    @Test
    @DisplayName("Empty list returns empty")
    void validateStatuses_emptyList_returnsEmpty() {

        List<PatientRawRecord> records = new ArrayList<>();
        assertThat(service.validateStatuses(records)).isEmpty();
    }

    @Test
    @DisplayName("Null input throws NullPointerException")
    void validateStatuses_nullInput_throwsException() {

        assertThatThrownBy(() ->
            service.validateStatuses(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Returns same list reference")
    void validateStatuses_returnsSameListReference() {

        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "Male", "A+", "Cardiology",
            "Admitted", "Paid",
            "Active", "Stable"));

        assertThat(service.validateStatuses(records))
            .isSameAs(records);
    }
}