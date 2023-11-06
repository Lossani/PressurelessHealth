package com.xempre.pressurelesshealth.models;

public class Measurement {
    private int idUser;
    private float systolicPressure;
    private float diastolicPressure;

//    private String date;

    public Measurement(int id, float sr, float dr){
        this.idUser = id;
        this.systolicPressure = sr;
        this.diastolicPressure = dr;
//        this.date = date;
    }

    public Measurement(Measurement measurement){
        this.idUser = measurement.getUserId();
        this.systolicPressure = measurement.getSystolicRecord();
        this.diastolicPressure = measurement.getDiastolicRecord();
//        this.date = measurement.getDate();
    }

//    public String getDate() {
//        return date;
//    }

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
