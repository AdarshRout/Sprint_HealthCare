package com.healthcare.service;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.model.PatientRawRecord;
import com.healthcare.util.NameNormalizerUtil;

public class NameNormalizationService {

    private static final Logger LOG =
        LogManager.getLogger(NameNormalizationService.class);

    /**
     * UC-2: Normalize all first and last names in the list.
     * Applies title case, trims whitespace.
     *
     * @param records list after UC-1 dedup
     * @return same list with names cleaned
     */
    public List<PatientRawRecord> normalizeNames(
            List<PatientRawRecord> records) {

        // DEFENSIVE: null check
        Objects.requireNonNull(records,
            "Input records list must not be null");

        LOG.info("UC-2 | Start | Records to normalize: {}",
            records.size());

        int fixedCount = 0;

        for (PatientRawRecord record : records) {

            // DEFENSIVE: skip null records
            if (record == null) {
                LOG.warn("UC-2 | Null record encountered — skipping");
                continue;
            }

            String originalFirst = record.getRawFirstName();
            String originalLast  = record.getRawLastName();

            String cleanFirst = NameNormalizerUtil
                .toTitleCase(originalFirst);
            String cleanLast  = NameNormalizerUtil
                .toTitleCase(originalLast);

            // Only log if something actually changed
            if (!originalFirst.equals(cleanFirst) 
                    || !originalLast.equals(cleanLast)) {

                LOG.debug("UC-2 | Row {} | '{} {}' → '{} {}'",
                    record.getSheetRowNumber(),
                    originalFirst, originalLast,
                    cleanFirst,    cleanLast);

                fixedCount++;
            }

            record.setRawFirstName(cleanFirst);
            record.setRawLastName(cleanLast);
        }

        LOG.info("UC-2 | Complete | Names fixed: {}",
            fixedCount);

        return records;
    }
}
