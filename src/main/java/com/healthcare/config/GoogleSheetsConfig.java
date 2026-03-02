package com.healthcare.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

public class GoogleSheetsConfig {

    private static final Logger LOG = 
        LogManager.getLogger(GoogleSheetsConfig.class);

    private static final String APP_NAME = "healthcare-pipeline";

    private static final List<String> SCOPES = 
        Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);

    // Singleton Sheets service
    private static volatile Sheets sheetsService;

    // Utility class - no instantiation
    private GoogleSheetsConfig() {}

    public static Sheets getSheetsService() {
        if (sheetsService == null) {
            synchronized (GoogleSheetsConfig.class) {
                if (sheetsService == null) {
                    sheetsService = buildSheetsService();
                }
            }
        }
        return sheetsService;
    }

    private static Sheets buildSheetsService() {
        LOG.info("Building Google Sheets API service...");

        String credentialsPath = AppConfig.getInstance()
            .getCredentialsPath();

        try {
            GoogleCredentials credentials = 
                loadCredentials(credentialsPath);

            Sheets service = new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
            )
            .setApplicationName(APP_NAME)
            .build();

            LOG.info("Google Sheets service built successfully.");
            return service;

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(
                "SSL error building Sheets service.", e);

        } catch (IOException e) {
            throw new RuntimeException(
                "IO error building Sheets service. " +
                "Check credentials path: " + credentialsPath, e);
        }
    }

    private static GoogleCredentials loadCredentials(
            String credentialsPath) throws IOException {

        // Try 1: load from file system path
        try (InputStream is = new FileInputStream(credentialsPath)) {
            LOG.info("Loading credentials from: {}", credentialsPath);
            return GoogleCredentials
                .fromStream(is)
                .createScoped(SCOPES);

        } catch (IOException e) {
            LOG.warn("Could not load from file path. " +
                     "Trying classpath...");
        }

        // Try 2: load from classpath (src/main/resources)
        try (InputStream is = GoogleSheetsConfig.class
                .getClassLoader()
                .getResourceAsStream("credentials.json")) {

            if (is == null) {
                throw new RuntimeException(
                    "credentials.json not found at '" + 
                    credentialsPath + "' or on classpath. " +
                    "Please complete Google Cloud setup."
                );
            }

            LOG.info("Credentials loaded from classpath.");
            return GoogleCredentials
                .fromStream(is)
                .createScoped(SCOPES);
        }
    }
}
