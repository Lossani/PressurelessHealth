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
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.api.GoogleFitCallback;
import com.xempre.pressurelesshealth.databinding.ActivityAddMeasurementBinding;
import com.xempre.pressurelesshealth.interfaces.MeasurementService;
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

    MainActivity mainActivity;
    GoogleFitApi googleFitApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity)getActivity();
        googleFitApi = mainActivity.getGoogleFitApi();

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

        if (googleFitApi != null) {
            GoogleFitCallback googleFitCallback = (Map<String, Float> measurements) -> {
                sys.setText(measurements.get(FIELD_BLOOD_PRESSURE_SYSTOLIC.getName()).toString());
                dis.setText(measurements.get(FIELD_BLOOD_PRESSURE_DIASTOLIC.getName()).toString());
            };
            googleFitApi.readBloodPressureMeasurement(mainActivity, googleFitCallback);

        }

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
                    String pattern = "yyy-MM-dd'T'HH:mm:ss'Z'";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    String date = simpleDateFormat.format(new Date());
                    saveButton(sr,dr,date);
                } catch (Exception ignored){
                    Toast.makeText(getContext(), "Asegurece de ingresar números validos.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void saveButton(float sr, float dr, String date){

        MeasurementService measurementService = ApiClient.createService(MeasurementService.class);

        Measurement measurement = new Measurement(2, sr, dr, date);

        // calling a method to create a post and passing our modal class.
        Call<Measurement> call = measurementService.save(measurement);

        // on below line we are executing our method.
        call.enqueue(new Callback<Measurement>() {
            @Override
            public void onResponse(Call<Measurement> call, Response<Measurement> response) {
                // this method is called when we get response from our api.
                if (response.code() == 201){
                    Toast.makeText(getContext(), "Medida guardada exitosamente.", Toast.LENGTH_SHORT).show();
                    sys.setText("");
                    dis.setText("");
                } else {
                    Toast.makeText(getContext(), "Ocurrio un error. Error " + response.code(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Measurement> call, Throwable t) {
                Toast.makeText(getContext(), "ERROR", Toast.LENGTH_LONG).show();

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