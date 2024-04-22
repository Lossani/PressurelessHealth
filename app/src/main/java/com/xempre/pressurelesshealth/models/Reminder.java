package com.xempre.pressurelesshealth.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Reminder {
    Integer id;

    boolean active;

    int triggeredTimes;

    @JsonProperty("medication_frequency")
    MedicationFrequency medicationFrequency;

    @JsonProperty("medication_frequency_id")
    int medicationFrequencyId;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getTriggeredTimes() {
        return triggeredTimes;
    }

    public void setTriggeredTimes(int triggeredTimes) {
        this.triggeredTimes = triggeredTimes;
    }

    public MedicationFrequency getMedicationFrequency() {
        return medicationFrequency;
    }


    public int getMedicationFrequencyId() {
        return medicationFrequencyId;
    }

    public void setMedicationFrequencyId(int medicationFrequencyId) {
        this.medicationFrequencyId = medicationFrequencyId;
    }
}
