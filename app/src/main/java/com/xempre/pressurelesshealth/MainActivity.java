package com.xempre.pressurelesshealth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.views.AddMeasurement;
import com.xempre.pressurelesshealth.views.MainView;
import com.xempre.pressurelesshealth.views.profile.UserProfile;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    GoogleFitApi googleFitApi = new GoogleFitApi();
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
        googleFitApi.createClient(this);



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
        Fitness.getHistoryClient(this, account)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        for (DataPoint dp : dataSet.getDataPoints()) {
                            for (Field field : dp.getDataType().getFields()) {
                                totalSteps += dp.getValue(field).asInt();
                            }
                        }
                        // Update UI with totalSteps
                        TextView textStepCount = findViewById(R.id.text_step_count);
                        textStepCount.setText(String.valueOf(totalSteps));
                    }

    }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                account = (GoogleSignInAccount) ((Task<?>) task).getResult(ApiException.class);
                Toast.makeText(this,"bien", Toast.LENGTH_LONG).show();
                // Signed in successfully, connect to the Google Fit API
            } catch (ApiException e) {
                System.out.println(e);
                System.out.println("PERRO");
                Toast.makeText(this,"mal", Toast.LENGTH_LONG).show();
                // The ApiException status code indicates the detailed failure reason.
            }
        }
    }
}