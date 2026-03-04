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
import com.healthcare.util.NumericValidatorUtil;

public class VisitDAO {

    private static final Logger LOG =
        LogManager.getLogger(VisitDAO.class);

    private static final String INSERT_SQL =
        "INSERT INTO visits " +
        "(patient_id, doctor_id, admission_date, " +
        " discharge_date, admission_status, " +
        " patient_status, length_of_stay) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    /**
     * Inserts one visit record.
     * Returns generated visit_id.
     * Returns -1 if insert fails.
     */
    public int insert(
            PatientRawRecord record, int patientId) {

        Connection conn = null;

        try {
            conn = ConnectionPool.getInstance()
                .getConnection();

            PreparedStatement stmt = conn.prepareStatement(
                INSERT_SQL,
                PreparedStatement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, patientId);
            stmt.setString(2, record.getRawDoctorId());
            stmt.setDate(3, parseDate(
                record.getRawAdmissionDate()));
            stmt.setDate(4, parseDate(
                record.getRawDischargeDate()));
            stmt.setString(5, nullIfUnknown(
                record.getRawAdmissionStatus()));
            stmt.setString(6, nullIfUnknown(
                record.getRawPatientStatus()));
            stmt.setObject(7, parseIntLos(
                record.getLengthOfStayDays()));

            int rows = stmt.executeUpdate();

            if (rows == 0) return -1;

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int visitId = keys.getInt(1);
                LOG.debug("VisitDAO | patient_id={} | " +
                          "Inserted visit_id={}",
                    patientId, visitId);
                return visitId;
            }

            return -1;

        } catch (SQLException e) {
            LOG.error("VisitDAO | patient_id={} | " +
                      "Insert failed: {}",
                patientId, e.getMessage());
            return -1;

        } finally {
            if (conn != null) {
                ConnectionPool.getInstance()
                    .releaseConnection(conn);
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────

    private Date parseDate(String raw) {
        Optional<LocalDate> parsed =
            DateParserUtil.parse(raw);
        return parsed.map(Date::valueOf).orElse(null);
    }

    private Integer parseIntLos(String value) {
        if (value == null || value.equals("INVALID")) {
            return null;
        }
        return NumericValidatorUtil.parseDouble(value)
            .stream().boxed()
            .findFirst()
            .map(Double::intValue)
            .orElse(null);
    }

    private String nullIfUnknown(String value) {
        if (value == null ||
                value.equals("UNKNOWN")) return null;
        return value;
    }
}
