package com.healthcare.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.db.ConnectionPool;
import com.healthcare.model.AggregationResult;

public class AggregationDAO {

    private static final Logger LOG =
        LogManager.getLogger(AggregationDAO.class);

    private static final String INSERT_DEPT =
        "INSERT INTO aggregation_by_department " +
        "(department, total_patients, " +
        " avg_bill_amount, avg_length_of_stay) " +
        "VALUES (?, ?, ?, ?)";

    private static final String INSERT_DOCTOR =
        "INSERT INTO aggregation_by_doctor " +
        "(doctor_id, total_patients, avg_bill_amount) " +
        "VALUES (?, ?, ?)";

    private static final String INSERT_DIAGNOSIS =
        "INSERT INTO aggregation_by_diagnosis " +
        "(diagnosis, total_patients, avg_blood_sugar) " +
        "VALUES (?, ?, ?)";

    public void insertDepartmentAggregations(
            List<AggregationResult> results) {

        Connection conn = null;
        int inserted = 0;

        try {
            conn = ConnectionPool.getInstance()
                .getConnection();

            PreparedStatement stmt =
                conn.prepareStatement(INSERT_DEPT);

            for (AggregationResult r : results) {
                stmt.setString(1, r.getGroupKey());
                stmt.setInt(2, r.getTotalPatients());
                stmt.setDouble(3, r.getAvgBillAmount());
                stmt.setDouble(4, r.getAvgLengthOfStay());
                stmt.addBatch();
                inserted++;
            }

            stmt.executeBatch();
            LOG.info("AggregationDAO | Department | " +
                     "Inserted: {}", inserted);

        } catch (SQLException e) {
            LOG.error("AggregationDAO | Department | " +
                      "Failed: {}", e.getMessage());
        } finally {
            if (conn != null) {
                ConnectionPool.getInstance()
                    .releaseConnection(conn);
            }
        }
    }

    public void insertDoctorAggregations(
            List<AggregationResult> results) {

        Connection conn = null;
        int inserted = 0;

        try {
            conn = ConnectionPool.getInstance()
                .getConnection();

            PreparedStatement stmt =
                conn.prepareStatement(INSERT_DOCTOR);

            for (AggregationResult r : results) {
                stmt.setString(1, r.getGroupKey());
                stmt.setInt(2, r.getTotalPatients());
                stmt.setDouble(3, r.getAvgBillAmount());
                stmt.addBatch();
                inserted++;
            }

            stmt.executeBatch();
            LOG.info("AggregationDAO | Doctor | " +
                     "Inserted: {}", inserted);

        } catch (SQLException e) {
            LOG.error("AggregationDAO | Doctor | " +
                      "Failed: {}", e.getMessage());
        } finally {
            if (conn != null) {
                ConnectionPool.getInstance()
                    .releaseConnection(conn);
            }
        }
    }

    public void insertDiagnosisAggregations(
            List<AggregationResult> results) {

        Connection conn = null;
        int inserted = 0;

        try {
            conn = ConnectionPool.getInstance()
                .getConnection();

            PreparedStatement stmt =
                conn.prepareStatement(INSERT_DIAGNOSIS);

            for (AggregationResult r : results) {
                stmt.setString(1, r.getGroupKey());
                stmt.setInt(2, r.getTotalPatients());
                stmt.setDouble(3, r.getAvgBloodSugar());
                stmt.addBatch();
                inserted++;
            }

            stmt.executeBatch();
            LOG.info("AggregationDAO | Diagnosis | " +
                     "Inserted: {}", inserted);

        } catch (SQLException e) {
            LOG.error("AggregationDAO | Diagnosis | " +
                      "Failed: {}", e.getMessage());
        } finally {
            if (conn != null) {
                ConnectionPool.getInstance()
                    .releaseConnection(conn);
            }
        }
    }
}