package com.healthcare.constant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EnumMappingTest {

    // ── Gender ────────────────────────────────────────────

    @Test
    @DisplayName("Gender: 'M' maps to Male")
    void gender_M_returnsMale() {
        assertThat(Gender.fromRaw("M").getDisplayName())
            .isEqualTo("Male");
    }

    @Test
    @DisplayName("Gender: 'male' maps to Male")
    void gender_male_returnsMale() {
        assertThat(Gender.fromRaw("male").getDisplayName())
            .isEqualTo("Male");
    }

    @Test
    @DisplayName("Gender: 'FEMALE' maps to Female")
    void gender_FEMALE_returnsFemale() {
        assertThat(Gender.fromRaw("FEMALE").getDisplayName())
            .isEqualTo("Female");
    }

    @Test
    @DisplayName("Gender: unknown value returns UNKNOWN")
    void gender_unknown_returnsUnknown() {
        assertThat(Gender.fromRaw("?").getDisplayName())
            .isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Gender: null returns UNKNOWN")
    void gender_null_returnsUnknown() {
        assertThat(Gender.fromRaw(null).getDisplayName())
            .isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Gender: blank returns UNKNOWN")
    void gender_blank_returnsUnknown() {
        assertThat(Gender.fromRaw("").getDisplayName())
            .isEqualTo("UNKNOWN");
    }

    // ── Blood Group ───────────────────────────────────────

    @Test
    @DisplayName("BloodGroup: 'A+' maps to A+")
    void bloodGroup_Apos_returnsApos() {
        assertThat(BloodGroup.fromRaw("A+").getDisplayName())
            .isEqualTo("A+");
    }

    @Test
    @DisplayName("BloodGroup: 'a+' maps to A+")
    void bloodGroup_lowercase_returnsApos() {
        assertThat(BloodGroup.fromRaw("a+").getDisplayName())
            .isEqualTo("A+");
    }

    @Test
    @DisplayName("BloodGroup: 'UNKNOWN' returns UNKNOWN")
    void bloodGroup_unknown_returnsUnknown() {
        assertThat(BloodGroup.fromRaw("UNKNOWN").getDisplayName())
            .isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("BloodGroup: null returns UNKNOWN")
    void bloodGroup_null_returnsUnknown() {
        assertThat(BloodGroup.fromRaw(null).getDisplayName())
            .isEqualTo("UNKNOWN");
    }

    // ── Department ────────────────────────────────────────

    @Test
    @DisplayName("Department: 'CARDIOLOGY' maps to Cardiology")
    void department_CARDIOLOGY_returnsCardiology() {
        assertThat(Department.fromRaw("CARDIOLOGY")
            .getDisplayName())
            .isEqualTo("Cardiology");
    }

    @Test
    @DisplayName("Department: 'CARD' abbreviation maps to Cardiology")
    void department_CARD_returnsCardiology() {
        assertThat(Department.fromRaw("CARD").getDisplayName())
            .isEqualTo("Cardiology");
    }

    @Test
    @DisplayName("Department: 'cardiology' lowercase maps correctly")
    void department_lowercase_returnsCardiology() {
        assertThat(Department.fromRaw("cardiology")
            .getDisplayName())
            .isEqualTo("Cardiology");
    }

    @Test
    @DisplayName("Department: unknown value returns UNKNOWN")
    void department_unknown_returnsUnknown() {
        assertThat(Department.fromRaw("XXXX").getDisplayName())
            .isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("Department: null returns UNKNOWN")
    void department_null_returnsUnknown() {
        assertThat(Department.fromRaw(null).getDisplayName())
            .isEqualTo("UNKNOWN");
    }

    // ── Admission Status ──────────────────────────────────

    @Test
    @DisplayName("AdmissionStatus: 'admitted' maps to Admitted")
    void admissionStatus_admitted_returnsAdmitted() {
        assertThat(AdmissionStatus.fromRaw("admitted")
            .getDisplayName())
            .isEqualTo("Admitted");
    }

    @Test
    @DisplayName("AdmissionStatus: 'admttd' typo maps to Admitted")
    void admissionStatus_typo_returnsAdmitted() {
        assertThat(AdmissionStatus.fromRaw("admttd")
            .getDisplayName())
            .isEqualTo("Admitted");
    }

    @Test
    @DisplayName("AdmissionStatus: 'Out-Patient' maps to Outpatient")
    void admissionStatus_outPatient_returnsOutpatient() {
        assertThat(AdmissionStatus.fromRaw("Out-Patient")
            .getDisplayName())
            .isEqualTo("Outpatient");
    }

    @Test
    @DisplayName("AdmissionStatus: unknown returns UNKNOWN")
    void admissionStatus_unknown_returnsUnknown() {
        assertThat(AdmissionStatus.fromRaw("xyz")
            .getDisplayName())
            .isEqualTo("UNKNOWN");
    }

    // ── Payment Status ────────────────────────────────────

    @Test
    @DisplayName("PaymentStatus: 'piad' typo maps to Paid")
    void paymentStatus_typo_returnsPaid() {
        assertThat(PaymentStatus.fromRaw("piad")
            .getDisplayName())
            .isEqualTo("Paid");
    }

    @Test
    @DisplayName("PaymentStatus: 'PENDING' maps to Pending")
    void paymentStatus_PENDING_returnsPending() {
        assertThat(PaymentStatus.fromRaw("PENDING")
            .getDisplayName())
            .isEqualTo("Pending");
    }

    @Test
    @DisplayName("PaymentStatus: 'waved' typo maps to Waived")
    void paymentStatus_waved_returnsWaived() {
        assertThat(PaymentStatus.fromRaw("waved")
            .getDisplayName())
            .isEqualTo("Waived");
    }

    @Test
    @DisplayName("PaymentStatus: null returns UNKNOWN")
    void paymentStatus_null_returnsUnknown() {
        assertThat(PaymentStatus.fromRaw(null).getDisplayName())
            .isEqualTo("UNKNOWN");
    }

    // ── Insurance Status ──────────────────────────────────

    @Test
    @DisplayName("InsuranceStatus: 'actvie' typo maps to Active")
    void insuranceStatus_typo_returnsActive() {
        assertThat(InsuranceStatus.fromRaw("actvie")
            .getDisplayName())
            .isEqualTo("Active");
    }

    @Test
    @DisplayName("InsuranceStatus: 'under-review' maps to Under Review")
    void insuranceStatus_underReview_returnsUnderReview() {
        assertThat(InsuranceStatus.fromRaw("under-review")
            .getDisplayName())
            .isEqualTo("Under Review");
    }

    @Test
    @DisplayName("InsuranceStatus: 'EXPRD' maps to Expired")
    void insuranceStatus_EXPRD_returnsExpired() {
        assertThat(InsuranceStatus.fromRaw("EXPRD")
            .getDisplayName())
            .isEqualTo("Expired");
    }

    @Test
    @DisplayName("InsuranceStatus: null returns UNKNOWN")
    void insuranceStatus_null_returnsUnknown() {
        assertThat(InsuranceStatus.fromRaw(null).getDisplayName())
            .isEqualTo("UNKNOWN");
    }

    // ── Patient Status ────────────────────────────────────

    @Test
    @DisplayName("PatientStatus: 'CRITICAL' maps to Critical")
    void patientStatus_CRITICAL_returnsCritical() {
        assertThat(PatientStatus.fromRaw("CRITICAL")
            .getDisplayName())
            .isEqualTo("Critical");
    }

    @Test
    @DisplayName("PatientStatus: 'stable' maps to Stable")
    void patientStatus_stable_returnsStable() {
        assertThat(PatientStatus.fromRaw("stable")
            .getDisplayName())
            .isEqualTo("Stable");
    }

    @Test
    @DisplayName("PatientStatus: 'DISCHARGED' maps to Discharged")
    void patientStatus_DISCHARGED_returnsDischarged() {
        assertThat(PatientStatus.fromRaw("DISCHARGED")
            .getDisplayName())
            .isEqualTo("Discharged");
    }

    @Test
    @DisplayName("PatientStatus: unknown returns UNKNOWN")
    void patientStatus_unknown_returnsUnknown() {
        assertThat(PatientStatus.fromRaw("xyz")
            .getDisplayName())
            .isEqualTo("UNKNOWN");
    }
}