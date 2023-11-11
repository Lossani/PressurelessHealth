package com.xempre.pressurelesshealth.interfaces;

import com.xempre.pressurelesshealth.models.Goal;
import com.xempre.pressurelesshealth.models.Measurement;
import com.xempre.pressurelesshealth.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GoalService {
    @GET("gamification/goals/{id}/")
    public Call<Goal> getUserById(@Path("id") int id);

    @GET("gamification/goals/")
    public Call<List<Goal>> getAll();
    @POST("gamification/goals/")
    public Call<Goal> save(@Body Measurement measurement);
}
