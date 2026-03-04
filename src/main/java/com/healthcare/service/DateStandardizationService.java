package com.healthcare.service;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.model.PatientRawRecord;
import com.healthcare.util.DateParserUtil;

public class DateStandardizationService {

    private static final Logger LOG =
        LogManager.getLogger(DateStandardizationService.class);

    /**
     * UC-4: Standardize all date fields to yyyy-MM-dd.
     * Affected fields: dob, admissionDate, dischargeDate
     *
     * @param records list after UC-3
     * @return same list with dates standardized
     */
    public List<PatientRawRecord> standardizeDates(
            List<PatientRawRecord> records) {

        // DEFENSIVE: null check
        Objects.requireNonNull(records,
            "Input records list must not be null");

        LOG.info("UC-4 | Start | Records: {}", records.size());

        int fixedCount  = 0;
        int invalidCount = 0;

        for (PatientRawRecord record : records) {

            // DEFENSIVE: skip null records
            if (record == null) {
                LOG.warn("UC-4 | Null record — skipping");
                continue;
            }

            boolean changed  = false;
            boolean hasInvalid = false;

            // Fix DOB
            String cleanDob = DateParserUtil
                .parseAndFormat(record.getRawDob());
            if (!cleanDob.equals(record.getRawDob())) {
                if (cleanDob.equals("INVALID")) {
                    LOG.warn("UC-4 | Row {} | dob '{}' → INVALID",
                        record.getSheetRowNumber(),
                        record.getRawDob());
                    hasInvalid = true;
                } else {
                    LOG.debug("UC-4 | Row {} | dob '{}' → '{}'",
                        record.getSheetRowNumber(),
                        record.getRawDob(), cleanDob);
                }
                record.setRawDob(cleanDob);
                changed = true;
            }
            
            // Fix Admission Date
            String cleanAdm = DateParserUtil
                .parseAndFormat(record.getRawAdmissionDate());
            if (!cleanAdm.equals(record.getRawAdmissionDate())) {
                if (cleanAdm.equals("INVALID")) {
                    LOG.warn("UC-4 | Row {} | admissionDate '{}' → INVALID",
                        record.getSheetRowNumber(),
                        record.getRawAdmissionDate());
                    hasInvalid = true;
                } else {
                    LOG.debug("UC-4 | Row {} | admissionDate '{}' → '{}'",
                        record.getSheetRowNumber(),
                        record.getRawAdmissionDate(), cleanAdm);
                }
                record.setRawAdmissionDate(cleanAdm);
                changed = true;
            }
            
            // Fix Discharge Date
            String cleanDis = DateParserUtil
                .parseAndFormat(record.getRawDischargeDate());
            if (!cleanDis.equals(record.getRawDischargeDate())) {
                if (cleanDis.equals("INVALID")) {
                    LOG.warn("UC-4 | Row {} | dischargeDate '{}' → INVALID",
                        record.getSheetRowNumber(),
                        record.getRawDischargeDate());
                    hasInvalid = true;
                } else {
                    LOG.debug("UC-4 | Row {} | dischargeDate '{}' → '{}'",
                        record.getSheetRowNumber(),
                        record.getRawDischargeDate(), cleanDis);
                }
                record.setRawDischargeDate(cleanDis);
                changed = true;
            }

            if (changed)  fixedCount++;
            if (hasInvalid) invalidCount++;
        }

        LOG.info("UC-4 | Complete | Records fixed: {} | " +
                 "Records with invalid dates: {}",
            fixedCount, invalidCount);

        return records;
    }
}
