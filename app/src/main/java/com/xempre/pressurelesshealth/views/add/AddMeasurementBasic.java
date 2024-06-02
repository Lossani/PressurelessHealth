package com.xempre.pressurelesshealth.views.add;

import static android.content.Context.MODE_PRIVATE;
import static com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC;
import static com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.SupportActionModeWrapper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.fitness.data.Field;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xempre.pressurelesshealth.MainActivity;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.api.GoogleFitCallback;
import com.xempre.pressurelesshealth.databinding.ActivityAddMeasurementBinding;
import com.xempre.pressurelesshealth.interfaces.MeasurementService;
import com.xempre.pressurelesshealth.models.Challenge;
import com.xempre.pressurelesshealth.models.Measurement;
import com.xempre.pressurelesshealth.utils.Utils;
import com.xempre.pressurelesshealth.views.MainActivityView;
import com.xempre.pressurelesshealth.views.reports.MeasurementList.MeasurementList;
import com.xempre.pressurelesshealth.views.settings.contacts.ContactList;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;
import com.xempre.pressurelesshealth.views.shared.CustomDialog;
import com.xempre.pressurelesshealth.views.shared.MinMaxFilter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMeasurementBasic extends Fragment {

    private ActivityAddMeasurementBinding binding;
    EditText sys;
    EditText dis;

    MainActivityView mainActivity;
    GoogleFitApi googleFitApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivityView)getActivity();
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

        binding.btnBackAddBasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment.back(getContext());
            }
        });

        if (googleFitApi != null) {
            GoogleFitCallback googleFitCallback = (Map<String, Number> measurements) -> {
                Number systolicPressure = measurements.get(FIELD_BLOOD_PRESSURE_SYSTOLIC.getName());
                Number diastolicPressure = measurements.get(FIELD_BLOOD_PRESSURE_DIASTOLIC.getName());
                Number heartRate = measurements.get(Field.FIELD_BPM.getName());
                Number date = measurements.get("DATE");

                try {
                    // Toast.makeText(mainActivity, heartRate != null ? heartRate.toString() : "No heart rate", Toast.LENGTH_LONG).show();
                } catch (Exception ignored) {

                }
                sys.setText(systolicPressure != null ? systolicPressure.toString() : null);
                dis.setText(diastolicPressure != null ? diastolicPressure.toString() : null);
            };

            ZonedDateTime endTime = LocalDateTime.now().atZone(ZoneId.systemDefault());
            ZonedDateTime startTime = endTime.minusHours(12);
            googleFitApi.readBloodPressureMeasurement(mainActivity, googleFitCallback, startTime, endTime);

        }

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.etSystolic.setFilters(new InputFilter[]{new MinMaxFilter(1.0,250.0)});
        binding.etDiastolic.setFilters(new InputFilter[]{new MinMaxFilter(1.0,200.0)});


        binding.etSystolic.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(binding.etSystolic.getText().length() == 1 && binding.etSystolic.getText().toString().trim().equals("0")){
                        binding.etSystolic.setText("");
                    }
                }else {
                    if(binding.etSystolic.getText().length() == 0){
                        binding.etSystolic.setText("0");
                    }
                }
            }
        });

        binding.etDiastolic.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(binding.etDiastolic.getText().length() == 1 && binding.etDiastolic.getText().toString().trim().equals("0")){
                        binding.etDiastolic.setText("");
                    }
                }else {
                    if(binding.etDiastolic.getText().length() == 0){
                        binding.etDiastolic.setText("0");
                    }
                }
            }
        });

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (sys.getText().toString().equals("")) {
                        CustomDialog.create(getActivity(), "Alerta", "La medida de Presión Sistólica no tiene un valor válido.");
                        return;
                    }

                    if (dis.getText().toString().equals("")) {
                        CustomDialog.create(getActivity(), "Alerta", "La medida de Presión Diastólica no tiene un valor válido.");
                        return;
                    }

                    float sr = Float.parseFloat(sys.getText().toString());
                    float dr = Float.parseFloat(dis.getText().toString());
                    if (sr<1 || dr<1 || sr > 250 || dr > 200){
                        CustomDialog.create(getActivity(), "Alerta", "Asegurece de ingresar números validos.");
                        return;
                    }
                    if (dr >= sr) {
                        CustomDialog.create(getActivity(), "Alerta", "La medida de Presión Sistólica debe ser mayor que la Diastólica. Por favor valide los datos ingresados.");
                        return;
                    }
