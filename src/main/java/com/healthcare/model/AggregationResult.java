package com.healthcare.model;

/**
 * Holds one row of aggregation output.
 * Used for UC-9 grouping results.
 */
public class AggregationResult {

    private String groupKey;      // e.g. "Cardiology", "D101"
    private String groupType;     // e.g. "DEPARTMENT", "DOCTOR"
    private int    totalPatients;
    private double avgBillAmount;
    private double avgLengthOfStay;
    private double avgBloodSugar;

    public AggregationResult(String groupKey, String groupType) {
        this.groupKey  = groupKey;
        this.groupType = groupType;
    }

    @Override
    public String toString() {
        return "AggregationResult{" +
               "groupType='" + groupType + "'" +
               ", groupKey='" + groupKey + "'" +
               ", totalPatients=" + totalPatients +
               ", avgBill=" + String.format("%.2f", avgBillAmount) +
               ", avgLOS=" + String.format("%.2f", avgLengthOfStay) +
               ", avgBloodSugar=" + String.format("%.2f", avgBloodSugar) +
               "}";
    }

    // ── Getters and Setters ───────────────────────────────
    public String getGroupKey() { return groupKey; }
    public void setGroupKey(String groupKey) { this.groupKey = groupKey; }

    public String getGroupType() { return groupType; }
    public void setGroupType(String groupType) { this.groupType = groupType; }

    public int getTotalPatients() { return totalPatients; }
    public void setTotalPatients(int totalPatients) { this.totalPatients = totalPatients; }

    public double getAvgBillAmount() { return avgBillAmount; }
    public void setAvgBillAmount(double avgBillAmount) { this.avgBillAmount = avgBillAmount; }

    public double getAvgLengthOfStay() { return avgLengthOfStay; }
    public void setAvgLengthOfStay(double avgLengthOfStay) { this.avgLengthOfStay = avgLengthOfStay; }

    public double getAvgBloodSugar() { return avgBloodSugar; }
    public void setAvgBloodSugar(double avgBloodSugar) { this.avgBloodSugar = avgBloodSugar; }
}