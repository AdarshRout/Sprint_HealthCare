package com.healthcare;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.model.PatientRawRecord;
import com.healthcare.reader.GoogleSheetsReader;

public class HealthcarePipelineApp {

    private static final Logger LOG = 
        LogManager.getLogger(HealthcarePipelineApp.class);

    public static void main(String[] args) {

        LOG.info("========================================");
        LOG.info("  Healthcare Pipeline — Starting       ");
        LOG.info("========================================");

        // Step 1: Create the reader
        GoogleSheetsReader reader = new GoogleSheetsReader();

        // Step 2: Read all rows
        List<PatientRawRecord> records = reader.readAll();

        // Step 3: Print summary
        LOG.info("Total records fetched: {}", records.size());

        // Step 4: Print first 3 records to verify
        int preview = Math.min(3, records.size());
        for (int i = 0; i < preview; i++) {
            LOG.info("Record {}: {}", i + 1, records.get(i));
        }

        LOG.info("========================================");
        LOG.info("  Pipeline test complete               ");
        LOG.info("========================================");
    }
}
