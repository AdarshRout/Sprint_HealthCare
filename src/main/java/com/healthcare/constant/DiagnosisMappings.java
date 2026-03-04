package com.healthcare.constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DiagnosisMappings {

    private static final Logger LOG =
        LogManager.getLogger(DiagnosisMappings.class);

    private static final String ICD_FILE = "icd10cm_codes_2026.txt";

    // Loaded once at class initialization
    private static final Map<String, String> ICD_MAP =
        loadFromFile();

    private DiagnosisMappings() {}

    /**
     * Returns the full ICD-10 code → disease name map.
     * Loaded once from icd10cm_codes.txt at startup.
     */
    public static Map<String, String> getIcdMap() {
        return ICD_MAP;
    }

    /**
     * Loads ICD-10 codes from txt file on classpath.
     *
     * File format (tab separated):
     *   CODE\tDESCRIPTION
     *   I10\tEssential (primary) hypertension
     */
    private static Map<String, String> loadFromFile() {
        LOG.info("Loading ICD-10 codes from {}...", ICD_FILE);

        Map<String, String> map = new HashMap<>();

        // DEFENSIVE: load from classpath
        try (InputStream is = DiagnosisMappings.class
                .getClassLoader()
                .getResourceAsStream(ICD_FILE)) {

            // DEFENSIVE: file not found
            if (is == null) {
                LOG.error("ICD-10 file '{}' not found " +
                    "on classpath. Diagnosis mapping " +
                    "will return UNKNOWN for all codes.",
                    ICD_FILE);
                return Collections.emptyMap();
            }

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, "UTF-8"));

            String line;
            int loaded = 0;
            int skipped = 0;

            while ((line = reader.readLine()) != null) {

                // DEFENSIVE: skip blank lines
                if (line.trim().isEmpty()) {
                    skipped++;
                    continue;
                }

                // Code is first word, description is rest
                // Format: "A000    Cholera due to Vibrio..."
                int firstSpace = line.indexOf(' ');

                // DEFENSIVE: no space found — malformed line
                if (firstSpace == -1) {
                    LOG.warn("Skipping malformed line: '{}'", line);
                    skipped++;
                    continue;
                }

                String[] parts = new String[]{
                    line.substring(0, firstSpace).trim(),
                    line.substring(firstSpace).trim()
                };

                String code = parts[0].trim().toUpperCase();
                String description = parts[1].trim();

                // DEFENSIVE: empty code or description
                if (code.isEmpty() || description.isEmpty()) {
                    skipped++;
                    continue;
                }

                map.put(code, description);
                loaded++;
            }

            LOG.info("ICD-10 codes loaded. " +
                     "Total: {} | Skipped: {}",
                loaded, skipped);

        } catch (IOException e) {
            LOG.error("Failed to load ICD-10 file: {}",
                e.getMessage(), e);
            return Collections.emptyMap();
        }

        return Collections.unmodifiableMap(map);
    }
}
