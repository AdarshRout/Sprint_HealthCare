package com.healthcare.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.model.PatientRawRecord;

public class DuplicateService {

    private static final Logger LOG = 
        LogManager.getLogger(DuplicateService.class);

    /**
     * UC-1: Remove duplicate records.
     * Uses fingerprint: firstName + lastName + dob + phone
     * 
     * @param records raw records from Google Sheets
     * @return list with duplicates removed
     */
    public List<PatientRawRecord> removeDuplicates(
            List<PatientRawRecord> records) {

        // DEFENSIVE: null check
        Objects.requireNonNull(records, 
            "Input records list must not be null");

        LOG.info("UC-1 | Start | Input records: {}", 
            records.size());

        List<PatientRawRecord> unique = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        int duplicateCount = 0;

        for (PatientRawRecord record : records) {

            // DEFENSIVE: skip null records
            if (record == null) {
                LOG.warn("UC-1 | Null record encountered — skipping");
                continue;
            }

            String fingerprint = buildFingerprint(record);

            if (seen.add(fingerprint)) {
                // add() returned true = first time seen = unique
                unique.add(record);
            } else {
                // add() returned false = already seen = duplicate
                duplicateCount++;
                LOG.warn("UC-1 | Duplicate found | Row: {} | " +
                         "Name: {} {} | DOB: {} | Phone: {}",
                    record.getSheetRowNumber(),
                    record.getRawFirstName(),
                    record.getRawLastName(),
                    record.getRawDob(),
                    record.getRawPhone());
            }
        }

        LOG.info("UC-1 | Complete | Unique: {} | Duplicates removed: {}",
            unique.size(), duplicateCount);

        return unique;
    }

    /**
     * Builds a fingerprint string from key identifying fields.
     * Lowercase + trim to catch case-difference duplicates.
     * Fields separated by | to avoid false matches.
     */
    private String buildFingerprint(PatientRawRecord record) {
        return record.getRawFirstName().trim().toLowerCase()
            + "|" + record.getRawLastName().trim().toLowerCase()
            + "|" + record.getRawDob().trim()
            + "|" + record.getRawPhone().trim();
    }
}
