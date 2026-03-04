package com.healthcare.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppConfig {

    private static final Logger LOG = LogManager.getLogger(AppConfig.class);
    private static final String PROPERTIES_FILE = "app.properties";

    // Singleton instance
    private static volatile AppConfig instance;

    private final Properties props;

    // Private constructor
    private AppConfig() {
        this.props = new Properties();
        loadProperties();
    }

    // Thread-safe singleton
    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {

            if (is == null) {
                throw new RuntimeException(
                    "Cannot find " + PROPERTIES_FILE + 
                    " on classpath"
                );
            }

            props.load(is);
            LOG.info("app.properties loaded successfully.");

        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to load " + PROPERTIES_FILE, e
            );
        }
    }

    // ── Getters ───────────────────────────────────────────

    public String getSheetId() {
        return props.getProperty("google.sheet.id");
    }

    public String getSheetTab() {
        return props.getProperty("google.sheet.tab");
    }

    public String getSheetRange() {
        return props.getProperty("google.sheet.range", "A:W");
    }

    public String getCredentialsPath() {
        return props.getProperty("google.credentials.path");
    }

    public int getSchedulerIntervalMinutes() {
        return Integer.parseInt(
            props.getProperty("scheduler.interval.minutes", "5")
        );
    }

    public String getDbUrl() {
        return props.getProperty("db.url");
    }

    public String getDbUsername() {
        return props.getProperty("db.username");
    }

    public String getDbPassword() {
        return props.getProperty("db.password");
    }

    public int getDbPoolSize() {
        return Integer.parseInt(
            props.getProperty("db.pool.size", "10")
        );
    }

    public String getErrorLogPath() {
        return props.getProperty("error.log.path");
    }

    public int getLastProcessedRow() {
        return Integer.parseInt(
            props.getProperty("last.processed.row", "1")
        );
    }

    public void setLastProcessedRow(int row) {
        props.setProperty("last.processed.row", 
            String.valueOf(row));
    }

    /**
     * Persists updated last.processed.row back to
     * app.properties file so it survives JVM restart.
     */
    public void persistLastProcessedRow(int row) {
        props.setProperty("last.processed.row",
            String.valueOf(row));
    
        // Find app.properties on file system
        String propsPath = "src/main/resources/app.properties";
    
        try (java.io.FileOutputStream fos =
                new java.io.FileOutputStream(propsPath)) {
                
            props.store(fos,
                "Updated by pipeline — last processed row");
            
            LOG.info("Persisted last.processed.row={} " +
                     "to app.properties", row);
            
        } catch (java.io.IOException e) {
            LOG.error("Failed to persist last.processed.row: {}",
                e.getMessage());
        }
    }

}
