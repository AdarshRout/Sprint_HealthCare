package com.healthcare.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.model.AggregationResult;
import com.healthcare.model.PatientRawRecord;
import com.healthcare.util.NumericValidatorUtil;

public class AggregationService {

    private static final Logger LOG =
        LogManager.getLogger(AggregationService.class);

    /**
     * UC-9: Aggregate records by Department.
     * Calculates: total patients, avg bill, avg LOS
     */
    public List<AggregationResult> aggregateByDepartment(
            List<PatientRawRecord> records) {

        Objects.requireNonNull(records,
            "Input records list must not be null");

        LOG.info("UC-9 | Aggregating by Department...");

        // Group by department — skip UNKNOWN/INVALID
        Map<String, List<PatientRawRecord>> grouped =
            records.stream()
                .filter(r -> r != null)
                .filter(r -> isValidKey(r.getRawDepartment()))
                .collect(Collectors.groupingBy(
                    PatientRawRecord::getRawDepartment));

        List<AggregationResult> results =
            grouped.entrySet().stream()
                .map(entry -> {
                    String dept = entry.getKey();
                    List<PatientRawRecord> group =
                        entry.getValue();

                    AggregationResult result =
                        new AggregationResult(
                            dept, "DEPARTMENT");

                    result.setTotalPatients(group.size());
                    result.setAvgBillAmount(
                        avgBill(group));
                    result.setAvgLengthOfStay(
                        avgLos(group));

                    LOG.info("UC-9 | DEPARTMENT | {} | " +
                             "patients={} | avgBill={} " +
                             "| avgLOS={}",
                        dept,
                        result.getTotalPatients(),
                        String.format("%.2f",
                            result.getAvgBillAmount()),
                        String.format("%.2f",
                            result.getAvgLengthOfStay()));

                    return result;
                })
                .collect(Collectors.toList());

        LOG.info("UC-9 | Department aggregation complete. " +
                 "Groups: {}", results.size());
        return results;
    }

    /**
     * UC-9: Aggregate records by Doctor ID.
     * Calculates: total patients, avg bill
     */
    public List<AggregationResult> aggregateByDoctor(
            List<PatientRawRecord> records) {

        Objects.requireNonNull(records,
            "Input records list must not be null");

        LOG.info("UC-9 | Aggregating by Doctor...");

        Map<String, List<PatientRawRecord>> grouped =
            records.stream()
                .filter(r -> r != null)
                .filter(r -> isValidKey(r.getRawDoctorId()))
                .collect(Collectors.groupingBy(
                    PatientRawRecord::getRawDoctorId));

        List<AggregationResult> results =
            grouped.entrySet().stream()
                .map(entry -> {
                    String doctorId = entry.getKey();
                    List<PatientRawRecord> group =
                        entry.getValue();

                    AggregationResult result =
                        new AggregationResult(
                            doctorId, "DOCTOR");

                    result.setTotalPatients(group.size());
                    result.setAvgBillAmount(avgBill(group));

                    LOG.info("UC-9 | DOCTOR | {} | " +
                             "patients={} | avgBill={}",
                        doctorId,
                        result.getTotalPatients(),
                        String.format("%.2f",
                            result.getAvgBillAmount()));

                    return result;
                })
                .collect(Collectors.toList());

        LOG.info("UC-9 | Doctor aggregation complete. " +
                 "Groups: {}", results.size());
        return results;
    }

    /**
     * UC-9: Aggregate records by Diagnosis.
     * Calculates: total patients, avg blood sugar
     */
    public List<AggregationResult> aggregateByDiagnosis(
            List<PatientRawRecord> records) {

        Objects.requireNonNull(records,
            "Input records list must not be null");

        LOG.info("UC-9 | Aggregating by Diagnosis...");

        Map<String, List<PatientRawRecord>> grouped =
            records.stream()
                .filter(r -> r != null)
                .filter(r -> isValidKey(r.getRawDiagnosis()))
                .collect(Collectors.groupingBy(
                    PatientRawRecord::getRawDiagnosis));

        List<AggregationResult> results =
            grouped.entrySet().stream()
                .map(entry -> {
                    String diagnosis = entry.getKey();
                    List<PatientRawRecord> group =
                        entry.getValue();

                    AggregationResult result =
                        new AggregationResult(
                            diagnosis, "DIAGNOSIS");

                    result.setTotalPatients(group.size());
                    result.setAvgBloodSugar(
                        avgBloodSugar(group));

                    LOG.info("UC-9 | DIAGNOSIS | {} | " +
                             "patients={} | avgBloodSugar={}",
                        diagnosis,
                        result.getTotalPatients(),
                        String.format("%.2f",
                            result.getAvgBloodSugar()));

                    return result;
                })
                .collect(Collectors.toList());

        LOG.info("UC-9 | Diagnosis aggregation complete. " +
                 "Groups: {}", results.size());
        return results;
    }

    // ── Private Helpers ───────────────────────────────────

    /**
     * Valid key = not null, not blank,
     * not "UNKNOWN", not "INVALID"
     */
    private boolean isValidKey(String key) {
        return key != null
            && !key.trim().isEmpty()
            && !key.equals("UNKNOWN")
            && !key.equals("INVALID");
    }

    private double avgBill(List<PatientRawRecord> records) {
        return records.stream()
            .filter(r -> isValidNumeric(r.getRawBillAmount()))
            .mapToDouble(r -> NumericValidatorUtil
                .parseDouble(r.getRawBillAmount())
                .getAsDouble())
            .average()
            .orElse(0.0);
    }

    private double avgLos(List<PatientRawRecord> records) {
        return records.stream()
            .filter(r -> isValidNumeric(
                r.getLengthOfStayDays()))
            .mapToDouble(r -> NumericValidatorUtil
                .parseDouble(r.getLengthOfStayDays())
                .getAsDouble())
            .average()
            .orElse(0.0);
    }

    private double avgBloodSugar(
            List<PatientRawRecord> records) {
        return records.stream()
            .filter(r -> isValidNumeric(r.getRawBloodSugar()))
            .mapToDouble(r -> NumericValidatorUtil
                .parseDouble(r.getRawBloodSugar())
                .getAsDouble())
            .average()
            .orElse(0.0);
    }

    /**
     * A numeric field is valid if it is not null,
     * not INVALID, and parseable as a double.
     */
    private boolean isValidNumeric(String value) {
        return value != null
            && !value.equals("INVALID")
            && NumericValidatorUtil
                .parseDouble(value).isPresent();
    }
}