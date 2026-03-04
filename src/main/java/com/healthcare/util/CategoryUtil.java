package com.healthcare.util;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.OptionalDouble;

public class CategoryUtil {

    private CategoryUtil() {}

    /**
     * Categorizes BMI value.
     *
     * < 18.5  → Underweight
     * 18.5–24.9 → Normal
     * 25.0–29.9 → Overweight
     * >= 30.0 → Obese
     * INVALID → UNKNOWN
     */
    public static String categorizeBmi(String bmi) {

        OptionalDouble parsed =
            NumericValidatorUtil.parseDouble(bmi);

        if (parsed.isEmpty() ||
                bmi == null ||
                bmi.equals("INVALID")) {
            return "UNKNOWN";
        }

        double value = parsed.getAsDouble();

        if (value < 18.5)  return "Underweight";
        if (value < 25.0)  return "Normal";
        if (value < 30.0)  return "Overweight";
        return "Obese";
    }

    /**
     * Categorizes Blood Sugar (mg/dL).
     *
     * < 70    → Low
     * 70–99   → Normal
     * 100–125 → Pre-Diabetic
     * >= 126  → Diabetic
     * INVALID → UNKNOWN
     */
    public static String categorizeBloodSugar(
            String bloodSugar) {

        OptionalDouble parsed =
            NumericValidatorUtil.parseDouble(bloodSugar);

        if (parsed.isEmpty() ||
                bloodSugar == null ||
                bloodSugar.equals("INVALID")) {
            return "UNKNOWN";
        }

        double value = parsed.getAsDouble();

        if (value < 70)   return "Low";
        if (value < 100)  return "Normal";
        if (value < 126)  return "Pre-Diabetic";
        return "Diabetic";
    }

    /**
     * Categorizes Cholesterol (mg/dL).
     *
     * < 200   → Optimal
     * 200–239 → Borderline
     * >= 240  → High
     * INVALID → UNKNOWN
     */
    public static String categorizeСholesterol(
            String cholesterol) {

        OptionalDouble parsed =
            NumericValidatorUtil.parseDouble(cholesterol);

        if (parsed.isEmpty() ||
                cholesterol == null ||
                cholesterol.equals("INVALID")) {
            return "UNKNOWN";
        }

        double value = parsed.getAsDouble();

        if (value < 200)  return "Optimal";
        if (value < 240)  return "Borderline";
        return "High";
    }

    /**
     * Categorizes Age from DOB string (yyyy-MM-dd).
     *
     * 0–12    → Child
     * 13–17   → Teenager
     * 18–59   → Adult
     * >= 60   → Senior
     * INVALID → UNKNOWN
     */
    public static String categorizeAge(String dob) {

        if (dob == null ||
                dob.trim().isEmpty() ||
                dob.equals("INVALID")) {
            return "UNKNOWN";
        }

        Optional<LocalDate> parsed =
            DateParserUtil.parse(dob);

        if (parsed.isEmpty()) {
            return "UNKNOWN";
        }

        int age = Period.between(
            parsed.get(), LocalDate.now()).getYears();

        // DEFENSIVE: negative age
        if (age < 0)   return "UNKNOWN";

        if (age <= 12) return "Child";
        if (age <= 17) return "Teenager";
        if (age <= 59) return "Adult";
        return "Senior";
    }
}