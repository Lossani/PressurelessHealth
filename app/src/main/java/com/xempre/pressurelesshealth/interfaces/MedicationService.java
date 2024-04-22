package com.xempre.pressurelesshealth.interfaces;

import com.xempre.pressurelesshealth.models.Measurement;
import com.xempre.pressurelesshealth.models.Medication;
import com.xempre.pressurelesshealth.models.MedicationFrequency;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MedicationService {
    @GET("health/medications/")
    public Call<List<Medication>> getAll();

    @GET("health/medication_frequencies/")
    public Call<List<MedicationFrequency>> getAllMedicationFrequencies(@Query("medication") Integer medicationId);

    @POST("health/medications/")
    public Call<Medication> saveMedication(@Body Medication medication);

    @PATCH("health/medications/{id}/")
    public Call<Medication> deleteMedication(@Path("id") int id, @Body Medication medication);

    @PATCH("health/medication_frequencies/{id}/")
    public Call<MedicationFrequency> deleteMedicationFrequency(@Path("id") int id, @Body MedicationFrequency medicationFrequency);


    @POST("health/medication_frequencies/")
    public Call<MedicationFrequency> saveMedicationFrequency(@Body MedicationFrequency medicationFrequency);
}
