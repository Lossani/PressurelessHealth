package com.xempre.pressurelesshealth.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Challenge {
    Integer id;
    String name;

    String description;

    Integer reward;

    boolean repeatable;
    @JsonProperty("latest_history")
    ChallengeHistory[] latestHistory;

    String image;
//    @JsonProperty("time_limit")
//    Long timeLimit;

    boolean enabled;
    Challenge(){}

    public Challenge(Challenge challenge){
        this.description = challenge.getDescription();
        this.name = challenge.getName();
        this.id = challenge.getId();
        this.enabled = challenge.getEnabled();
        this.image = challenge.getImage();
        this.reward = challenge.getReward();
        this.repeatable = challenge.isRepeatable();
        this.latestHistory = challenge.getLatestHistory();
//        this.timeLimit = challenge.getTimeLimit();
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public ChallengeHistory[] getLatestHistory() {
        return latestHistory;
    }

    public void setLatestHistory(ChallengeHistory[] latestHistory) {
        this.latestHistory = latestHistory;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void setReward(Integer reward) {
        this.reward = reward;
    }

    public Integer getReward() {
        return reward;
    }



    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

//    public Long getTimeLimit() {
//        return timeLimit;
//    }

    public String getImage() {
        return image;
    }

    public boolean getEnabled() {
        return enabled;
    }
}
