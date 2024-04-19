package com.xempre.pressurelesshealth.interfaces;

import com.xempre.pressurelesshealth.models.Contact;
import com.xempre.pressurelesshealth.models.Goal;
import com.xempre.pressurelesshealth.models.GoalHistory;
import com.xempre.pressurelesshealth.models.Measurement;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ContactService {
    @GET("core/contacts/")
    public Call<List<Contact>> getAll();

    @POST("core/contacts/")
    public Call<Contact> save(@Body Contact contact);
}
