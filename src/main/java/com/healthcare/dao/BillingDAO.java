package com.healthcare.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.db.ConnectionPool;
import com.healthcare.model.PatientRawRecord;
import com.healthcare.util.NumericValidatorUtil;

public class BillingDAO {

    private static final Logger LOG =
        LogManager.getLogger(BillingDAO.class);

    private static final String INSERT_SQL =
        "INSERT INTO billing " +
        "(visit_id, patient_id, bill_amount, " +
        " payment_status, insurance_status) " +
        "VALUES (?, ?, ?, ?, ?)";

    /**
     * Inserts one billing record.
     * Requires both visit_id and patient_id.
     */
    public boolean insert(
            PatientRawRecord record,
            int patientId,
            int visitId) {

        Connection conn = null;

        try {
            conn = ConnectionPool.getInstance()
                .getConnection();

            PreparedStatement stmt = conn.prepareStatement(
                INSERT_SQL);

            stmt.setInt(1, visitId);
            stmt.setInt(2, patientId);
            stmt.setObject(3, parseDouble(
                record.getRawBillAmount()));
            stmt.setString(4, nullIfUnknown(
                record.getRawPaymentStatus()));
            stmt.setString(5, nullIfUnknown(
                record.getRawInsuranceStatus()));

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            LOG.error("BillingDAO | patient_id={} | " +
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