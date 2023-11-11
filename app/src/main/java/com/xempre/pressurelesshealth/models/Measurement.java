package com.xempre.pressurelesshealth.models;

public class Measurement {
    private int idUser;
    private float systolicPressure;
    private float diastolicPressure;

    private String measurementDate;

    public Measurement(int id, float sr, float dr, String measurementDate){
        this.idUser = id;
        this.systolicPressure = sr;
        this.diastolicPressure = dr;
        this.measurementDate = measurementDate;
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
