package com.xempre.pressurelesshealth;

import static android.content.ContentValues.TAG;
import static com.google.android.gms.fitness.data.HealthDataTypes.TYPE_BLOOD_PRESSURE;

import static java.text.DateFormat.getTimeInstance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.api.GoogleFitCallback;
import com.xempre.pressurelesshealth.views.AddMeasurement;
import com.xempre.pressurelesshealth.views.MainView;
import com.xempre.pressurelesshealth.views.profile.UserProfile;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    GoogleFitApi googleFitApi = null;

    Activity mainActivity = this;
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build();

    GoogleSignInAccount account = null;
    int totalSteps = 0;

    Button btnReadSteps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // googleFitApi.createClient(this);
        googleFitApi = new GoogleFitApi(this);


        btnReadSteps = findViewById(R.id.btn_read_steps);
        btnReadSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readStepData(account);
                startActivity(new Intent(MainActivity.this, MainView.class));
            }
        });

/*
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
*/



        //totalSteps = googleFitApi.accessGoogleFit(this, account);





    }

    private void readStepData(GoogleSignInAccount account) {
        GoogleFitCallback googleFitCallback = (Map<String, Float> measurements) -> {
            for (Map.Entry<String, Float> entry : measurements.entrySet()) {
                String key = entry.getKey();
                Float val = entry.getValue();

                Toast.makeText(this, key + " " + val, Toast.LENGTH_LONG).show();
            }
        };
        googleFitApi.readBloodPressureMeasurement(this, googleFitCallback);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        googleFitApi.onActivityResult(data);

    }
}