package com.healthcare.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.db.ConnectionPool;
import com.healthcare.model.PatientRawRecord;
import com.healthcare.util.NumericValidatorUtil;

public class ClinicalRecordDAO {

    private static final Logger LOG =
        LogManager.getLogger(ClinicalRecordDAO.class);

    private static final String INSERT_SQL =
        "INSERT INTO clinical_records " +
        "(patient_id, department, diagnosis, " +
        " systolic, diastolic, weight_kg, height_cm, " +
        " blood_sugar, cholesterol, bmi, map_value, " +
        " bmi_category, blood_sugar_cat, " +
        " cholesterol_cat) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Inserts one clinical record.
     * Requires patient_id from PatientDAO.
     */
    public boolean insert(
            PatientRawRecord record, int patientId) {

        Connection conn = null;

        try {
            conn = ConnectionPool.getInstance()
                .getConnection();

            PreparedStatement stmt = conn.prepareStatement(
                INSERT_SQL);

            stmt.setInt(1, patientId);
            stmt.setString(2, nullIfUnknown(
                record.getRawDepartment()));
            stmt.setString(3, nullIfUnknown(
                record.getRawDiagnosis()));
            stmt.setObject(4, parseDouble(
                record.getRawSystolic()));
            stmt.setObject(5, parseDouble(
                record.getRawDiastolic()));
            stmt.setObject(6, parseDouble(
                record.getRawWeightKg()));
            stmt.setObject(7, parseDouble(
                record.getRawHeightCm()));
            stmt.setObject(8, parseDouble(
                record.getRawBloodSugar()));
            stmt.setObject(9, parseDouble(
                record.getRawCholesterol()));
            stmt.setObject(10, parseDouble(
                record.getBmi()));
            stmt.setObject(11, parseDouble(
                record.getMap()));
            stmt.setString(12, nullIfUnknown(
                record.getBmiCategory()));
            stmt.setString(13, nullIfUnknown(
                record.getBloodSugarCategory()));
            stmt.setString(14, nullIfUnknown(
                record.getCholesterolCategory()));

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            LOG.error("ClinicalRecordDAO | patient_id={} | " +
                      "Insert failed: {}",
                patientId, e.getMessage());
            return false;

        } finally {
            if (conn != null) {
                ConnectionPool.getInstance()
                    .releaseConnection(conn);
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────

    /**
     * Parses numeric string to Double.
     * Returns null if INVALID or unparseable.
     * Using setObject(index, null) stores NULL in MySQL.
     */
    private Double parseDouble(String value) {
        if (value == null || value.equals("INVALID")) {
            return null;
        }
        return NumericValidatorUtil.parseDouble(value)
            .stream().boxed()
            .findFirst().orElse(null);
    }

    private String nullIfUnknown(String value) {
        if (value == null ||
                value.equals("UNKNOWN")) return null;
        return value;
    }
}
