package com.xempre.pressurelesshealth.models;

public class Record {
    private int userId;
    private int systolicRecord;
    private int diastolicRecord;

    private String date;

    public Record(int id, int sr, int dr, String date){
        this.userId = id;
        this.systolicRecord = sr;
        this.diastolicRecord = dr;
        this.date = date;
    }

    public Record(Record record){
        this.userId = record.getUserId();
        this.systolicRecord = record.getSystolicRecord();
        this.diastolicRecord = record.getDiastolicRecord();
        this.date = record.getDate();
    }

    public String getDate() {
        return date;
    }

    public int getUserId() {
        return userId;
    }

    public int getDiastolicRecord() {
        return diastolicRecord;
    }

    public int getSystolicRecord() {
        return systolicRecord;
    }
}
