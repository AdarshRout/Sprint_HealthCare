package com.healthcare.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.config.AppConfig;

public class ConnectionPool {

    private static final Logger LOG =
        LogManager.getLogger(ConnectionPool.class);

    // Singleton instance
    private static volatile ConnectionPool instance;

    private final LinkedBlockingQueue<Connection> pool;
    private final int poolSize;
    private final String url;
    private final String username;
    private final String password;

    // Private constructor — creates all connections upfront
    private ConnectionPool() {
        AppConfig config = AppConfig.getInstance();

        this.url      = config.getDbUrl();
        this.username = config.getDbUsername();
        this.password = config.getDbPassword();
        this.poolSize = config.getDbPoolSize();

        this.pool = new LinkedBlockingQueue<>(poolSize);

        initializePool();
    }

    // Thread-safe singleton
    public static ConnectionPool getInstance() {
        if (instance == null) {
            synchronized (ConnectionPool.class) {
                if (instance == null) {
                    instance = new ConnectionPool();
                }
            }
        }
        return instance;
    }

    /**
     * Creates all connections upfront at startup.
     * Fails fast if DB is unreachable.
     */
    private void initializePool() {
        LOG.info("Initializing connection pool. Size: {}",
            poolSize);

        for (int i = 0; i < poolSize; i++) {
            try {
                Connection conn = createConnection();
                pool.offer(conn);
                LOG.debug("Connection {} created.", i + 1);
            } catch (SQLException e) {
                throw new RuntimeException(
                    "Failed to create connection " +
                    (i + 1) + " during pool init.", e);
            }
        }

        LOG.info("Connection pool ready. " +
                 "Available connections: {}",
            pool.size());
    }

    /**
     * Creates a single JDBC connection.
     */
    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(
            url, username, password);
    }

    /**
     * Borrows a connection from the pool.
     * Blocks if none available — waits up to 30 seconds.
     * Throws RuntimeException if timeout exceeded.
     */
    public Connection getConnection() {
        try {
            Connection conn = pool.poll(
                30, TimeUnit.SECONDS);

            if (conn == null) {
                throw new RuntimeException(
                    "Timed out waiting for a DB connection. " +
                    "Pool size: " + poolSize);
            }

            // DEFENSIVE: connection might have gone stale
            if (!isConnectionValid(conn)) {
                LOG.warn("Stale connection detected. " +
                         "Replacing...");
                conn = createConnection();
            }

            LOG.debug("Connection borrowed. " +
                      "Remaining in pool: {}",
                pool.size());
            return conn;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(
                "Interrupted while waiting " +
                "for DB connection.", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                "Failed to create replacement connection.", e);
        }
    }

    /**
     * Returns a connection back to the pool.
     * Never closes the connection — just returns it.
     */
    public void releaseConnection(Connection conn) {
        if (conn == null) {
            LOG.warn("Attempted to release null connection.");
            return;
        }

        // DEFENSIVE: don't return stale connections
        if (!isConnectionValid(conn)) {
            LOG.warn("Stale connection discarded on release. " +
                     "Creating replacement...");
            try {
                conn = createConnection();
            } catch (SQLException e) {
                LOG.error("Failed to create replacement " +
                          "on release: {}", e.getMessage());
                return;
            }
        }

        boolean returned = pool.offer(conn);
        if (!returned) {
            LOG.warn("Pool is full. " +
                     "Closing excess connection.");
            closeQuietly(conn);
        }

        LOG.debug("Connection returned. " +
                  "Available in pool: {}",
            pool.size());
    }

    /**
     * Checks if a connection is still valid.
     * Uses 2 second timeout.
     */
    private boolean isConnectionValid(Connection conn) {
        try {
            return conn != null && conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Closes all connections in the pool.
     * Call this when application shuts down.
     */
    public void shutdown() {
        LOG.info("Shutting down connection pool...");
        int closed = 0;

        for (Connection conn : pool) {
            closeQuietly(conn);
            closed++;
        }

        pool.clear();
        LOG.info("Connection pool shut down. " +
                 "Closed {} connections.", closed);
    }

    /**
     * Closes a connection silently — no exception thrown.
     */
    private void closeQuietly(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            LOG.warn("Error closing connection: {}",
                e.getMessage());
        }
    }

    /**
     * Returns current available connections count.
     * Useful for monitoring and testing.
     */
    public int getAvailableConnections() {
        return pool.size();
    }
}
