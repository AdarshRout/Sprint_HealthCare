package com.healthcare.model;

import java.util.List;
import java.util.Objects;

public class PatientRawRecord {

    // Row metadata
    private int sheetRowNumber;

    // Section 1 - Patient Identity
    private String submissionTimestamp;
    private String rawFirstName;
    private String rawLastName;
    private String rawDob;
    private String rawGender;
    private String rawPhone;
    private String rawEmail;
    private String rawBloodGroup;

    // Section 2 - Clinical Data
    private String rawDepartment;
    private String rawDiagnosisCode;
    private String rawSystolic;
    private String rawDiastolic;
    private String rawWeightKg;
    private String rawHeightCm;
    private String rawBloodSugar;
    private String rawCholesterol;

    // Section 3 - Visit Information
    private String rawAdmissionDate;
    private String rawDischargeDate;
    private String rawAdmissionStatus;
    private String rawPaymentStatus;
    private String rawInsuranceStatus;
    private String rawBillAmount;
    private String rawDoctorId;

    public PatientRawRecord() {}

    public static PatientRawRecord fromSheetRow(
        List<Object> row, int rowNumber) {

        Objects.requireNonNull(row,
            "Sheet row must not be null. Row: " + rowNumber);

        PatientRawRecord r = new PatientRawRecord();
        r.sheetRowNumber        = rowNumber;
        r.submissionTimestamp   = safeGet(row, 0);
        r.rawFirstName          = safeGet(row, 1);
        r.rawLastName           = safeGet(row, 2);
        r.rawDob                = safeGet(row, 3);
        r.rawGender             = safeGet(row, 4);
        r.rawPhone              = safeGet(row, 5);
        r.rawEmail              = safeGet(row, 6);
        r.rawBloodGroup         = safeGet(row, 7);
        r.rawDepartment         = safeGet(row, 8);
        r.rawDiagnosisCode      = safeGet(row, 9);
        r.rawSystolic           = safeGet(row, 10);
        r.rawDiastolic          = safeGet(row, 11);
        r.rawWeightKg           = safeGet(row, 12);
        r.rawHeightCm           = safeGet(row, 13);
        r.rawBloodSugar         = safeGet(row, 14);
        r.rawCholesterol        = safeGet(row, 15);
        r.rawAdmissionDate      = safeGet(row, 16);
        r.rawDischargeDate      = safeGet(row, 17);
        r.rawAdmissionStatus    = safeGet(row, 18);
        r.rawPaymentStatus      = safeGet(row, 19);
        r.rawInsuranceStatus    = safeGet(row, 20);
        r.rawBillAmount         = safeGet(row, 21);
        r.rawDoctorId           = safeGet(row, 22);
        return r;
    }

    private static String safeGet(List<Object> row, int index) {
        if (index >= row.size()) {
            return "";
        }
        Object value = row.get(index);
        if (value == null) {
            return "";
        }
        return value.toString().trim();
    }

    public boolean isCompletelyEmpty() {
        return rawFirstName.isBlank()
            && rawLastName.isBlank()
            && rawPhone.isBlank()
            && rawEmail.isBlank();
    }

    @Override
    public String toString() {
        return "PatientRawRecord{" +
               "row=" + sheetRowNumber +
               ", name='" + rawFirstName + " " + rawLastName + "'" +
               ", phone='" + rawPhone + "'" +
               ", email='" + rawEmail + "'" +
               "}";
    }

	public int getSheetRowNumber() {
		return sheetRowNumber;
	}

	public void setSheetRowNumber(int sheetRowNumber) {
		this.sheetRowNumber = sheetRowNumber;
	}

	public String getSubmissionTimestamp() {
		return submissionTimestamp;
	}

	public void setSubmissionTimestamp(String submissionTimestamp) {
		this.submissionTimestamp = submissionTimestamp;
	}

	public String getRawFirstName() {
		return rawFirstName;
	}

	public void setRawFirstName(String rawFirstName) {
		this.rawFirstName = rawFirstName;
	}

