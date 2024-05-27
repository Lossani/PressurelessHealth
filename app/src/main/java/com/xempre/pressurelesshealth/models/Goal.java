package com.xempre.pressurelesshealth.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Goal {
    Integer id;
    String name;

    String description;

    String image;

    Integer reward;

    boolean enabled;

    boolean reached;

    @JsonProperty("reached_on")
    String reachedOn;
    Goal(){}

    public Goal(Goal goal){
        this.description = goal.getDescription();
        this.name = goal.getName();
        this.id = goal.getId();
        this.enabled = goal.getEnabled();
        this.image = goal.getImage();
        this.reward = goal.getReward();

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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public Integer getReward() {
        return reward;
    }

    public String getImage() {
        return image;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setReached(boolean reached) {
        this.reached = reached;
    }

    public boolean getReached() {
        return reached;
    }

    public String getReachedOn() {
        return reachedOn;
    }

    public void setReachedOn(String reachedOn) {
        this.reachedOn = reachedOn;
    }
}
