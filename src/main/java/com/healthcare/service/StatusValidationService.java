package com.healthcare.service;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.constant.AdmissionStatus;
import com.healthcare.constant.BloodGroup;
import com.healthcare.constant.Department;
import com.healthcare.constant.Gender;
import com.healthcare.constant.InsuranceStatus;
import com.healthcare.constant.PatientStatus;
import com.healthcare.constant.PaymentStatus;
import com.healthcare.model.PatientRawRecord;

public class StatusValidationService {

    private static final Logger LOG =
        LogManager.getLogger(StatusValidationService.class);

    /**
     * UC-6: Validate and standardize all status fields.
     * Fields: gender, bloodGroup, department,
     *         admissionStatus, paymentStatus,
     *         insuranceStatus, patientStatus
     *
     * @param records list after UC-5
     * @return same list with all status fields standardized
     */
    public List<PatientRawRecord> validateStatuses(
            List<PatientRawRecord> records) {

        Objects.requireNonNull(records,
            "Input records list must not be null");

        LOG.info("UC-6 | Start | Records: {}", records.size());

        int fixedCount   = 0;
        int unknownCount = 0;

        for (PatientRawRecord record : records) {

            if (record == null) {
                LOG.warn("UC-6 | Null record — skipping");
                continue;
            }

            boolean hasUnknown = false;

            // Gender
            String gender = Gender.fromRaw(
                record.getRawGender()).getDisplayName();
            logIfChanged(record, "gender",
                record.getRawGender(), gender);
            record.setRawGender(gender);
            if (gender.equals("UNKNOWN")) hasUnknown = true;

            // Blood Group
            String bg = BloodGroup.fromRaw(
                record.getRawBloodGroup()).getDisplayName();
            logIfChanged(record, "bloodGroup",
                record.getRawBloodGroup(), bg);
            record.setRawBloodGroup(bg);
            if (bg.equals("UNKNOWN")) hasUnknown = true;

            // Department
            String dept = Department.fromRaw(
                record.getRawDepartment()).getDisplayName();
            logIfChanged(record, "department",
                record.getRawDepartment(), dept);
            record.setRawDepartment(dept);
            if (dept.equals("UNKNOWN")) hasUnknown = true;

            // Admission Status
            String adm = AdmissionStatus.fromRaw(
                record.getRawAdmissionStatus()).getDisplayName();
            logIfChanged(record, "admissionStatus",
                record.getRawAdmissionStatus(), adm);
            record.setRawAdmissionStatus(adm);
            if (adm.equals("UNKNOWN")) hasUnknown = true;

            // Payment Status
            String pay = PaymentStatus.fromRaw(
                record.getRawPaymentStatus()).getDisplayName();
            logIfChanged(record, "paymentStatus",
                record.getRawPaymentStatus(), pay);
            record.setRawPaymentStatus(pay);
            if (pay.equals("UNKNOWN")) hasUnknown = true;

            // Insurance Status
            String ins = InsuranceStatus.fromRaw(
                record.getRawInsuranceStatus()).getDisplayName();
            logIfChanged(record, "insuranceStatus",
                record.getRawInsuranceStatus(), ins);
            record.setRawInsuranceStatus(ins);
            if (ins.equals("UNKNOWN")) hasUnknown = true;

            // Patient Status
            String pat = PatientStatus.fromRaw(
                record.getRawPatientStatus()).getDisplayName();
            logIfChanged(record, "patientStatus",
                record.getRawPatientStatus(), pat);
            record.setRawPatientStatus(pat);
            if (pat.equals("UNKNOWN")) hasUnknown = true;

            fixedCount++;
            if (hasUnknown) unknownCount++;
        }

        LOG.info("UC-6 | Complete | Records fixed: {} | " +
                 "Records with unknowns: {}",
            fixedCount, unknownCount);

        return records;
    }

    private void logIfChanged(
            PatientRawRecord record,
            String fieldName,
            String original,
            String mapped) {

        if (mapped.equals("UNKNOWN")) {
            LOG.warn("UC-6 | Row {} | {} '{}' → UNKNOWN",
                record.getSheetRowNumber(),
                fieldName, original);
        } else if (!mapped.equals(
                original == null ? "" : original.trim())) {
            LOG.debug("UC-6 | Row {} | {} '{}' → '{}'",
                record.getSheetRowNumber(),
                fieldName, original, mapped);
        }
    }

}