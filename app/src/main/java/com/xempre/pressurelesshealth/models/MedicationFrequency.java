package com.xempre.pressurelesshealth.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MedicationFrequency {
    Integer id;

//    Integer weekday;

    String hour;

    String dose;
    boolean deleted;

    boolean monday;
    boolean tuesday;
    boolean wednesday;
    boolean thursday;
    boolean friday;
    boolean saturday;
    boolean sunday;

    @JsonProperty("reminder_notification_enabled")
    boolean reminderNotificationEnabled;
    @JsonProperty("medication_id")
    Integer medicationId;

    @JsonProperty("medication")
    Medication medication;

    @JsonProperty("reminder")
    Reminder reminder;

    public MedicationFrequency(){
    }

    public boolean getMonday() {
        return monday;
    }

    public boolean getTuesday() {
        return tuesday;
    }

    public boolean getWednesday() {
        return wednesday;
    }

    public boolean getThursday() {
        return thursday;
    }

    public boolean getFriday() {
        return friday;
    }

    public boolean getSaturday() {
        return saturday;
    }

    public boolean getSunday() {
        return sunday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public Integer getId() {
        return id;
    }

    public boolean getDeleted(){
        return deleted;
    }

    public Integer getMedicationId() {
        return medicationId;
    }

//    public Integer getWeekday() {
//
//        return weekday;
//    }

    public String getDose() {
        return dose;
    }

    public String getHour() {
        return hour;
    }

    public boolean isReminderNotificationEnabled() { return reminderNotificationEnabled; }

    public Reminder getReminder() { return reminder; }

    public Medication getMedication() { return medication; }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public void setMedicationId(Integer medicationId) {
        this.medicationId = medicationId;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

//    public void setWeekday(Integer weekday) {
//        this.weekday = weekday;
//    }

    public void setReminderNotificationEnabled(boolean enabled) { this.reminderNotificationEnabled = enabled; }

    public void setReminder(Reminder reminder) { this.reminder = reminder; }

    public void setMedication(Medication medication) { this.medication = medication; }

    public boolean[] getDaysArray(){
        return new boolean[]{getSunday(), getMonday(), getTuesday(), getWednesday(), getThursday(), getFriday(), getSaturday()};
    }
}
