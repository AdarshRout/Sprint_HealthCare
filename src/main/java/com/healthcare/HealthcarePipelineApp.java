package com.healthcare;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthcare.model.PatientRawRecord;
import com.healthcare.reader.GoogleSheetsReader;
import com.healthcare.service.DuplicateService;
import com.healthcare.service.NameNormalizationService;

public class HealthcarePipelineApp {

	private static final Logger LOG = LogManager.getLogger(HealthcarePipelineApp.class);

	public static void main(String[] args) {

		LOG.info("========================================");
		LOG.info("  Healthcare Pipeline — Starting       ");
		LOG.info("========================================");

		// Step 1: Read from Google Sheets
		GoogleSheetsReader reader = new GoogleSheetsReader();
		List<PatientRawRecord> rawRecords = reader.readAll();
		LOG.info("Total records fetched: {}", rawRecords.size());

		// Step 2: UC-1 Remove Duplicates
		DuplicateService duplicateService = new DuplicateService();
		List<PatientRawRecord> uniqueRecords = duplicateService.removeDuplicates(rawRecords);
		LOG.info("After dedup: {}", uniqueRecords.size());

		// Step 3: UC-2 Normalize Names
		NameNormalizationService nameService = new NameNormalizationService();
		List<PatientRawRecord> namedRecords = nameService.normalizeNames(uniqueRecords);
		LOG.info("After name normalization: {}", namedRecords.size());

		LOG.info("========================================");
		LOG.info("  Pipeline test complete               ");
		LOG.info("========================================");
	}
}
