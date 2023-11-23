package com.xempre.pressurelesshealth.interfaces;

import com.xempre.pressurelesshealth.models.Goal;
import com.xempre.pressurelesshealth.models.GoalHistory;
import com.xempre.pressurelesshealth.models.LeaderboardItem;
import com.xempre.pressurelesshealth.models.Measurement;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LeaderboardService {

    @GET("gamification/leaderboard/")
    public Call<List<LeaderboardItem>> getAll();

}
