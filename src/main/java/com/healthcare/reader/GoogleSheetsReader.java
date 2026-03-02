package com.healthcare.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.healthcare.config.AppConfig;
import com.healthcare.config.GoogleSheetsConfig;
import com.healthcare.model.PatientRawRecord;

public class GoogleSheetsReader {

    private static final Logger LOG = 
        LogManager.getLogger(GoogleSheetsReader.class);

    private final AppConfig config;
    private final Sheets sheetsService;

    public GoogleSheetsReader() {
        this.config = AppConfig.getInstance();
        this.sheetsService = GoogleSheetsConfig.getSheetsService();
        LOG.info("GoogleSheetsReader initialized.");
    }

    // Reads ALL rows from sheet (skipping header)
    public List<PatientRawRecord> readAll() {
        LOG.info("Reading all rows from sheet tab: '{}'",
            config.getSheetTab());
        return readRows(2); // row 1 is header
    }

    // Reads only NEW rows since last processed row
    public List<PatientRawRecord> readNewRowsSince(
            int lastProcessedRow) {

        // DEFENSIVE: invalid row number
        if (lastProcessedRow < 1) {
            LOG.warn("lastProcessedRow={} is invalid. " +
                     "Defaulting to 1.", lastProcessedRow);
            lastProcessedRow = 1;
        }

        int startRow = lastProcessedRow + 1;
        LOG.info("Reading new rows starting from row {}.", 
            startRow);
        return readRows(startRow);
    }

    private List<PatientRawRecord> readRows(int startRow) {

        // DEFENSIVE: startRow must be positive
        if (startRow < 1) {
            throw new RuntimeException(
                "startRow must be >= 1. Got: " + startRow);
        }

        String range = buildRange(startRow);
        LOG.debug("Fetching range: '{}'", range);

        try {
            ValueRange response = sheetsService
                .spreadsheets()
                .values()
                .get(config.getSheetId(), range)
                .execute();

            List<List<Object>> rawRows = response.getValues();

            // DEFENSIVE: null or empty response
            if (rawRows == null || rawRows.isEmpty()) {
                LOG.info("No new rows found in range: {}", range);
                return Collections.emptyList();
            }

            LOG.info("Fetched {} raw rows.", rawRows.size());
            return parseRows(rawRows, startRow);

        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to read from Google Sheets. " +
                "Range: " + range, e);
        }
    }

    private List<PatientRawRecord> parseRows(
            List<List<Object>> rawRows, int startRowNumber) {

        // DEFENSIVE: null check
        Objects.requireNonNull(rawRows, 
            "rawRows must not be null");

        List<PatientRawRecord> records = new ArrayList<>();
        int skipped = 0;

        for (int i = 0; i < rawRows.size(); i++) {
            List<Object> row = rawRows.get(i);
            int rowNumber = startRowNumber + i;

            // DEFENSIVE: null row
            if (row == null) {
                LOG.warn("Row {} is null — skipping.", rowNumber);
                skipped++;
                continue;
            }

            PatientRawRecord record = 
                PatientRawRecord.fromSheetRow(row, rowNumber);

            // Skip completely empty rows
            if (record.isCompletelyEmpty()) {
                LOG.warn("Row {} is empty — skipping.", rowNumber);
                skipped++;
                continue;
            }

            records.add(record);
            LOG.debug("Row {} parsed → {}", rowNumber, record);
        }

        LOG.info("Parsed {} valid records. Skipped {} rows.",
            records.size(), skipped);

        return records;
    }

    private String buildRange(int startRow) {
        String tab = config.getSheetTab();
        String range = config.getSheetRange(); // "A:W"
        String startCol = range.split(":")[0]; // "A"
        String endCol = range.split(":")[1];   // "W"

        // Format: 'Form Responses 1'!A2:W
        return String.format("'%s'!%s%d:%s",
            tab, startCol, startRow, endCol);
    }
}
