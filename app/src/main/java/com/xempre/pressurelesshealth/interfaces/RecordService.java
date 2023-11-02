package com.xempre.pressurelesshealth.interfaces;

import com.xempre.pressurelesshealth.models.Record;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RecordService {
    @GET("records/{id}")
    public Call<Record> find(@Path("id") String id);

    @GET("records")
    public Call<List<Record>> getAll();
    @POST("records")
    public Call<Record> save(@Body Record record);

}
