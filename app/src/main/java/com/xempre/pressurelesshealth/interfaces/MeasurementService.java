package com.xempre.pressurelesshealth.interfaces;

import com.xempre.pressurelesshealth.models.Measurement;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MeasurementService {
    @GET("measurements/{id}")
    public Call<Measurement> find(@Path("id") String id);

    @GET("measurements/all/")
    public Call<List<Measurement>> getAll();
    @POST("measurements/all/")
    public Call<Measurement> save(@Body Measurement measurement);

}
