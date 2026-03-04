package com.healthcare.db;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.dao.AggregationDAO;
import com.healthcare.dao.BillingDAO;
import com.healthcare.dao.ClinicalRecordDAO;
import com.healthcare.dao.PatientDAO;
import com.healthcare.dao.VisitDAO;
import com.healthcare.model.AggregationResult;
import com.healthcare.model.PatientRawRecord;

public class DatabaseWriter {

    private static final Logger LOG =
        LogManager.getLogger(DatabaseWriter.class);

    private final PatientDAO        patientDAO;
    private final ClinicalRecordDAO clinicalDAO;
    private final VisitDAO          visitDAO;
    private final BillingDAO        billingDAO;
    private final AggregationDAO    aggregationDAO;

    public DatabaseWriter() {
        this.patientDAO     = new PatientDAO();
        this.clinicalDAO    = new ClinicalRecordDAO();
        this.visitDAO       = new VisitDAO();
        this.billingDAO     = new BillingDAO();
        this.aggregationDAO = new AggregationDAO();
    }

    /**
     * Writes all cleaned patient records to MySQL.
     * For each record:
     *   1. Insert patient     → get patient_id
     *   2. Insert clinical    → uses patient_id
     *   3. Insert visit       → get visit_id
     *   4. Insert billing     → uses patient_id + visit_id
     *
     * Skips a record entirely if patient insert fails.
     *
     * @param records fully cleaned and categorized records
     */
    public void writePatients(
            List<PatientRawRecord> records) {

        Objects.requireNonNull(records,
            "Records list must not be null");

        LOG.info("DatabaseWriter | Start | Records: {}",
            records.size());

        int success  = 0;
        int failed   = 0;
        int skipped  = 0;

        for (PatientRawRecord record : records) {

            // DEFENSIVE: skip null records
            if (record == null) {
                LOG.warn("DatabaseWriter | " +
                         "Null record — skipping");
                skipped++;
                continue;
            }

            try {
                // Step 1: Insert patient
                int patientId =
                    patientDAO.insert(record);

                if (patientId == -1) {
                    LOG.warn("DatabaseWriter | Row {} | " +
                             "Patient insert failed — " +
                             "skipping all related inserts.",
                        record.getSheetRowNumber());
                    failed++;
                    continue;
                }

                // Step 2: Insert clinical record
                boolean clinicalOk =
                    clinicalDAO.insert(record, patientId);

                if (!clinicalOk) {
                    LOG.warn("DatabaseWriter | Row {} | " +
                             "Clinical insert failed.",
                        record.getSheetRowNumber());
                }

                // Step 3: Insert visit
                int visitId =
                    visitDAO.insert(record, patientId);

                if (visitId == -1) {
                    LOG.warn("DatabaseWriter | Row {} | " +
                             "Visit insert failed — " +
                             "skipping billing insert.",
                        record.getSheetRowNumber());
                    failed++;
                    continue;
                }

                // Step 4: Insert billing
                boolean billingOk = billingDAO.insert(
                    record, patientId, visitId);

                if (!billingOk) {
                    LOG.warn("DatabaseWriter | Row {} | " +
                             "Billing insert failed.",
                        record.getSheetRowNumber());
                }

                success++;
                LOG.debug("DatabaseWriter | Row {} | " +
                          "patient_id={} visit_id={} ✓",
                    record.getSheetRowNumber(),
                    patientId, visitId);

            } catch (Exception e) {
                LOG.error("DatabaseWriter | Row {} | " +
                          "Unexpected error: {}",
                    record.getSheetRowNumber(),
                    e.getMessage(), e);
                failed++;
            }
        }

        LOG.info("DatabaseWriter | Complete | " +
                 "Success: {} | Failed: {} | Skipped: {}",
            success, failed, skipped);
    }

    /**
     * Writes all aggregation results to MySQL.
     *
     * @param byDept      department aggregations
     * @param byDoctor    doctor aggregations
     * @param byDiagnosis diagnosis aggregations
     */
    public void writeAggregations(
            List<AggregationResult> byDept,
            List<AggregationResult> byDoctor,
            List<AggregationResult> byDiagnosis) {

        Objects.requireNonNull(byDept,
            "Department aggregations must not be null");
        Objects.requireNonNull(byDoctor,
            "Doctor aggregations must not be null");
        Objects.requireNonNull(byDiagnosis,
            "Diagnosis aggregations must not be null");

        LOG.info("DatabaseWriter | Writing aggregations...");

        aggregationDAO.insertDepartmentAggregations(byDept);
        aggregationDAO.insertDoctorAggregations(byDoctor);
        aggregationDAO.insertDiagnosisAggregations(byDiagnosis);

        LOG.info("DatabaseWriter | Aggregations written. " +
                 "Dept: {} | Doctor: {} | Diagnosis: {}",
            byDept.size(),
            byDoctor.size(),
            byDiagnosis.size());
    }

    /**
     * Shuts down the connection pool.
     * Always call this when pipeline finishes.
     */
    public void shutdown() {
        LOG.info("DatabaseWriter | Shutting down...");
        ConnectionPool.getInstance().shutdown();
        LOG.info("DatabaseWriter | Shutdown complete.");
    }
}