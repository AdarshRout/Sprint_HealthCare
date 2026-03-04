package com.healthcare.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.OptionalDouble;

public class DerivedFieldsUtil {

    private DerivedFieldsUtil() {}

    /**
     * Calculates BMI from weight and height.
     * Formula: weight(kg) / (height(m))²
     * Returns rounded to 2 decimal places.
     * Returns "INVALID" if either input is invalid.
     *
     * Healthy BMI range: 18.5 – 24.9
     * We accept 10.0 – 70.0 as plausible range.
     */
    public static String calculateBmi(
            String rawWeight, String rawHeight) {

        OptionalDouble weight =
            NumericValidatorUtil.parseDouble(rawWeight);
        OptionalDouble height =
            NumericValidatorUtil.parseDouble(rawHeight);

        // DEFENSIVE: either field invalid or INVALID string
        if (weight.isEmpty() || height.isEmpty()) {
            return "INVALID";
        }

        if (rawWeight.equals("INVALID") ||
                rawHeight.equals("INVALID")) {
            return "INVALID";
        }

        double heightMetres = height.getAsDouble() / 100.0;

        // DEFENSIVE: avoid division by zero
        if (heightMetres <= 0) {
            return "INVALID";
        }

        double bmi = weight.getAsDouble() /
            (heightMetres * heightMetres);

        // DEFENSIVE: implausible BMI range
        if (bmi < 10.0 || bmi > 70.0) {
            return "INVALID";
        }

        return String.format("%.2f", bmi);
    }

    /**
     * Calculates Mean Arterial Pressure (MAP).
     * Formula: diastolic + (systolic - diastolic) / 3
     * Returns rounded to 2 decimal places.
     * Returns "INVALID" if either input is invalid.
     *
     * Normal MAP range: 70 – 110 mmHg
     * We accept 40 – 180 as plausible.
     */
    public static String calculateMap(
            String rawSystolic, String rawDiastolic) {

        OptionalDouble systolic =
            NumericValidatorUtil.parseDouble(rawSystolic);
        OptionalDouble diastolic =
            NumericValidatorUtil.parseDouble(rawDiastolic);

        if (systolic.isEmpty() || diastolic.isEmpty()) {
            return "INVALID";
        }

        double map = diastolic.getAsDouble() +
            (systolic.getAsDouble() -
             diastolic.getAsDouble()) / 3.0;

        // DEFENSIVE: implausible MAP range
        if (map < 40.0 || map > 180.0) {
            return "INVALID";
        }

        return String.format("%.2f", map);
    }

    /**
     * Calculates Length of Stay in days.
     * Formula: dischargeDate - admissionDate
     * Returns "INVALID" if either date is INVALID.
     * Returns "INVALID" if discharge is before admission.
     */
    public static String calculateLos(
            String rawAdmissionDate,
            String rawDischargeDate) {

        Optional<LocalDate> admission =
            DateParserUtil.parse(rawAdmissionDate);
        Optional<LocalDate> discharge =
            DateParserUtil.parse(rawDischargeDate);

        if (admission.isEmpty() || discharge.isEmpty()) {
            return "INVALID";
        }

        long days = ChronoUnit.DAYS.between(
            admission.get(), discharge.get());

        // DEFENSIVE: discharge before admission
        if (days < 0) {
            return "INVALID";
        }

        // DEFENSIVE: implausibly long stay (> 365 days)
        if (days > 365) {
            return "INVALID";
        }

        return String.valueOf(days);
    }
}