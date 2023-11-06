package com.xempre.pressurelesshealth.api;

import static android.content.ContentValues.TAG;
import static androidx.core.app.ActivityCompat.startActivityForResult;

import static com.google.android.gms.fitness.data.HealthDataTypes.TYPE_BLOOD_PRESSURE;
import static com.google.android.gms.fitness.data.HealthFields.BLOOD_PRESSURE_MEASUREMENT_LOCATION_LEFT_UPPER_ARM;
import static com.google.android.gms.fitness.data.HealthFields.BODY_POSITION_SITTING;
import static com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC;
import static com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_MEASUREMENT_LOCATION;
import static com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC;
import static com.google.android.gms.fitness.data.HealthFields.FIELD_BODY_POSITION;

import static java.text.DateFormat.getTimeInstance;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.HealthDataTypes;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GoogleFitApi {
    private GoogleSignInAccount googleAccount = null;

    public GoogleFitApi(Activity parent) {
        createClient(parent);
    }

    private void createClient (Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(new Scope("https://www.googleapis.com/auth/fitness.activity.read"),
                    new Scope("https://www.googleapis.com/auth/fitness.location.read"),
                    new Scope("https://www.googleapis.com/auth/fitness.blood_pressure.read"),
                    new Scope("https://www.googleapis.com/auth/fitness.blood_pressure.write"),
                    new Scope("https://www.googleapis.com/auth/fitness.heart_rate.read"),
                    new Scope("https://www.googleapis.com/auth/fitness.oxygen_saturation.read"),
                    new Scope("https://www.googleapis.com/auth/fitness.body.read"))
            .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(activity, signInIntent, 1, null);


    }

    public void onActivityResult (Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            googleAccount = (GoogleSignInAccount) ((Task<?>) task).getResult(ApiException.class);
            // Signed in successfully, connect to the Google Fit API
        } catch (ApiException e) {
            System.out.println(e);
            // The ApiException status code indicates the detailed failure reason.
        }
    }

    public void saveBloodPressureMeasurement(float systolicPressure, float diastolicPressure) {
        DataSource bloodPressureSource = new DataSource.Builder()
                 .setDataType(TYPE_BLOOD_PRESSURE)
                // ...
                .build();

        DataPoint bloodPressure = DataPoint.builder(bloodPressureSource)
                .setTimestamp(Instant.now().getEpochSecond(), TimeUnit.MILLISECONDS)
                .setField(FIELD_BLOOD_PRESSURE_SYSTOLIC, systolicPressure)
                .setField(FIELD_BLOOD_PRESSURE_DIASTOLIC, diastolicPressure)
                .setField(FIELD_BODY_POSITION, BODY_POSITION_SITTING)
                .setField(
                        FIELD_BLOOD_PRESSURE_MEASUREMENT_LOCATION,
                        BLOOD_PRESSURE_MEASUREMENT_LOCATION_LEFT_UPPER_ARM)
                .build();
    }

    public void readBloodPressureMeasurement(Activity activity, GoogleFitCallback callback) {
        Map<String, Float> results = new HashMap<String, Float>();

        Fitness.getHistoryClient(activity, googleAccount)
            .readData(createReadRequest()).addOnSuccessListener(
                new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        DateFormat dateFormat = getTimeInstance();
                        //googleFitApi.printData(dataReadResponse);
                        for (DataSet dataSet : dataReadResponse.getDataSets()) {
                            //GoogleFitApi.dumpDataSet(data);
                            for (DataPoint dp : dataSet.getDataPoints()) {
                                Log.v(TAG, "Data Point:");
                                Log.v(TAG, "Type: " + dataSet.getDataType().getName());
                                Log.v(TAG, "Start: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                                Log.v(TAG, "End: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));

                                results.put(FIELD_BLOOD_PRESSURE_SYSTOLIC.getName(), dp.getValue(FIELD_BLOOD_PRESSURE_SYSTOLIC).asFloat());
                                results.put(FIELD_BLOOD_PRESSURE_DIASTOLIC.getName(), dp.getValue(FIELD_BLOOD_PRESSURE_DIASTOLIC).asFloat());
                                callback.fnCallback(results);
                                //for (Field field : dp.getDataType().getFields()) {
                                //    Value val = dp.getValue(field);
                                    //results.put(field.getName(), (float) dp.getValue(field).asInt());
                                    //callback.fnCallback(results);
                                //}
                            }
                        }
                    }
                }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e.getMessage());
                }
            });
    }

    private DataReadRequest createReadRequest() {
        // Read the data that's been collected throughout the past week.
        ZonedDateTime endTime = LocalDateTime.now().atZone(ZoneId.systemDefault());
        ZonedDateTime startTime = endTime.minusHours(12);
        // Log.i(TAG, "Range Start: " + startTime.getHour());
        // Log.i(TAG, "Range End: $endTime");

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(HealthDataTypes.TYPE_BLOOD_PRESSURE)
                //.aggregate(HealthDataTypes.TYPE_BLOOD_PRESSURE
                //  , HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY
                //)
                // bucketByTime allows for a time span, while bucketBySession allows
                // bucketing by sessions.
                //.bucketByTime(1, TimeUnit.DAYS)
                //.bucketBySession()
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .enableServerQueries()
                .build();
        return readRequest;
    }

    public static void printData(DataReadResponse dataReadResult) {
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        Log.v(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
        if (dataReadResult.getBuckets().size() > 0) {

            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                Log.v(TAG, "Datasets: " + dataSets);

                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            System.out.print("Number of returned DataSets is: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
    }

    // [START parse_dataset]
    public static void dumpDataSet(DataSet dataSet) {

        Log.v(TAG, "Name: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();
        Log.v(TAG, "Fields: " + dataSet.getDataSource().getDataType().getFields());

        Log.v(TAG, "Data Point Values :" + dataSet.getDataPoints());
        Log.v(TAG, "Data :" + dataSet.toString());
        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.v(TAG, "Data Point:");
            Log.v(TAG, "Type: " + dataSet.getDataType().getName());
            Log.v(TAG, "Start: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.v(TAG, "End: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.v(TAG, "Field: " + field.getName() + ", Value : " + dp.getValue(field));
            }
        }
    }
}