//                    if (dr == sr) {
//                        CustomDialog.create(getActivity(), "Alerta", "La diferencia entre la Presión Sistólica y Diastólica no puede ser 0. Por favor valide los datos ingresados.");
//                        return;
//                    }

                    String pattern = "yyy-MM-dd'T'HH:mm:ss'Z'";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    String date = simpleDateFormat.format(new Date());


                    if (sr < 90 || dr < 60) {

                        final boolean[] confirmSave = {false};
                        String msjSystolic = sr<90?"Se ha detectado que la medida de presión Sistólica es demasiado baja ("+sr+").":"";
                        String msjDiastolic = dr<60?"\nSe ha detectado que la medida de presión Diastólica es demasiado baja ("+dr+").":"";
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(
                                        msjSystolic+msjDiastolic+ "\n¿Está seguro que desea almacenar está medida?")
                                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Ejecutar la función de eliminación aquí
                                        saveButton(sr,dr,date);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });

                        // Crear y mostrar el diálogo
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        saveButton(sr,dr,date);
                    }

                } catch (Exception ignored){
                    Toast.makeText(getContext(), "Asegurece de ingresar números validos.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

//    private void replaceFragment(Fragment fragment){
//        BottomNavigationView bottomNavigationMain = getActivity().findViewById(R.id.bottomNavigationMain);
//        bottomNavigationMain.setSelectedItemId(R.id.bb_report);
//
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout, fragment);
//        fragmentTransaction.commit();
//    }

    public void saveButton(float sr, float dr, String date){

        MeasurementService measurementService = ApiClient.createService(getContext(),MeasurementService.class,1);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref",MODE_PRIVATE);

        int userId = sharedPreferences.getInt("userId", 0);

        Measurement measurement = new Measurement(0, userId, sr, dr, date, false);

        if(!binding.mltAddBasic.getText().toString().trim().equals(""))
            measurement.setComments(binding.mltAddBasic.getText().toString().trim());

        // calling a method to create a post and passing our modal class.
        Call<Measurement> call = measurementService.save(measurement);

        // on below line we are executing our method.
        call.enqueue(new Callback<Measurement>() {
            @Override
            public void onResponse(Call<Measurement> call, Response<Measurement> response) {
                // this method is called when we get response from our api.
                if (response.code() == 201){
                    Toast.makeText(getContext(), "Medida guardada exitosamente.", Toast.LENGTH_SHORT).show();

                    Utils.schedule12HourMeasurementReminder(getContext());

                    CustomDialog dialog = new CustomDialog();
                    com.xempre.pressurelesshealth.utils.Callback callback = () -> {
                        ChangeFragment.change(getContext(), R.id.frame_layout, new MeasurementList());
                    };
                    switch (measurement.categorizeBloodPressure()){
                        case "NORMAL":
                            dialog.create(getActivity(), "PRESIÓN NORMAL", "Tus niveles de presión arterial son normales. ¡Sigue así con hábitos saludables!", callback);
                            break;
                        case "ELEVATED":
                            dialog.create(getActivity(), "PRESIÓN ELEVADA", "Tu presión arterial está elevada, es importante tomar medidas para controlarla.", callback);
                            break;
                        case "HYPERTENSION_STAGE_1":
                            dialog.create(getActivity(), "HIPERTENSIÓN - ETAPA 1", "Estás en la Etapa 1 de hipertensión. Considera realizar cambios en tu estilo de vida.", callback);
                            break;
                        case "HYPERTENSION_STAGE_2":
                            dialog.create(getActivity(), "HIPERTENSIÓN - ETAPA 2", "Estás en la Etapa 2 de hipertensión. Te recomendamos buscar ayuda médica para controlarla.", callback);
                            break;
                        case "HYPERTENSIVE_CRISIS":
                            dialog.create(getActivity(), "CRISIS DE HIPERTENSIÓN", "Tu presión arterial está en crisis. ¡Llama a tu médico de inmediato!", callback);
                            ChangeFragment.change(getContext(), R.id.frame_layout, new ContactList());
                            return;
                        default:
                            break;
                    }

                    Measurement measurementResponse = response.body();

                    String challengeMessage = "";
                    for (Challenge challenge: measurementResponse.getCompletedChallenges()){
                        dialog.create(getActivity(), "Reto completado", "Has completado el reto: " + challenge.getName() + " y has ganado " + challenge.getReward() + " puntos.");
                    }

//                    ChangeFragment.change(getContext(), R.id.frame_layout, new MeasurementList());
                    //replaceFragment(new MeasurementList());
//                    sys.setText("");
//                    dis.setText("");
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