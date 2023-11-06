package com.xempre.pressurelesshealth.views.profile;

import static com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC;
import static com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.xempre.pressurelesshealth.MainActivity;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.api.GoogleFitCallback;
import com.xempre.pressurelesshealth.databinding.ActivityAddMeasurementBinding;
import com.xempre.pressurelesshealth.databinding.FragmentUserProfileBinding;
import com.xempre.pressurelesshealth.interfaces.RecordService;
import com.xempre.pressurelesshealth.models.Record;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserProfile extends Fragment {

    private FragmentUserProfileBinding binding;
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

        binding = FragmentUserProfileBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    int sr = Integer.parseInt(sys.getText().toString());
//                    int dr = Integer.parseInt(dis.getText().toString());
//                    saveButton(sr,dr);
//                } catch (Exception ignored){
//                    Toast.makeText(getContext(), "Asegurece de ingresar números validos.", Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}