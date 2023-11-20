package com.xempre.pressurelesshealth.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeHistory {
    Integer id;
    @JsonProperty("start_date")
    String startDate;

    @JsonProperty("end_date")
    String endDate;

    @JsonProperty("user")
    Integer userId;
//    @JsonProperty("challenge")
//    Challenge challenge;
    @JsonProperty("succeeded")
    boolean isSucceeded;

    float progress;
    @JsonProperty("challenge")
    Integer challengeId;

    ChallengeHistory(){}

    public ChallengeHistory(ChallengeHistory challengeHistory){
        id = challengeHistory.getId();
        startDate = challengeHistory.getStartDate();
        endDate = challengeHistory.getEndDate();
        userId = challengeHistory.getUserId();
//        challenge = challengeHistory.getChallenge();
        isSucceeded = challengeHistory.getIsSucceeded();
        progress = challengeHistory.getProgress();
        challengeId = challengeHistory.getChallengeId();
//        challengeId = challengeHistory.getChallenge().getId();
    }

    public ChallengeHistory(Integer userId, Integer challengeId, String date) {
        this.userId = userId;
        this.challengeId = challengeId;
        this.startDate = date;
    }

    public Integer getId() {
        return id;
    }

    public Integer getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Integer challengeId) {
        this.challengeId = challengeId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public boolean getIsSucceeded() {
        return this.isSucceeded;
    }

    public float getProgress() {
        return progress;
    }

    public void setIsSucceeded(boolean succeeded) {
        isSucceeded = succeeded;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

//    public void setChallenge(Challenge challenge) {
//        this.challenge = challenge;
//    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Integer getUserId() {
        return userId;
    }

//    public Challenge getChallenge() {
//        return challenge;
//    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartDate() {
        return startDate;
    }
}