package com.healthcare.service;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.model.PatientRawRecord;
import com.healthcare.util.DerivedFieldsUtil;

public class DerivedFieldsService {

    private static final Logger LOG =
        LogManager.getLogger(DerivedFieldsService.class);

    /**
     * UC-8: Calculate derived fields for all records.
     * Adds: bmi, map, lengthOfStayDays
     *
     * @param records list after UC-7
     * @return same list with derived fields populated
     */
    public List<PatientRawRecord> calculateDerivedFields(
            List<PatientRawRecord> records) {

        // DEFENSIVE: null check
        Objects.requireNonNull(records,
            "Input records list must not be null");

        LOG.info("UC-8 | Start | Records: {}", records.size());

        int invalidBmi = 0;
        int invalidMap = 0;
        int invalidLos = 0;

        for (PatientRawRecord record : records) {

            // DEFENSIVE: skip null records
            if (record == null) {
                LOG.warn("UC-8 | Null record — skipping");
                continue;
            }

            // Calculate BMI
            String bmi = DerivedFieldsUtil.calculateBmi(
                record.getRawWeightKg(),
                record.getRawHeightCm());
            record.setBmi(bmi);
            if (bmi.equals("INVALID")) {
                LOG.warn("UC-8 | Row {} | BMI could not be " +
                    "calculated. weight='{}' height='{}'",
                    record.getSheetRowNumber(),
                    record.getRawWeightKg(),
                    record.getRawHeightCm());
                invalidBmi++;
            } else {
                LOG.debug("UC-8 | Row {} | BMI = {}",
                    record.getSheetRowNumber(), bmi);
            }

            // Calculate MAP
            String map = DerivedFieldsUtil.calculateMap(
                record.getRawSystolic(),
                record.getRawDiastolic());
            record.setMap(map);
            if (map.equals("INVALID")) {
                LOG.warn("UC-8 | Row {} | MAP could not be " +
                    "calculated. systolic='{}' diastolic='{}'",
                    record.getSheetRowNumber(),
                    record.getRawSystolic(),
                    record.getRawDiastolic());
                invalidMap++;
            } else {
                LOG.debug("UC-8 | Row {} | MAP = {}",
                    record.getSheetRowNumber(), map);
            }

            // Calculate Length of Stay
            String los = DerivedFieldsUtil.calculateLos(
                record.getRawAdmissionDate(),
                record.getRawDischargeDate());
            record.setLengthOfStayDays(los);
            if (los.equals("INVALID")) {
                LOG.warn("UC-8 | Row {} | LOS could not be " +
                    "calculated. admission='{}' discharge='{}'",
                    record.getSheetRowNumber(),
                    record.getRawAdmissionDate(),
                    record.getRawDischargeDate());
                invalidLos++;
            } else {
                LOG.debug("UC-8 | Row {} | LOS = {} days",
                    record.getSheetRowNumber(), los);
            }
        }

        LOG.info("UC-8 | Complete | Invalid BMI: {} | " +
                 "Invalid MAP: {} | Invalid LOS: {}",
            invalidBmi, invalidMap, invalidLos);

        return records;
    }
}