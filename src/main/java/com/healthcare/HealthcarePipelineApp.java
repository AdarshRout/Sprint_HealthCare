package com.healthcare;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.config.AppConfig;
import com.healthcare.db.DatabaseWriter;
import com.healthcare.model.AggregationResult;
import com.healthcare.model.PatientRawRecord;
import com.healthcare.reader.GoogleSheetsReader;
import com.healthcare.service.AggregationService;
import com.healthcare.service.CategorizationService;
import com.healthcare.service.ContactValidationService;
import com.healthcare.service.DateStandardizationService;
import com.healthcare.service.DerivedFieldsService;
import com.healthcare.service.DiagnosisMappingService;
import com.healthcare.service.DuplicateService;
import com.healthcare.service.NameNormalizationService;
import com.healthcare.service.NumericFixService;
import com.healthcare.service.StatusValidationService;

public class HealthcarePipelineApp {

    private static final Logger LOG =
        LogManager.getLogger(HealthcarePipelineApp.class);

    public static void main(String[] args) {

        LOG.info("========================================");
        LOG.info("  Healthcare Pipeline — Starting       ");
        LOG.info("========================================");

        // ── Read ──────────────────────────────────

        int lastRow = AppConfig.getInstance()
            .getLastProcessedRow();
        LOG.info("Last processed row: {}", lastRow);

        GoogleSheetsReader reader =
            new GoogleSheetsReader();

        List<PatientRawRecord> rawRecords =
            reader.readNewRowsSince(lastRow);
        LOG.info("Total records fetched: {}",
            rawRecords.size());

        // Capture immediately after reading
        int newLastRow = lastRow + rawRecords.size();

        if (rawRecords.isEmpty()) {
            LOG.info("No new records to process. " +
                     "Exiting.");
            return;
        }
        
        DatabaseWriter dbWriter = new DatabaseWriter();

        try {


            // ── UC-1: Remove Duplicates ───────────────

            DuplicateService duplicateService =
                new DuplicateService();
            List<PatientRawRecord> uniqueRecords =
                duplicateService.removeDuplicates(rawRecords);
            LOG.info("After dedup: {}",
                uniqueRecords.size());

            // ── UC-2: Normalize Names ─────────────────

            NameNormalizationService nameService =
                new NameNormalizationService();
            nameService.normalizeNames(uniqueRecords);
            LOG.info("After name normalization: {}",
                uniqueRecords.size());

            // ── UC-3: Fix Numeric Fields ──────────────

            NumericFixService numericService =
                new NumericFixService();
            numericService.fixNumericFields(uniqueRecords);
            LOG.info("After numeric fix: {}",
                uniqueRecords.size());

            // ── UC-4: Standardize Dates ───────────────

            DateStandardizationService dateService =
                new DateStandardizationService();
            dateService.standardizeDates(uniqueRecords);
            LOG.info("After date standardization: {}",
                uniqueRecords.size());

            // ── UC-5: Map Diagnosis Codes ─────────────

            DiagnosisMappingService diagnosisService =
                new DiagnosisMappingService();
            diagnosisService.mapDiagnosis(uniqueRecords);
            LOG.info("After diagnosis mapping: {}",
                uniqueRecords.size());

            // ── UC-6: Validate Statuses ───────────────

            StatusValidationService statusService =
                new StatusValidationService();
            statusService.validateStatuses(uniqueRecords);
            LOG.info("After status validation: {}",
                uniqueRecords.size());

            // ── UC-7: Validate Contacts ───────────────

            ContactValidationService contactService =
                new ContactValidationService();
            contactService.validateContacts(uniqueRecords);
            LOG.info("After contact validation: {}",
                uniqueRecords.size());

            // ── UC-8: Derived Fields ──────────────────

            DerivedFieldsService derivedService =
                new DerivedFieldsService();
            derivedService.calculateDerivedFields(
                uniqueRecords);
            LOG.info("After derived fields: {}",
                uniqueRecords.size());

            // ── UC-9: Aggregation ─────────────────────

            AggregationService aggregationService =
                new AggregationService();

            List<AggregationResult> byDepartment =
                aggregationService
                    .aggregateByDepartment(uniqueRecords);
            LOG.info("Department groups: {}",
                byDepartment.size());

            List<AggregationResult> byDoctor =
                aggregationService
                    .aggregateByDoctor(uniqueRecords);
            LOG.info("Doctor groups: {}",
                byDoctor.size());

            List<AggregationResult> byDiagnosis =
                aggregationService
                    .aggregateByDiagnosis(uniqueRecords);
            LOG.info("Diagnosis groups: {}",
                byDiagnosis.size());

            // ── UC-10: Categorization ─────────────────

            CategorizationService categorizationService =
                new CategorizationService();
            categorizationService.categorize(uniqueRecords);
            LOG.info("After categorization: {}",
                uniqueRecords.size());

            // ── Write to Database ─────────────────────

            LOG.info("========================================");
            LOG.info("  Writing to MySQL database...        ");
            LOG.info("========================================");

            dbWriter.writePatients(uniqueRecords);
            dbWriter.writeAggregations(
                byDepartment, byDoctor, byDiagnosis);

            // ── Persist Last Processed Row ────────────

            AppConfig.getInstance()
                .persistLastProcessedRow(newLastRow);
            LOG.info("Last processed row updated to: {}",
                newLastRow);

            // ── Summary ───────────────────────────────

            LOG.info("========================================");
            LOG.info("  Pipeline Complete!                  ");
            LOG.info("  Records read:      {}",
                rawRecords.size());
            LOG.info("  Records processed: {}",
                uniqueRecords.size());
            LOG.info("  Duplicates removed: {}",
                rawRecords.size() - uniqueRecords.size());
            LOG.info("  Next run starts from row: {}",
                newLastRow);
            LOG.info("========================================");

        } catch (Exception e) {
            LOG.error("Pipeline failed with error: {}",
                e.getMessage(), e);
            throw new RuntimeException(
                "Pipeline execution failed.", e);

        } finally {
            // Always shut down pool — even if pipeline fails
            dbWriter.shutdown();
        }
    }
}
