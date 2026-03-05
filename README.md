# Healthcare Data Pipeline

A Core Java ETL pipeline that reads patient data from Google Sheets,
applies 10 data processing use cases, and persists cleaned and
enriched data into a normalized MySQL database.

---

## Table of Contents

- [Overview](#overview)
- [Requirements](#requirements)
- [Project Structure](#project-structure)
- [Setup Guide](#setup-guide)
  - [1. Clone the Repository](#1-clone-the-repository)
  - [2. Google Cloud Setup](#2-google-cloud-setup)
  - [3. MySQL Setup](#3-mysql-setup)
  - [4. Configure app.properties](#4-configure-appproperties)
  - [5. Build the Project](#5-build-the-project)
- [Running the Pipeline](#running-the-pipeline)
- [Running the Tests](#running-the-tests)
- [Verifying Data in MySQL](#verifying-data-in-mysql)
- [Use Cases](#use-cases)
- [Pipeline Flow](#pipeline-flow)
- [Re-running the Pipeline](#re-running-the-pipeline)

---

## Overview

This project demonstrates a production-style data pipeline built
entirely with Core Java (no Spring Boot). It reads 5,500+ raw
patient records from a Google Form / Google Sheets source, processes
them through 10 sequential use cases, and writes the cleaned and
enriched data into a 7-table normalized MySQL database.

**Key features:**
- Incremental reading — only processes new rows on each run
- In-memory deduplication backed by a database unique constraint
- Custom JDBC connection pool (no external pooling library)
- ICD-10-CM diagnosis code mapping (74,719 codes)
- 266 unit tests across all use cases

---

## Requirements

### Software

| Requirement       | Version      | Notes                              |
|-------------------|--------------|------------------------------------|
| Java (JDK)        | 17 or higher | Must be on system PATH             |
| Maven             | 3.8+         | Must be on system PATH             |
| MySQL Server      | 8.0+         | Running locally or remotely        |
| MySQL Workbench   | Any          | Optional — for running SQL scripts |
| Git               | Any          | For cloning the repository         |

### Google Cloud

| Requirement               | Notes                                       |
|---------------------------|---------------------------------------------|
| Google Cloud account      | Free tier is sufficient                     |
| Google Sheets API enabled | Enabled in Google Cloud Console             |
| OAuth 2.0 credentials     | Downloaded as `credentials.json`            |
| Google Sheet              | Shared with the OAuth client email          |

### MySQL

| Requirement         | Notes                                           |
|---------------------|-------------------------------------------------|
| MySQL running       | Default port 3306                               |
| Database created    | `healthcare_db`                                 |
| User with access    | Username and password configured in properties  |

---

## Project Structure

```
healthcare-pipeline/
├── src/
│   ├── main/
│   │   ├── java/com/healthcare/
│   │   │   ├── HealthcarePipelineApp.java       # Main entry point
│   │   │   ├── config/
│   │   │   │   ├── AppConfig.java               # Singleton config loader
│   │   │   │   └── GoogleSheetsConfig.java      # Sheets API setup
│   │   │   ├── constant/
│   │   │   │   ├── Gender.java                  # UC-6 enums
│   │   │   │   ├── BloodGroup.java
│   │   │   │   ├── Department.java
│   │   │   │   ├── AdmissionStatus.java
│   │   │   │   ├── PaymentStatus.java
│   │   │   │   ├── InsuranceStatus.java
│   │   │   │   └── PatientStatus.java
│   │   │   ├── dao/
│   │   │   │   ├── PatientDAO.java              # patients table
│   │   │   │   ├── ClinicalRecordDAO.java       # clinical_records table
│   │   │   │   ├── VisitDAO.java                # visits table
│   │   │   │   ├── BillingDAO.java              # billing table
│   │   │   │   └── AggregationDAO.java          # aggregation tables
│   │   │   ├── db/
│   │   │   │   ├── ConnectionPool.java          # Custom JDBC pool
│   │   │   │   └── DatabaseWriter.java          # Orchestrates all DAOs
│   │   │   ├── model/
│   │   │   │   ├── PatientRawRecord.java        # 28-field data model
│   │   │   │   └── AggregationResult.java       # Aggregation model
│   │   │   ├── reader/
│   │   │   │   └── GoogleSheetsReader.java      # Reads sheet rows
│   │   │   ├── service/
│   │   │   │   ├── DuplicateService.java        # UC-1
│   │   │   │   ├── NameNormalizationService.java# UC-2
│   │   │   │   ├── NumericFixService.java       # UC-3
│   │   │   │   ├── DateStandardizationService.java # UC-4
│   │   │   │   ├── DiagnosisMappingService.java # UC-5
│   │   │   │   ├── StatusValidationService.java # UC-6
│   │   │   │   ├── ContactValidationService.java# UC-7
│   │   │   │   ├── DerivedFieldsService.java    # UC-8
│   │   │   │   ├── AggregationService.java      # UC-9
│   │   │   │   └── CategorizationService.java   # UC-10
│   │   │   └── util/
│   │   │       ├── DateParserUtil.java
│   │   │       ├── NumericValidatorUtil.java
│   │   │       ├── NameNormalizerUtil.java
│   │   │       ├── ContactValidatorUtil.java
│   │   │       ├── DerivedFieldsUtil.java
│   │   │       ├── CategoryUtil.java
│   │   │       ├── DiagnosisMapperUtil.java
│   │   │       └── DiagnosisMappings.java
│   │   └── resources/
│   │       ├── app.properties                   # All config values
│   │       ├── log4j2.xml                       # Logging config
│   │       ├── credentials.json                 # Google OAuth (git-ignored)
│   │       └── icd10cm_codes_2026.txt           # 74,719 ICD-10 codes
│   └── test/
│       └── java/com/healthcare/                 # 266 unit tests
├── schema.sql                                   # MySQL schema script
├── pom.xml
└── README.md
```

---

## Setup Guide

### 1. Clone the Repository

```bash
git clone https://github.com/<your-username>/healthcare-pipeline.git
cd healthcare-pipeline
```

---

### 2. Google Cloud Setup

#### 2a. Enable the Google Sheets API

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Navigate to **APIs & Services → Library**
4. Search for **Google Sheets API** and click **Enable**

#### 2b. Create OAuth 2.0 Credentials

1. Navigate to **APIs & Services → Credentials**
2. Click **Create Credentials → OAuth 2.0 Client ID**
3. Application type: **Desktop App**
4. Click **Create**, then **Download JSON**
5. Rename the downloaded file to `credentials.json`
6. Place it at:
   ```
   src/main/resources/credentials.json
   ```

> **Important:** `credentials.json` is git-ignored and must never
> be committed to version control.

#### 2c. Authorize the Application (First Run Only)

On the very first run, a browser window will open automatically
asking you to sign in with your Google account and grant access
to Google Sheets. After you approve, a token is saved locally
and subsequent runs will not require browser authorization.

#### 2d. Prepare the Google Sheet

1. Open your Google Sheet containing patient data
2. Ensure data starts from row 2 (row 1 = headers)
3. Columns must be in order **A through X** (24 columns)
4. Copy the **Spreadsheet ID** from the URL:
   ```
   https://docs.google.com/spreadsheets/d/<SPREADSHEET_ID>/edit
   ```
5. Copy the **Sheet tab name** (e.g., `Form Responses 1`)

---

### 3. MySQL Setup

#### 3a. Create the Database and Tables

Open MySQL Workbench or any MySQL client and run the schema script:

```bash
mysql -u root -p < schema.sql
```

Or manually paste and run `schema.sql` contents in MySQL Workbench.

The script creates the `healthcare_db` database with these 7 tables:

| Table                      | Purpose                          |
|----------------------------|----------------------------------|
| `patients`                 | Core patient identity data       |
| `clinical_records`         | Vitals, diagnosis, BMI, MAP      |
| `visits`                   | Admission, discharge, LOS        |
| `billing`                  | Bill amount, payment, insurance  |
| `aggregation_by_department`| Department-level summaries       |
| `aggregation_by_doctor`    | Doctor-level summaries           |
| `aggregation_by_diagnosis` | Diagnosis-level summaries        |

#### 3b. Verify Tables Were Created

```sql
USE healthcare_db;
SHOW TABLES;
```

Expected output:
```
aggregation_by_department
aggregation_by_diagnosis
aggregation_by_doctor
billing
clinical_records
patients
visits
```

---

### 4. Configure app.properties

Open `src/main/resources/app.properties` and fill in all values:

```properties
# ── Google Sheets ──────────────────────────────────────────
sheets.spreadsheet.id=YOUR_SPREADSHEET_ID_HERE
sheets.range.sheet.name=Form Responses 1
sheets.tokens.directory.path=tokens
sheets.application.name=Healthcare Pipeline

# ── Incremental Reading ────────────────────────────────────
# Row number of the LAST row already processed.
# Set to 1 on first run (row 1 = header, data starts row 2).
# Updated automatically after each successful run.
last.processed.row=1

# ── Database ───────────────────────────────────────────────
db.url=jdbc:mysql://localhost:3306/healthcare_db
db.username=root
db.password=YOUR_MYSQL_PASSWORD_HERE
db.pool.size=10
```

> **Note:** `last.processed.row` is managed automatically by the
> pipeline. After each successful run it is updated to point to
> the next unprocessed row. Do not edit it manually unless you
> want to reprocess from a specific row.

---

### 5. Build the Project

```bash
mvn clean compile
```

Expected output:
```
[INFO] BUILD SUCCESS
```

---

## Running the Pipeline

### From an IDE (Eclipse / IntelliJ)

1. Right-click `HealthcarePipelineApp.java`
2. Select **Run As → Java Application**

### From the Command Line

```bash
mvn exec:java -Dexec.mainClass="com.healthcare.HealthcarePipelineApp"
```

### What to Expect — First Run

```
Healthcare Pipeline — Starting
Last processed row: 1
Total records fetched: 5501
After dedup: 5250
After name normalization: 5250
After numeric fix: 5250
After date standardization: 5250
After diagnosis mapping: 5250
After status validation: 5250
After contact validation: 5250
After derived fields: 5250
Department groups: 14 | Doctor groups: 10 | Diagnosis groups: 19
After categorization: 5250

Writing to MySQL database...
DatabaseWriter | Complete | Success: 5250 | Failed: 0

Aggregations written. Dept: 14 | Doctor: 10 | Diagnosis: 19
Persisted last.processed.row=5502 to app.properties
Pipeline Complete!
  Records read:       5501
  Records processed:  5250
  Duplicates removed: 251
  Next run starts from row: 5502
Connection pool shut down. Closed 10 connections.
```

### What to Expect — Subsequent Runs (No New Data)

```
Healthcare Pipeline — Starting
Last processed row: 5503
No new rows to read — start row exceeds sheet size.
Total records fetched: 0
No new records to process. Exiting.
```

No database connections are opened when there is no new data to process.

---

## Running the Tests

### Run All 266 Tests

```bash
mvn test
```

### Run Tests for a Specific Use Case

```bash
# UC-1 Deduplication
mvn test -Dtest=DuplicateServiceTest

# UC-2 Name Normalization
mvn test -Dtest=NameNormalizerUtilTest,NameNormalizationServiceTest

# UC-3 Numeric Validation
mvn test -Dtest=NumericValidatorUtilTest,NumericFixServiceTest

# UC-4 Date Standardization
mvn test -Dtest=DateParserUtilTest,DateStandardizationServiceTest

# UC-5 Diagnosis Mapping
mvn test -Dtest=DiagnosisMapperUtilTest,DiagnosisMappingServiceTest

# UC-6 Status Validation
mvn test -Dtest=StatusValidationServiceTest

# UC-7 Contact Validation
mvn test -Dtest=ContactValidatorUtilTest,ContactValidationServiceTest

# UC-8 Derived Fields
mvn test -Dtest=DerivedFieldsUtilTest,DerivedFieldsServiceTest

# UC-9 Aggregation
mvn test -Dtest=AggregationServiceTest

# UC-10 Categorization
mvn test -Dtest=CategoryUtilTest,CategorizationServiceTest
```

### Expected Test Output

```
[INFO] Tests run: 266, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Test Coverage by Use Case

| Use Case | Tests |
|----------|-------|
| UC-1 Deduplication         |  8  |
| UC-2 Name Normalization     | 23  |
| UC-3 Numeric Validation     | 28  |
| UC-4 Date Standardization   | 23  |
| UC-5 Diagnosis Mapping      | 17  |
| UC-6 Status Validation      | 38  |
| UC-7 Contact Validation     | 31  |
| UC-8 Derived Fields         | 27  |
| UC-9 Aggregation            | 14  |
| UC-10 Categorization        | 46  |
| **Total**                   | **266** |

---

## Verifying Data in MySQL

After the first run, open MySQL Workbench and run these queries
to verify data was inserted correctly.

### Row Counts

```sql
USE healthcare_db;

SELECT COUNT(*) AS patients           FROM patients;
SELECT COUNT(*) AS clinical_records   FROM clinical_records;
SELECT COUNT(*) AS visits             FROM visits;
SELECT COUNT(*) AS billing            FROM billing;
SELECT COUNT(*) AS dept_aggregations  FROM aggregation_by_department;
SELECT COUNT(*) AS doc_aggregations   FROM aggregation_by_doctor;
SELECT COUNT(*) AS diag_aggregations  FROM aggregation_by_diagnosis;
```

Expected results:

| Table                      | Expected Count |
|----------------------------|----------------|
| patients                   | ~5,250         |
| clinical_records           | ~5,250         |
| visits                     | ~5,250         |
| billing                    | ~5,250         |
| aggregation_by_department  | 14             |
| aggregation_by_doctor      | 10             |
| aggregation_by_diagnosis   | 19             |

### Sample Patient Records

```sql
SELECT patient_id, first_name, last_name,
       dob, gender, blood_group, age_group
FROM patients
LIMIT 10;
```

### Join Across Tables

```sql
SELECT
    p.patient_id,
    p.first_name,
    p.last_name,
    cr.department,
    cr.diagnosis,
    cr.bmi_category,
    cr.blood_sugar_cat,
    v.admission_date,
    v.discharge_date,
    v.length_of_stay,
    b.bill_amount,
    b.payment_status
FROM patients p
JOIN clinical_records cr ON p.patient_id = cr.patient_id
JOIN visits v            ON p.patient_id = v.patient_id
JOIN billing b           ON v.visit_id   = b.visit_id
LIMIT 10;
```

### Department Aggregations

```sql
SELECT department,
       total_patients,
       ROUND(avg_bill_amount, 2)    AS avg_bill,
       ROUND(avg_length_of_stay, 2) AS avg_los_days
FROM aggregation_by_department
ORDER BY total_patients DESC;
```

### Duplicate Protection Verification

The `patients` table has a unique constraint on
`(first_name, last_name, dob, phone)`. Re-running the pipeline
will not insert duplicate records — MySQL silently skips them
via `INSERT IGNORE`.

```sql
-- Verify unique constraint exists
SHOW INDEX FROM patients
WHERE Key_name = 'uq_patient_identity';
```

---

## Use Cases

| # | Use Case             | Description                                              | Result            |
|---|----------------------|----------------------------------------------------------|-------------------|
| 1 | Deduplication        | Fingerprint-based HashSet deduplication                  | 251 removed       |
| 2 | Name Normalization   | Title case conversion for first and last names           | 2,547 fixed       |
| 3 | Numeric Validation   | Validates and cleans 7 numeric fields                    | 3,416 fixed       |
| 4 | Date Standardization | Parses 12 date formats → `yyyy-MM-dd`                    | All 5,249 fixed   |
| 5 | Diagnosis Mapping    | Maps raw diagnoses to ICD-10-CM codes (74,719 codes)     | Exact + prefix    |
| 6 | Status Validation    | Validates 7 status fields using type-safe enums          | UNKNOWN flagged   |
| 7 | Contact Validation   | Indian mobile format + email validation                  | 661 + 471 flagged |
| 8 | Derived Fields       | Calculates BMI, MAP, Length of Stay                      | Added to records  |
| 9 | Aggregation          | Stream-based grouping by dept, doctor, diagnosis         | 3 summary tables  |
| 10| Categorization       | BMI, blood sugar, age group, cholesterol categories      | 4 fields added    |

---

## Pipeline Flow

```
Google Sheets (raw data)
        │
        ▼
GoogleSheetsReader.readNewRowsSince(lastRow)
        │
        ▼
UC-1  DuplicateService          → removes duplicate records
        │
        ▼
UC-2  NameNormalizationService  → title case names
        │
        ▼
UC-3  NumericFixService         → validates/cleans numeric fields
        │
        ▼
UC-4  DateStandardizationService → standardizes all dates
        │
        ▼
UC-5  DiagnosisMappingService   → maps to ICD-10-CM codes
        │
        ▼
UC-6  StatusValidationService   → validates 7 status enums
        │
        ▼
UC-7  ContactValidationService  → validates phone and email
        │
        ▼
UC-8  DerivedFieldsService      → calculates BMI, MAP, LOS
        │
        ▼
UC-9  AggregationService        → groups and aggregates
        │
        ▼
UC-10 CategorizationService     → categorizes into buckets
        │
        ▼
DatabaseWriter
  ├── PatientDAO        → patients
  ├── ClinicalRecordDAO → clinical_records
  ├── VisitDAO          → visits
  ├── BillingDAO        → billing
  └── AggregationDAO    → 3 aggregation tables
        │
        ▼
AppConfig.persistLastProcessedRow()
        │
        ▼
ConnectionPool.shutdown()
```

---

## Re-running the Pipeline

The pipeline supports incremental processing out of the box.

- `last.processed.row` in `app.properties` is updated after
  every successful run
- On the next run, only rows added after the last run are read
- If no new rows exist, the pipeline exits immediately without
  opening any database connections
- The database unique constraint on `patients` acts as a safety
  net — duplicate records are silently skipped even if
  `last.processed.row` is reset manually

**To reprocess all data from scratch:**

1. Truncate all tables in MySQL:
   ```sql
   USE healthcare_db;
   SET FOREIGN_KEY_CHECKS = 0;
   TRUNCATE TABLE billing;
   TRUNCATE TABLE visits;
   TRUNCATE TABLE clinical_records;
   TRUNCATE TABLE patients;
   TRUNCATE TABLE aggregation_by_department;
   TRUNCATE TABLE aggregation_by_doctor;
   TRUNCATE TABLE aggregation_by_diagnosis;
   SET FOREIGN_KEY_CHECKS = 1;
   ```

2. Reset `app.properties`:
   ```properties
   last.processed.row=1
   ```

3. Run the pipeline again.
