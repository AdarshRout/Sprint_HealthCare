package com.healthcare.service;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.model.PatientRawRecord;
import com.healthcare.util.NumericValidatorUtil;

public class NumericFixService {

    private static final Logger LOG =
        LogManager.getLogger(NumericFixService.class);

    // Valid ranges for each numeric field
    private static final double MIN_SYSTOLIC   = 60,  MAX_SYSTOLIC   = 250;
    private static final double MIN_DIASTOLIC  = 40,  MAX_DIASTOLIC  = 150;
    private static final double MIN_WEIGHT     = 1,   MAX_WEIGHT     = 300;
    private static final double MIN_HEIGHT     = 50,  MAX_HEIGHT     = 250;
    private static final double MIN_SUGAR      = 40,  MAX_SUGAR      = 600;
    private static final double MIN_CHOLESTEROL= 100, MAX_CHOLESTEROL= 400;
    private static final double MIN_BILL       = 0,   MAX_BILL       = 999999;

    /**
     * UC-3: Fix numeric fields across all records.
     * Invalid or out-of-range values → replaced with "INVALID"
     *
     * @param records list after UC-2
     * @return same list with numeric fields cleaned
     */
    public List<PatientRawRecord> fixNumericFields(
            List<PatientRawRecord> records) {

        // DEFENSIVE: null check
        Objects.requireNonNull(records,
            "Input records list must not be null");

        LOG.info("UC-3 | Start | Records: {}", records.size());

        int fixedCount = 0;

        for (PatientRawRecord record : records) {

            // DEFENSIVE: skip null records
            if (record == null) {
                LOG.warn("UC-3 | Null record — skipping");
                continue;
            }

            boolean wasFixed = fixRecord(record);
            if (wasFixed) {
                fixedCount++;
            }
        }

        LOG.info("UC-3 | Complete | Records with fixes: {}",
            fixedCount);

        return records;
    }

    /**
     * Fixes all numeric fields on one record.
     * Returns true if at least one field was changed.
     */
    private boolean fixRecord(PatientRawRecord record) {
        boolean fixed = false;

        fixed |= fixField(record, "systolic",
            record.getRawSystolic(),
            MIN_SYSTOLIC, MAX_SYSTOLIC,
            record::setRawSystolic);

        fixed |= fixField(record, "diastolic",
            record.getRawDiastolic(),
            MIN_DIASTOLIC, MAX_DIASTOLIC,
            record::setRawDiastolic);

        fixed |= fixField(record, "weight",
            record.getRawWeightKg(),
            MIN_WEIGHT, MAX_WEIGHT,
            record::setRawWeightKg);

        fixed |= fixField(record, "height",
            record.getRawHeightCm(),
            MIN_HEIGHT, MAX_HEIGHT,
            record::setRawHeightCm);

        fixed |= fixField(record, "bloodSugar",
            record.getRawBloodSugar(),
            MIN_SUGAR, MAX_SUGAR,
            record::setRawBloodSugar);

        fixed |= fixField(record, "cholesterol",
            record.getRawCholesterol(),
            MIN_CHOLESTEROL, MAX_CHOLESTEROL,
            record::setRawCholesterol);
        
        String cleanBill = NumericValidatorUtil
            .stripCurrencyPrefix(record.getRawBillAmount());
        record.setRawBillAmount(cleanBill);

        fixed |= fixField(record, "billAmount",
            record.getRawBillAmount(),
            MIN_BILL, MAX_BILL,
            record::setRawBillAmount);

        return fixed;
    }

    /**
     * Validates one field and updates it if invalid.
     *
     * @param record    the record being processed
     * @param fieldName name for logging
     * @param rawValue  current raw string value
     * @param min       minimum valid value
     * @param max       maximum valid value
     * @param setter    method reference to update the field
     * @return true if field was changed to INVALID
     */
    private boolean fixField(
            PatientRawRecord record,
            String fieldName,
            String rawValue,
            double min,
            double max,
            java.util.function.Consumer<String> setter) {

        String result = NumericValidatorUtil
            .validateNumeric(rawValue, min, max);

        if (result.equals("INVALID")) {
            LOG.warn("UC-3 | Row {} | {} = '{}' → INVALID",
                record.getSheetRowNumber(),
                fieldName,
                rawValue);
            setter.accept("INVALID");
            return true;
        }

        return false;
    }
}
