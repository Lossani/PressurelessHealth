package com.xempre.pressurelesshealth.api;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.HealthDataTypes;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public class GoogleFitApi {
    //    private FitnessOptions bloodPressureOptions = new FitnessOptions.Builder()
//            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
//        .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
//        .build();
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(new Scope("https://www.googleapis.com/auth/fitness.activity.read"),
                    new Scope("https://www.googleapis.com/auth/fitness.location.read"))
            .build();
    public void createClient (Activity activity) {
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(activity, signInIntent, 1, null);


        /*GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(activity, bloodPressureOptions)
        if (!GoogleSignIn.hasPermissions(account, bloodPressureOptions)) {
            GoogleSignIn.requestPermissions(
                    activity, // your activity
                    1, // e.g. 1
                    account,
                    bloodPressureOptions
            );
            return null;
        } else {
            return accessGoogleFit(activity);
        }*/
    }

    public int accessGoogleFit(Activity activity, GoogleSignInAccount account) {
        /*LocalDateTime end = LocalDateTime.now();
        LocalDateTime startDate = end.minusDays(4);
        long endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond();
        long startSeconds = startDate.atZone(ZoneId.systemDefault()).toEpochSecond();
        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(activity, bloodPressureOptions);

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(HealthDataTypes.TYPE_BLOOD_PRESSURE)
                .aggregate(DataType.TYPE_HEART_RATE_BPM)
                .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
                .bucketByTime(5, TimeUnit.MINUTES)
                .build();

        DataPoint result;*/

        final int[] totalSteps = {0};

        Fitness.getHistoryClient(activity, account)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {

                        for (DataPoint dp : dataSet.getDataPoints()) {
                            for (Field field : dp.getDataType().getFields()) {
                                totalSteps[0] += dp.getValue(field).asInt();
                            }
                        }
                        // Update UI with totalSteps
                    }
                });

        return totalSteps[0];

        /*GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                        .build();

        GoogleSignInAccount googleSignInAccount =
                GoogleSignIn.getAccountForExtension(activity, fitnessOptions);

        Task<DataReadResponse> response = Fitness.getHistoryClient(activity, googleSignInAccount)
                .readData(new DataReadRequest.Builder()
                        .read(DataType.TYPE_STEP_COUNT_DELTA)
                        .setTimeRange(startSeconds, endSeconds, TimeUnit.MILLISECONDS)
                        .build());

        DataReadResponse readDataResponse = Tasks.await(response);
        DataSet dataSet = readDataResponse.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);

        return dataSet;*/
    }
}
