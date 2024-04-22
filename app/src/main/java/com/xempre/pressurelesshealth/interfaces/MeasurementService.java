package com.xempre.pressurelesshealth.interfaces;

import com.xempre.pressurelesshealth.models.Measurement;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MeasurementService {
    @GET("health/measurements/{id}")
    public Call<Measurement> find(@Path("id") String id);

    @GET("health/measurements/")
    public Call<List<Measurement>> getAll();
    @GET("health/measurements/latest/")
    public Call<Measurement> getLatest();
    @GET("health/measurements/")
    public Call<List<Measurement>> getAllByDateRange(@Query("measurement_date_start") String startDate, @Query("measurement_date_end") String endDate);
    @POST("health/measurements/")
    public Call<Measurement> save(@Body Measurement measurement);

}
