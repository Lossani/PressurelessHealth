package com.xempre.pressurelesshealth.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoalHistory {
    Integer id;
    @JsonProperty("reached_on")
    String reachedOn;
    @JsonProperty("user")
    String userId;
    @JsonProperty("goal")
    Integer goal;

    boolean isSucceeded;

    float progress;

    GoalHistory(){}

    public GoalHistory(GoalHistory goal){
        this.reachedOn = goal.getReachedOn();
        this.userId = goal.getUserId();
        this.id = goal.getId();
        this.goal = goal.getGoal();
        this.isSucceeded = goal.getIsSucceeded();
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public void setSucceeded(boolean succeeded) {
        this.isSucceeded = succeeded;
    }

    public boolean getIsSucceeded() {
        return isSucceeded;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    public String getReachedOn() {
        return reachedOn;
    }

    public void setReachedOn(String reachedOn) {
        this.reachedOn = reachedOn;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
