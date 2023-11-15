package com.xempre.pressurelesshealth.interfaces;

import com.xempre.pressurelesshealth.models.Measurement;
import com.xempre.pressurelesshealth.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    @GET("core/users/{id}/")
    public Call<User> getUserById(@Path("id") int id);

    @GET("core/users/")
    public Call<List<User>> getAll();
    @POST("core/users/")
    public Call<User> save(@Body Measurement measurement);
}
