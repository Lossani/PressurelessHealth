package com.xempre.pressurelesshealth.interfaces;

import com.xempre.pressurelesshealth.models.Measurement;
import com.xempre.pressurelesshealth.models.ResponseLogin;
import com.xempre.pressurelesshealth.models.RestartPassword;
import com.xempre.pressurelesshealth.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    @GET("core/users/{id}/")
    public Call<User> getUserById(@Path("id") int id);

    @GET("core/users/")
    public Call<List<User>> getAll();
    @POST("core/users/")
    public Call<User> save(@Body Measurement measurement);

    @POST("core/login/")
    public Call<ResponseLogin> login(@Body User user);

    @POST("core/users/")
    public Call<User> register(@Body User user);

    @POST("core/password_reset/")
    public Call<Void> resetPassword(@Body RestartPassword email);

    @PUT("core/password_change/")
    public Call<Void> resetPasswordValidation(@Body RestartPassword code_password);

}
