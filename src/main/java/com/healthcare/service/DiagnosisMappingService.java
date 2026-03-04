package com.healthcare.service;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.model.PatientRawRecord;
import com.healthcare.util.DiagnosisMapperUtil;

public class DiagnosisMappingService {

    private static final Logger LOG =
        LogManager.getLogger(DiagnosisMappingService.class);

    /**
     * UC-5: Map ICD-10 diagnosis codes to disease names.
     * "I10" → "Essential Hypertension"
     *
     * @param records list after UC-4
     * @return same list with diagnosis mapped
     */
    public List<PatientRawRecord> mapDiagnosis(
            List<PatientRawRecord> records) {

        // DEFENSIVE: null check
        Objects.requireNonNull(records,
            "Input records list must not be null");

        LOG.info("UC-5 | Start | Records: {}", records.size());

        int mappedCount  = 0;
        int unknownCount = 0;

        for (PatientRawRecord record : records) {

            // DEFENSIVE: skip null records
            if (record == null) {
                LOG.warn("UC-5 | Null record — skipping");
                continue;
            }

            String rawCode = record.getRawDiagnosis();
            String disease = DiagnosisMapperUtil
                .mapToDisease(rawCode);

            if (disease.equals("UNKNOWN")) {
                LOG.warn("UC-5 | Row {} | code '{}' → UNKNOWN",
                    record.getSheetRowNumber(), rawCode);
                unknownCount++;
            } else {
                LOG.debug("UC-5 | Row {} | '{}' → '{}'",
                    record.getSheetRowNumber(),
                    rawCode, disease);
                mappedCount++;
            }

            record.setRawDiagnosis(disease);
        }

        LOG.info("UC-5 | Complete | Mapped: {} | Unknown: {}",
            mappedCount, unknownCount);

        return records;
    }
}