	public String getRawLastName() {
		return rawLastName;
	}

	public void setRawLastName(String rawLastName) {
		this.rawLastName = rawLastName;
	}

	public String getRawDob() {
		return rawDob;
	}

	public void setRawDob(String rawDob) {
		this.rawDob = rawDob;
	}

	public String getRawGender() {
		return rawGender;
	}

	public void setRawGender(String rawGender) {
		this.rawGender = rawGender;
	}

	public String getRawPhone() {
		return rawPhone;
	}

	public void setRawPhone(String rawPhone) {
		this.rawPhone = rawPhone;
	}

	public String getRawEmail() {
		return rawEmail;
	}

	public void setRawEmail(String rawEmail) {
		this.rawEmail = rawEmail;
	}

	public String getRawBloodGroup() {
		return rawBloodGroup;
	}

	public void setRawBloodGroup(String rawBloodGroup) {
		this.rawBloodGroup = rawBloodGroup;
	}

	public String getRawDepartment() {
		return rawDepartment;
	}

	public void setRawDepartment(String rawDepartment) {
		this.rawDepartment = rawDepartment;
	}

	public String getRawDiagnosisCode() {
		return rawDiagnosisCode;
	}

	public void setRawDiagnosisCode(String rawDiagnosisCode) {
		this.rawDiagnosisCode = rawDiagnosisCode;
	}

	public String getRawSystolic() {
		return rawSystolic;
	}

	public void setRawSystolic(String rawSystolic) {
		this.rawSystolic = rawSystolic;
	}

	public String getRawDiastolic() {
		return rawDiastolic;
	}

	public void setRawDiastolic(String rawDiastolic) {
		this.rawDiastolic = rawDiastolic;
	}

	public String getRawWeightKg() {
		return rawWeightKg;
	}

	public void setRawWeightKg(String rawWeightKg) {
		this.rawWeightKg = rawWeightKg;
	}

	public String getRawHeightCm() {
		return rawHeightCm;
	}

	public void setRawHeightCm(String rawHeightCm) {
		this.rawHeightCm = rawHeightCm;
	}

	public String getRawBloodSugar() {
		return rawBloodSugar;
	}

	public void setRawBloodSugar(String rawBloodSugar) {
		this.rawBloodSugar = rawBloodSugar;
	}

	public String getRawCholesterol() {
		return rawCholesterol;
	}

	public void setRawCholesterol(String rawCholesterol) {
		this.rawCholesterol = rawCholesterol;
	}

	public String getRawAdmissionDate() {
		return rawAdmissionDate;
	}

	public void setRawAdmissionDate(String rawAdmissionDate) {
		this.rawAdmissionDate = rawAdmissionDate;
	}

	public String getRawDischargeDate() {
		return rawDischargeDate;
	}

	public void setRawDischargeDate(String rawDischargeDate) {
		this.rawDischargeDate = rawDischargeDate;
	}

	public String getRawAdmissionStatus() {
		return rawAdmissionStatus;
	}

	public void setRawAdmissionStatus(String rawAdmissionStatus) {
		this.rawAdmissionStatus = rawAdmissionStatus;
	}

	public String getRawPaymentStatus() {
		return rawPaymentStatus;
	}

	public void setRawPaymentStatus(String rawPaymentStatus) {
		this.rawPaymentStatus = rawPaymentStatus;
	}

	public String getRawInsuranceStatus() {
		return rawInsuranceStatus;
	}

	public void setRawInsuranceStatus(String rawInsuranceStatus) {
		this.rawInsuranceStatus = rawInsuranceStatus;
	}

	public String getRawBillAmount() {
		return rawBillAmount;
	}

	public void setRawBillAmount(String rawBillAmount) {
		this.rawBillAmount = rawBillAmount;
	}

	public String getRawDoctorId() {
		return rawDoctorId;
	}

	public void setRawDoctorId(String rawDoctorId) {
		this.rawDoctorId = rawDoctorId;
	}

    

}
