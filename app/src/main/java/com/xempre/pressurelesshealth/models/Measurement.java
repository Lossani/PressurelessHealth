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
    private boolean isAdvanced;
    public Measurement() {
    }
    public Measurement(int id, float sr, float dr, String measurementDate, boolean isAdvanced){
        this.idUser = id;
        this.systolicPressure = sr;
        this.diastolicPressure = dr;
        this.measurementDate = measurementDate;
        this.isAdvanced = isAdvanced;
    }

    public Measurement(Measurement measurement){
        this.idUser = measurement.getUserId();
        this.systolicPressure = measurement.getSystolicRecord();
        this.diastolicPressure = measurement.getDiastolicRecord();
        this.measurementDate = measurement.getMeasurementDate();
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

    public float getDiastolicRecord() {
        return diastolicPressure;
    }

    public float getSystolicRecord() {
        return systolicPressure;
    }
}
