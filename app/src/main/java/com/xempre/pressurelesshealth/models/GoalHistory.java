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
    Integer goalId;

    GoalHistory(){}

    public GoalHistory(GoalHistory goal){
        this.reachedOn = goal.getReachedOn();
        this.userId = goal.getUserId();
        this.id = goal.getId();
        this.goalId = goal.getGoalId();

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGoalId() {
        return goalId;
    }

    public void setGoalId(Integer goalId) {
        this.goalId = goalId;
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
