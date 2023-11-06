package com.xempre.pressurelesshealth.views;

import static com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC;
import static com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.xempre.pressurelesshealth.MainActivity;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.api.GoogleFitCallback;
import com.xempre.pressurelesshealth.databinding.ActivityAddMeasurementBinding;
import com.xempre.pressurelesshealth.interfaces.RecordService;
import com.xempre.pressurelesshealth.models.Measurement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddMeasurement extends Fragment {

    private ActivityAddMeasurementBinding binding;
    EditText sys;
    EditText dis;

    MainActivity mainViewActivity;
    GoogleFitApi googleFitApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainViewActivity = (MainActivity)getActivity();
        googleFitApi = mainViewActivity.getGoogleFitApi();

//        sys = getView().findViewById(R.id.etSystolic);
//        dis = getView().findViewById(R.id.etDiastolic);
//        message = getView().findViewById(R.id.textView2);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = ActivityAddMeasurementBinding.inflate(inflater, container, false);
        sys = binding.etSystolic;
        dis = binding.etDiastolic;

        GoogleFitCallback googleFitCallback = (Map<String, Float> measurements) -> {
            sys.setText(measurements.get(FIELD_BLOOD_PRESSURE_SYSTOLIC.getName()).toString());
            dis.setText(measurements.get(FIELD_BLOOD_PRESSURE_DIASTOLIC.getName()).toString());
        };
        googleFitApi.readBloodPressureMeasurement(mainViewActivity, googleFitCallback);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//            }
//        });

//        binding.reportButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_MeasurementList);
//            }
//        });

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    float sr = Float.parseFloat(sys.getText().toString());
                    float dr = Float.parseFloat(dis.getText().toString());
                    saveButton(sr,dr);
                } catch (Exception ignored){
                    Toast.makeText(getContext(), "Asegurece de ingresar números validos.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void saveButton(float sr, float dr){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://health.xempre.com")
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

        Measurement measurement = new Measurement(2, sr, dr);

        // calling a method to create a post and passing our modal class.
        Call<Measurement> call = recordService.save(measurement);

        // on below line we are executing our method.
        call.enqueue(new Callback<Measurement>() {
            @Override
            public void onResponse(Call<Measurement> call, Response<Measurement> response) {
                // this method is called when we get response from our api.
                Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();
                Log.e("perro",response.toString());
//                // below line is for hiding our progress bar.
//                loadingPB.setVisibility(View.GONE);
//
//                // on below line we are setting empty text
//                // to our both edit text.
//                jobEdt.setText("");
//                nameEdt.setText("");

                // we are getting response from our body
                // and passing it to our modal class.
                Measurement responseFromAPI = response.body();

//                // on below line we are getting our data from modal class and adding it to our string.
                String responseString = "Response Code : " + response.code() + "\nName : "  + "\n" + "Job : ";

//                // below line we are setting our
//                // string to our text view.
//                message.setText(responseString);
            }

            @Override
            public void onFailure(Call<Measurement> call, Throwable t) {
                Toast.makeText(getContext(), "ERROR"+t.toString(), Toast.LENGTH_LONG).show();

                // setting text to our text view when
                // we get error response from API.
//                responseTV.setText("Error found is : " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}