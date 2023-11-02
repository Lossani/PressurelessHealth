package com.xempre.pressurelesshealth.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.interfaces.RecordService;
import com.xempre.pressurelesshealth.models.Record;
import com.xempre.pressurelesshealth.views.reports.MeasurementList.MeasurementList;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddMeasurement extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Button btnSave;

    EditText sys;
    EditText dis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_measurement);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bb_add){
                int i = 0;
            } else if (item.getItemId() == R.id.bb_report) {
                startActivity(new Intent(AddMeasurement.this, MeasurementList.class));
            }
            return true;
        });

        sys = findViewById(R.id.etSystolic);
        dis = findViewById(R.id.etDiastolic);

        btnSave = findViewById(R.id.saveButton);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int sr = Integer.parseInt(sys.getText().toString());
                    int dr = Integer.parseInt(dis.getText().toString());
                    Toast.makeText(AddMeasurement.this, "prueba." + sys.getText(), Toast.LENGTH_SHORT).show();

                    saveButton(sr,dr);
                } catch (Exception ignored){
                    Toast.makeText(AddMeasurement.this, "Asegurece de ingresar números validos.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void saveButton(int sr, int dr){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.3:5000")
                // as we are sending data in json format so
                // we have to add Gson converter factory
                .addConverterFactory(GsonConverterFactory.create())
                // at last we are building our retrofit builder.
                .build();
        // below line is to create an instance for our retrofit api class.
        RecordService recordService = retrofit.create(RecordService.class);
//        Toast.makeText(getContext(), "Data added to API", Toast.LENGTH_SHORT).show();
        // passing data from our text fields to our modal class.
//        Date date = new Date();

        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());

        Record record = new Record(1, sr, dr,date);

        // calling a method to create a post and passing our modal class.
        Call<Record> call = recordService.save(record);

        // on below line we are executing our method.
        call.enqueue(new Callback<Record>() {
            @Override
            public void onResponse(Call<Record> call, Response<Record> response) {
                // this method is called when we get response from our api.
                Toast.makeText(getApplicationContext(), "Data added to API", Toast.LENGTH_SHORT).show();

//                // below line is for hiding our progress bar.
//                loadingPB.setVisibility(View.GONE);
//
//                // on below line we are setting empty text
//                // to our both edit text.
//                jobEdt.setText("");
//                nameEdt.setText("");

                // we are getting response from our body
                // and passing it to our modal class.
                Record responseFromAPI = response.body();

//                // on below line we are getting our data from modal class and adding it to our string.
                String responseString = "Response Code : " + response.code() + "\nName : "  + "\n" + "Job : ";

//                // below line we are setting our
//                // string to our text view.
//                message.setText(responseString);
            }

            @Override
            public void onFailure(Call<Record> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "ERROR"+t.toString(), Toast.LENGTH_LONG).show();

                // setting text to our text view when
                // we get error response from API.
//                responseTV.setText("Error found is : " + t.getMessage());
            }
        });
    }

}