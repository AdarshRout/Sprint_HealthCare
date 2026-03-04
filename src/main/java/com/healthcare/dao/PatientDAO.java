package com.healthcare.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.db.ConnectionPool;
import com.healthcare.model.PatientRawRecord;
import com.healthcare.util.DateParserUtil;

public class PatientDAO {

    private static final Logger LOG =
        LogManager.getLogger(PatientDAO.class);

    private static final String INSERT_SQL =
        "INSERT IGNORE INTO patients " +
        "(first_name, last_name, dob, gender, " +
        " phone, email, blood_group, age_group, " +
        " sheet_row_number) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Inserts one patient record.
     * Returns generated patient_id.
     * Returns -1 if insert fails.
     */
    public int insert(PatientRawRecord record) {

        Connection conn = null;

        try {
            conn = ConnectionPool.getInstance()
                .getConnection();

            PreparedStatement stmt = conn.prepareStatement(
                INSERT_SQL,
                PreparedStatement.RETURN_GENERATED_KEYS);

            // Set parameters
            stmt.setString(1, record.getRawFirstName());
            stmt.setString(2, record.getRawLastName());
            stmt.setDate(3, parseDate(record.getRawDob()));
            stmt.setString(4, nullIfUnknown(
                record.getRawGender()));
            stmt.setString(5, nullIfInvalid(
                record.getRawPhone()));
            stmt.setString(6, nullIfInvalid(
                record.getRawEmail()));
            stmt.setString(7, nullIfUnknown(
                record.getRawBloodGroup()));
            stmt.setString(8, nullIfUnknown(
                record.getAgeGroup()));
            stmt.setInt(9, record.getSheetRowNumber());

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                // INSERT IGNORE skipped this — already exists in DB
                LOG.warn("PatientDAO | Row {} | " +
                         "Duplicate detected — skipped insert.",
                    record.getSheetRowNumber());
                return -1;
            }

            // Get generated patient_id
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int patientId = keys.getInt(1);
                LOG.debug("PatientDAO | Row {} | " +
                          "Inserted patient_id={}",
                    record.getSheetRowNumber(), patientId);
                return patientId;
            }

            return -1;

        } catch (SQLException e) {
            LOG.error("PatientDAO | Row {} | " +
                      "Insert failed: {}",
                record.getSheetRowNumber(),
                e.getMessage());
            return -1;

        } finally {
            if (conn != null) {
                ConnectionPool.getInstance()
                    .releaseConnection(conn);
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────

    /**
     * Parses a yyyy-MM-dd string to java.sql.Date.
     * Returns null if parsing fails.
     */
    private Date parseDate(String raw) {
        Optional<LocalDate> parsed =
            DateParserUtil.parse(raw);
        return parsed.map(Date::valueOf).orElse(null);
    }

    /**
     * Returns null if value is "INVALID".
     * MySQL stores NULL instead of the string "INVALID".
     */
    private String nullIfInvalid(String value) {
        if (value == null ||
                value.equals("INVALID")) return null;
        return value;
    }

    /**
     * Returns null if value is "UNKNOWN".
     */
    private String nullIfUnknown(String value) {
        if (value == null ||
                value.equals("UNKNOWN")) return null;
        return value;
    }
}
