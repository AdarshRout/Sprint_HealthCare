package com.healthcare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.healthcare.model.AggregationResult;
import com.healthcare.model.PatientRawRecord;

class AggregationServiceTest {

    private AggregationService service;

    @BeforeEach
    void setUp() {
        service = new AggregationService();
    }

    // ── Helper ────────────────────────────────────────────

    private PatientRawRecord buildRecord(
            String dept,      String doctorId,
            String diagnosis, String bill,
            String los,       String bloodSugar) {

        PatientRawRecord r = new PatientRawRecord();
        r.setSheetRowNumber(2);
        r.setRawFirstName("John");
        r.setRawLastName("Smith");
        r.setRawPhone("9876543210");
        r.setRawEmail("john@test.com");
        r.setRawDepartment(dept);
        r.setRawDoctorId(doctorId);
        r.setRawDiagnosis(diagnosis);
        r.setRawBillAmount(bill);
        r.setLengthOfStayDays(los);
        r.setRawBloodSugar(bloodSugar);
        return r;
    }

    // ── aggregateByDepartment tests ───────────────────────

    @Test
    @DisplayName("Two records same dept — grouped into one result")
    void aggregateByDepartment_twoSameDept_oneGroup() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "Cardiology", "D101",
            "Heart Failure", "5000", "3", "95"));
        records.add(buildRecord(
            "Cardiology", "D101",
            "Heart Failure", "7000", "5", "110"));

        // ACT
        List<AggregationResult> results =
            service.aggregateByDepartment(records);

        // ASSERT
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getGroupKey())
            .isEqualTo("Cardiology");
        assertThat(results.get(0).getTotalPatients())
            .isEqualTo(2);
    }

    @Test
    @DisplayName("Two different depts — two groups")
    void aggregateByDepartment_twoDiffDepts_twoGroups() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "Cardiology", "D101",
            "Heart Failure", "5000", "3", "95"));
        records.add(buildRecord(
            "Neurology", "D202",
            "Migraine", "3000", "2", "88"));

        // ACT
        List<AggregationResult> results =
            service.aggregateByDepartment(records);

        // ASSERT
        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("Avg bill calculated correctly")
    void aggregateByDepartment_avgBill_calculatedCorrectly() {

        // ARRANGE — bills 4000 and 6000 → avg 5000
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "Cardiology", "D101",
            "Heart Failure", "4000", "3", "95"));
        records.add(buildRecord(
            "Cardiology", "D101",
            "Heart Failure", "6000", "5", "110"));

        // ACT
        List<AggregationResult> results =
            service.aggregateByDepartment(records);

        // ASSERT
        assertThat(results.get(0).getAvgBillAmount())
            .isEqualTo(5000.0);
    }

    @Test
    @DisplayName("Avg LOS calculated correctly")
    void aggregateByDepartment_avgLos_calculatedCorrectly() {

        // ARRANGE — LOS 4 and 6 → avg 5
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "Cardiology", "D101",
            "Heart Failure", "5000", "4", "95"));
        records.add(buildRecord(
            "Cardiology", "D101",
            "Heart Failure", "5000", "6", "110"));

        // ACT
        List<AggregationResult> results =
            service.aggregateByDepartment(records);

        // ASSERT
        assertThat(results.get(0).getAvgLengthOfStay())
            .isEqualTo(5.0);
    }

    @Test
    @DisplayName("UNKNOWN department records are skipped")
    void aggregateByDepartment_unknownDept_skipped() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "UNKNOWN", "D101",
            "Heart Failure", "5000", "3", "95"));
        records.add(buildRecord(
            "Cardiology", "D101",
            "Heart Failure", "5000", "3", "95"));

        // ACT
        List<AggregationResult> results =
            service.aggregateByDepartment(records);

        // ASSERT — only Cardiology group, UNKNOWN skipped
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getGroupKey())
            .isEqualTo("Cardiology");
    }

    @Test
    @DisplayName("INVALID bill excluded from avg calculation")
    void aggregateByDepartment_invalidBill_excludedFromAvg() {

        // ARRANGE — one valid bill 6000, one INVALID
        // avg should be 6000 not 3000
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "Cardiology", "D101",
            "Heart Failure", "6000", "3", "95"));
        records.add(buildRecord(
            "Cardiology", "D101",
            "Heart Failure", "INVALID", "3", "95"));

        // ACT
        List<AggregationResult> results =
            service.aggregateByDepartment(records);

        // ASSERT
        assertThat(results.get(0).getAvgBillAmount())
            .isEqualTo(6000.0);
    }

    // ── aggregateByDoctor tests ───────────────────────────

    @Test
    @DisplayName("Records grouped by doctor correctly")
    void aggregateByDoctor_twoSameDoctor_oneGroup() {

        // ARRANGE
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "Cardiology", "D101",
            "Heart Failure", "5000", "3", "95"));
        records.add(buildRecord(
            "Neurology", "D101",
            "Migraine", "3000", "2", "88"));

        // ACT
        List<AggregationResult> results =
            service.aggregateByDoctor(records);

        // ASSERT
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTotalPatients())
            .isEqualTo(2);
    }

    @Test
    @DisplayName("Group type is DOCTOR")
    void aggregateByDoctor_groupType_isDoctor() {

        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "Cardiology", "D101",
            "Heart Failure", "5000", "3", "95"));

        List<AggregationResult> results =
            service.aggregateByDoctor(records);

        assertThat(results.get(0).getGroupType())
            .isEqualTo("DOCTOR");
    }

    // ── aggregateByDiagnosis tests ────────────────────────

    @Test
    @DisplayName("Avg blood sugar calculated correctly")
    void aggregateByDiagnosis_avgBloodSugar_correct() {

        // ARRANGE — blood sugar 90 and 110 → avg 100
        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "Cardiology", "D101",
            "Heart Failure", "5000", "3", "90"));
        records.add(buildRecord(
            "Cardiology", "D101",
            "Heart Failure", "5000", "3", "110"));

        // ACT
        List<AggregationResult> results =
            service.aggregateByDiagnosis(records);

        // ASSERT
        assertThat(results.get(0).getAvgBloodSugar())
            .isEqualTo(100.0);
    }

    @Test
    @DisplayName("UNKNOWN diagnosis records skipped")
    void aggregateByDiagnosis_unknownDiagnosis_skipped() {

        List<PatientRawRecord> records = new ArrayList<>();
        records.add(buildRecord(
            "Cardiology", "D101",
            "UNKNOWN", "5000", "3", "95"));

        List<AggregationResult> results =
            service.aggregateByDiagnosis(records);

        assertThat(results).isEmpty();
    }

    // ── Null / empty input tests ──────────────────────────

    @Test
    @DisplayName("Empty list returns empty results")
    void aggregateByDepartment_emptyList_returnsEmpty() {
        assertThat(service.aggregateByDepartment(
            new ArrayList<>())).isEmpty();
    }

    @Test
    @DisplayName("Null input throws NullPointerException")
    void aggregateByDepartment_nullInput_throwsException() {
        assertThatThrownBy(() ->
            service.aggregateByDepartment(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Null input for doctor throws NullPointerException")
    void aggregateByDoctor_nullInput_throwsException() {
        assertThatThrownBy(() ->
            service.aggregateByDoctor(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Null input for diagnosis throws NullPointerException")
    void aggregateByDiagnosis_nullInput_throwsException() {
        assertThatThrownBy(() ->
            service.aggregateByDiagnosis(null))
            .isInstanceOf(NullPointerException.class);
    }
}