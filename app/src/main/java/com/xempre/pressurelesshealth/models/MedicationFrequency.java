package com.xempre.pressurelesshealth.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MedicationFrequency {
    Integer id;

    Integer weekday;

    String hour;

    String dose;
    boolean deleted;
    @JsonProperty("medication")
    Integer medicationId;

    public MedicationFrequency(){}

    public Integer getId() {
        return id;
    }

    public boolean isDeleted(){
        return deleted;
    }

    public Integer getMedicationId() {
        return medicationId;
    }

    public Integer getWeekday() {
        return weekday;
    }

    public String getDose() {
        return dose;
    }

    public String getHour() {
        return hour;
    }

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

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }
}
