package com.xempre.pressurelesshealth.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Measurement {
    @JsonProperty("user")
    private int idUser;
    @JsonProperty("systolic_pressure")
    private float systolicPressure;
    @JsonProperty("diastolic_pressure")
    private float diastolicPressure;
    @JsonProperty("measurement_date")
    private String measurementDate;
    @JsonProperty("used_recommended_method")
    private boolean isAdvancedMethod;
    @JsonProperty("description")
    private String description;

    @JsonProperty("completed_challenges")
    private Challenge[] completedChallenges;

    @JsonProperty("failed_challenges")
    private Challenge[] failedChallenges;

    @JsonProperty("comments")
    private String comments;
    public Measurement() {
    }
    public Measurement(int id, float sr, float dr, String measurementDate, boolean isAdvancedMethod){
        this.idUser = id;
        this.systolicPressure = sr;
        this.diastolicPressure = dr;
        this.measurementDate = measurementDate;
        this.isAdvancedMethod = isAdvancedMethod;
    }

    public Measurement(Measurement measurement){
        this.idUser = measurement.getUserId();
        this.systolicPressure = measurement.getSystolicRecord();
        this.diastolicPressure = measurement.getDiastolicRecord();
        this.measurementDate = measurement.getMeasurementDate();
        this.isAdvancedMethod = measurement.getIsAdvanced();
    }

    public Challenge[] getFailedChallenges() {
        return failedChallenges;
    }

    public Challenge[] getCompletedChallenges() {
        return completedChallenges;
    }

    public void setCompletedChallenges(Challenge[] completedChallenges) {
        this.completedChallenges = completedChallenges;
    }

    public void setFailedChallenges(Challenge[] failedChallenges) {
        this.failedChallenges = failedChallenges;
    }

    public String getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(String measurementDate) {
        this.measurementDate = measurementDate;
    }

    public int getUserId() {
        return idUser;
    }

    public boolean getIsAdvanced() {
        return isAdvancedMethod;
    }

    public void setIsAdvanced(boolean isAdvancedMethod) {
        this.isAdvancedMethod = isAdvancedMethod;
    }

    public float getDiastolicRecord() {
        return diastolicPressure;
    }

    public float getSystolicRecord() {
        return systolicPressure;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String categorizeBloodPressure() {
        if (systolicPressure > 180 || diastolicPressure > 120) {
            return "HYPERTENSIVE_CRISIS";
        } else if (systolicPressure >= 140 || diastolicPressure >= 90) {
            return "HYPERTENSION_STAGE_2";
        } else if ((systolicPressure >= 130 && systolicPressure <= 139) || (diastolicPressure >= 80 && diastolicPressure <= 89)) {
            return "HYPERTENSION_STAGE_1";
        } else if (systolicPressure >= 120 && systolicPressure <= 129 && diastolicPressure < 80) {
            return "ELEVATED";
        } else if (systolicPressure < 120 && diastolicPressure < 80) {
            return "NORMAL";
        } else {
            return "Unknown category";
        }
    }
}
