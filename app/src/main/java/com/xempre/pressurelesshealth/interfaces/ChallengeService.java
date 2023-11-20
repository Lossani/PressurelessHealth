package com.xempre.pressurelesshealth.interfaces;

import com.xempre.pressurelesshealth.models.Challenge;
import com.xempre.pressurelesshealth.models.ChallengeHistory;
import com.xempre.pressurelesshealth.models.Goal;
import com.xempre.pressurelesshealth.models.GoalHistory;
import com.xempre.pressurelesshealth.models.Measurement;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChallengeService {
    @GET("gamification/challenges/{id}/")
    public Call<Challenge> getUserById(@Path("id") int id);

    @GET("gamification/challenges/?enabled=1")
    public Call<List<Challenge>> getAllEnabled();

    @GET("core/challenge_history/")
    public Call<List<ChallengeHistory>> getAllCompleted();

    @POST("core/challenge_history/")
    public Call<ChallengeHistory> startChallenge(@Body ChallengeHistory challengeHistory);

    @POST("gamification/challenges/")
    public Call<Challenge> save(@Body Measurement measurement);
}
