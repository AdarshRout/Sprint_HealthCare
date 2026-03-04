package com.healthcare.service;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.model.PatientRawRecord;
import com.healthcare.util.ContactValidatorUtil;

public class ContactValidationService {

    private static final Logger LOG =
        LogManager.getLogger(ContactValidationService.class);

    /**
     * UC-7: Validate phone and email fields.
     * Invalid values → replaced with "INVALID"
     * Valid emails → lowercased
     *
     * @param records list after UC-6
     * @return same list with contact fields validated
     */
    public List<PatientRawRecord> validateContacts(
            List<PatientRawRecord> records) {

        // DEFENSIVE: null check
        Objects.requireNonNull(records,
            "Input records list must not be null");

        LOG.info("UC-7 | Start | Records: {}", records.size());

        int invalidPhones = 0;
        int invalidEmails = 0;

        for (PatientRawRecord record : records) {

            // DEFENSIVE: skip null records
            if (record == null) {
                LOG.warn("UC-7 | Null record — skipping");
                continue;
            }

            // Validate phone
            String cleanPhone = ContactValidatorUtil
                .validatePhone(record.getRawPhone());

            if (cleanPhone.equals("INVALID")) {
                LOG.warn("UC-7 | Row {} | phone '{}' → INVALID",
                    record.getSheetRowNumber(),
                    record.getRawPhone());
                invalidPhones++;
            } else if (!cleanPhone.equals(record.getRawPhone())) {
                LOG.debug("UC-7 | Row {} | phone '{}' → '{}'",
                    record.getSheetRowNumber(),
                    record.getRawPhone(), cleanPhone);
            }
            record.setRawPhone(cleanPhone);

            // Validate email
            String cleanEmail = ContactValidatorUtil
                .validateEmail(record.getRawEmail());

            if (cleanEmail.equals("INVALID")) {
                LOG.warn("UC-7 | Row {} | email '{}' → INVALID",
                    record.getSheetRowNumber(),
                    record.getRawEmail());
                invalidEmails++;
            } else if (!cleanEmail.equals(record.getRawEmail())) {
                LOG.debug("UC-7 | Row {} | email '{}' → '{}'",
                    record.getSheetRowNumber(),
                    record.getRawEmail(), cleanEmail);
            }
            record.setRawEmail(cleanEmail);
        }

        LOG.info("UC-7 | Complete | Invalid phones: {} | " +
                 "Invalid emails: {}",
            invalidPhones, invalidEmails);

        return records;
    }
}