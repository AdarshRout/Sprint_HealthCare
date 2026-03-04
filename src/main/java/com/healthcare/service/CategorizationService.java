package com.healthcare.service;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.model.PatientRawRecord;
import com.healthcare.util.CategoryUtil;

public class CategorizationService {

    private static final Logger LOG =
        LogManager.getLogger(CategorizationService.class);

    /**
     * UC-10: Categorize numeric fields into buckets.
     * Adds: bmiCategory, bloodSugarCategory,
     *       ageGroup, cholesterolCategory
     *
     * @param records list after UC-9
     * @return same list with category fields populated
     */
    public List<PatientRawRecord> categorize(
            List<PatientRawRecord> records) {

        Objects.requireNonNull(records,
            "Input records list must not be null");

        LOG.info("UC-10 | Start | Records: {}",
            records.size());

        int unknownBmi         = 0;
        int unknownBloodSugar  = 0;
        int unknownAge         = 0;
        int unknownCholesterol = 0;

        for (PatientRawRecord record : records) {

            // DEFENSIVE: skip null records
            if (record == null) {
                LOG.warn("UC-10 | Null record — skipping");
                continue;
            }

            // BMI Category
            String bmiCat = CategoryUtil
                .categorizeBmi(record.getBmi());
            record.setBmiCategory(bmiCat);
            if (bmiCat.equals("UNKNOWN")) unknownBmi++;

            // Blood Sugar Category
            String sugarCat = CategoryUtil
                .categorizeBloodSugar(
                    record.getRawBloodSugar());
            record.setBloodSugarCategory(sugarCat);
            if (sugarCat.equals("UNKNOWN"))
                unknownBloodSugar++;

            // Age Group
            String ageCat = CategoryUtil
                .categorizeAge(record.getRawDob());
            record.setAgeGroup(ageCat);
            if (ageCat.equals("UNKNOWN")) unknownAge++;

            // Cholesterol Category
            String cholCat = CategoryUtil
                .categorizeСholesterol(
                    record.getRawCholesterol());
            record.setCholesterolCategory(cholCat);
            if (cholCat.equals("UNKNOWN"))
                unknownCholesterol++;

            LOG.debug("UC-10 | Row {} | BMI={} " +
                      "BloodSugar={} Age={} Chol={}",
                record.getSheetRowNumber(),
                bmiCat, sugarCat, ageCat, cholCat);
        }

        LOG.info("UC-10 | Complete | " +
                 "Unknown BMI: {} | " +
                 "Unknown BloodSugar: {} | " +
                 "Unknown Age: {} | " +
                 "Unknown Cholesterol: {}",
            unknownBmi, unknownBloodSugar,
            unknownAge, unknownCholesterol);

        return records;
    }
}
