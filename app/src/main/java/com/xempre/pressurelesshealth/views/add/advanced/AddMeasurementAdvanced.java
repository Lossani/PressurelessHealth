package com.xempre.pressurelesshealth.views.add.advanced;

import static android.content.Context.MODE_PRIVATE;
import static com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC;
import static com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.google.android.gms.fitness.data.Field;
import com.xempre.pressurelesshealth.MainActivity;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.api.GoogleFitCallback;
import com.xempre.pressurelesshealth.databinding.AdvancedAddMeasurementBinding;
import com.xempre.pressurelesshealth.interfaces.MeasurementService;
import com.xempre.pressurelesshealth.models.Challenge;
import com.xempre.pressurelesshealth.models.Measurement;
import com.xempre.pressurelesshealth.utils.Constants;
import com.xempre.pressurelesshealth.views.MainActivityView;
import com.xempre.pressurelesshealth.views.add.SelectAddMode;
import com.xempre.pressurelesshealth.views.medication.MedicationList;
import com.xempre.pressurelesshealth.views.reports.MeasurementList.MeasurementList;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;
import com.xempre.pressurelesshealth.views.shared.CustomDialog;
import com.xempre.pressurelesshealth.views.shared.MinMaxFilter;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMeasurementAdvanced extends Fragment {

    private AdvancedAddMeasurementBinding binding;
    MainActivityView mainActivity;
    GoogleFitApi googleFitApi;

    EditText sys;
    EditText dis;

    Pair<Number, Number>[] measurements;

    Number dateOld;

    Dialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivityView)getActivity();
        googleFitApi = mainActivity.getGoogleFitApi();

        measurements = new Pair[3];

        // Inicializar el arreglo con pares (0,0)
        for (int i = 0; i < measurements.length; i++) {
            measurements[i] = new Pair<>(0, 0);
        }

//        measurements = new ArrayList<>();
        dateOld = null;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = AdvancedAddMeasurementBinding.inflate(inflater, container, false);

        binding.saveButtonAdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveButton();
                } catch (Exception ignored){
                    Toast.makeText(getContext(), "Asegurece de ingresar números validos.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.btnUpdate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_data(0);
                calculateMeasurement();
            }
        });

        binding.btnUpdate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_data(1);
                calculateMeasurement();
            }
        });

        binding.btnUpdate3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_data(2);
                calculateMeasurement();
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());;
        boolean isGoogleAuth = sharedPreferences.getBoolean(Constants.SETTINGS_GOOGLE_AUTH_SIGNED_IN, false);

        if (!isGoogleAuth){
            binding.btnUpdate1.setVisibility(View.INVISIBLE);
            binding.btnUpdate2.setVisibility(View.INVISIBLE);
            binding.btnUpdate3.setVisibility(View.INVISIBLE);
        }


        binding.btnBackAddAdvanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectAddMode = new SelectAddMode();
                ChangeFragment.change(getContext(), R.id.frame_layout, selectAddMode);
            }
        });

        binding.fabAddAdvanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.ok_dialog);
                dialog.setCancelable(false);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TextView title;
                title = dialog.findViewById(R.id.tvTitleOkDialog);
                title.setText("Medida Avanzada");

                TextView content;
                content = dialog.findViewById(R.id.tvContentOkDialog);
                content.setText(Html.fromHtml(
                        "<Este modo brinda medidas más precisas y confiables para realizar un correcto seguimiento de su presión.<br>\n" +
                                "Para realizar esta medida correctamente, debe seguir los siguientes pasos:<br>\n" +
                                "1.- Tomar la medida 1.<br>\n" +
                                "2.- Esperar al menos 1 minuto.<br>\n" +
                                "3.- Tomar la medida 2.<br>\n" +
                                "4.- Esperar al menos 1 minuto.<br>\n" +
                                "5.- Tomar la medida 3.<br>\n" +
                                "Una vez realizados estos pasos se le mostrará el resultado que podrá almacenar al presionar el botón <b>Guardar</b>."
                ));


                dialog.findViewById(R.id.btnOkDialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


        binding.etSystolicAd1.setFilters(new InputFilter[]{new MinMaxFilter(1,250)});
        binding.etSystolicAd2.setFilters(new InputFilter[]{new MinMaxFilter(1,250)});
        binding.etSystolicAd3.setFilters(new InputFilter[]{new MinMaxFilter(1,250)});
        binding.etDiastolicAd1.setFilters(new InputFilter[]{new MinMaxFilter(1,200)});
        binding.etDiastolicAd2.setFilters(new InputFilter[]{new MinMaxFilter(1,200)});
        binding.etDiastolicAd3.setFilters(new InputFilter[]{new MinMaxFilter(1,200)});

        binding.etDiastolicAd1.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals("")) calculateMeasurement();

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etSystolicAd1.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals("")) calculateMeasurement();
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etDiastolicAd2.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals("")) calculateMeasurement();
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etSystolicAd2.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals("")) calculateMeasurement();
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etDiastolicAd3.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals("")) calculateMeasurement();
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etSystolicAd3.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals("")) calculateMeasurement();
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etSystolicAd1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(binding.etSystolicAd1.getText().length() == 1 && binding.etSystolicAd1.getText().toString().trim().equals("0")){
                        binding.etSystolicAd1.setText("");
                    }
                }else {
                    if(binding.etSystolicAd1.getText().length() == 0){
                        binding.etSystolicAd1.setText("0");
                    }
                }
            }
        });
        binding.etSystolicAd2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(binding.etSystolicAd2.getText().length() == 1 && binding.etSystolicAd2.getText().toString().trim().equals("0")){
                        binding.etSystolicAd2.setText("");
                    }
                }else {
                    if(binding.etSystolicAd2.getText().length() == 0){
                        binding.etSystolicAd2.setText("0");
                    }
                }
            }
        });
        binding.etSystolicAd3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(binding.etSystolicAd3.getText().length() == 1 && binding.etSystolicAd3.getText().toString().trim().equals("0")){
                        binding.etSystolicAd3.setText("");
                    }
                }else {
                    if(binding.etSystolicAd3.getText().length() == 0){
                        binding.etSystolicAd3.setText("0");
                    }
                }
            }
        });
        binding.etDiastolicAd1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(binding.etDiastolicAd1.getText().length() == 1 && binding.etDiastolicAd1.getText().toString().trim().equals("0")){
                        binding.etDiastolicAd1.setText("");
                    }
                }else {
                    if(binding.etDiastolicAd1.getText().length() == 0){
                        binding.etDiastolicAd1.setText("0");
                    }
                }
            }
        });
        binding.etDiastolicAd2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(binding.etDiastolicAd2.getText().length() == 1 && binding.etDiastolicAd2.getText().toString().trim().equals("0")){
                        binding.etDiastolicAd2.setText("");
                    }
                }else {
                    if(binding.etDiastolicAd2.getText().length() == 0){
                        binding.etDiastolicAd2.setText("0");
                    }
                }
            }
        });
        binding.etDiastolicAd3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(binding.etDiastolicAd3.getText().length() == 1 && binding.etDiastolicAd3.getText().toString().trim().equals("0")){
                        binding.etDiastolicAd3.setText("");
                    }
                }else {
                    if(binding.etDiastolicAd3.getText().length() == 0){
                        binding.etDiastolicAd3.setText("0");
                    }
                }
            }
        });

        return binding.getRoot();
    }

    public void update_data(int op){
        String text = "";
        switch (op){
            case 0:
                dis = binding.etDiastolicAd1;
                sys = binding.etSystolicAd1;
                text = "1";
            break;
            case 1:
                dis = binding.etDiastolicAd2;
                sys = binding.etSystolicAd2;
                text = "3";
                break;
            case 2:
                dis = binding.etDiastolicAd3;
                sys = binding.etSystolicAd3;
                text = "2";
                break;
            default:
                return;
        }
        AtomicInteger cont = new AtomicInteger();
        if (googleFitApi != null) {
            String finalText = text;
            GoogleFitCallback googleFitCallback = (Map<String, Number> measurements) -> {
                Number systolicPressure = measurements.get(FIELD_BLOOD_PRESSURE_SYSTOLIC.getName());
                Number diastolicPressure = measurements.get(FIELD_BLOOD_PRESSURE_DIASTOLIC.getName());
//                Float datePressure = measurements.get(FIELD_.getName());
                Number heartRate = measurements.get(Field.FIELD_BPM.getName());
                Number date = measurements.get("DATE");

                String st1 = dateOld!=null?dateOld.toString():"";
                String st2 = date != null?date.toString():"";
                cont.addAndGet(1);


                if (!st1.equals("")){
                    if (!(st1.equals(st2))) {
                        sys.setText(systolicPressure != null ? systolicPressure.toString() : null);
                        dis.setText(diastolicPressure != null ? diastolicPressure.toString() : null);
                        dateOld = date;
                        this.measurements[op] = new Pair<>(systolicPressure, diastolicPressure);
                        Log.d("FECHA22", date.toString()+", "+ dateOld.toString());
//                        if(this.measurements.size()<3) this.measurements.add(new Pair<>(systolicPressure, diastolicPressure));
//                        if (this.measurements.size() == 3){
//                            calculateMeasurement();
//                        }
                    } else {
                        Toast.makeText(getActivity(), "No se ha detectado una nueva medida reciente en Google Fit."+ finalText, Toast.LENGTH_LONG).show();
                    }

                } else {
                    sys.setText(systolicPressure != null ? systolicPressure.toString() : null);
                    dis.setText(diastolicPressure != null ? diastolicPressure.toString() : null);
                    dateOld = date;
                    this.measurements[0] = new Pair<>(systolicPressure, diastolicPressure);
//                    this.measurements.add(new Pair<>(systolicPressure, diastolicPressure));
//                    Log.d("FECHA1", date.toString());
                }
            };

            ZonedDateTime endTime = LocalDateTime.now().atZone(ZoneId.systemDefault());
            ZonedDateTime startTime = endTime.minusHours(12);
            googleFitApi.readBloodPressureMeasurement(mainActivity, googleFitCallback, startTime, endTime);
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void saveButton(){

//        float sr = Float.parseFloat(sys.getText().toString());
//        float dr = Float.parseFloat(dis.getText().toString());

        Pair<Number, Number> res = calculateMeasurement();

        String pattern = "yyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());

        MeasurementService measurementService = ApiClient.createService(getContext(), MeasurementService.class, 1);
        DecimalFormat df = new DecimalFormat("0.00");
        if (new Float(df.format(res.first))<0 || new Float(df.format(res.second))<0 || new Float(df.format(res.first)) > 250 || new Float(df.format(res.second)) > 150){
            Toast.makeText(getContext(), "Asegurece de ingresar números validos. Revise las medidas por favor.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref",MODE_PRIVATE);

        int userId = sharedPreferences.getInt("userId", 0);

        Measurement measurement = new Measurement(userId, new Float(df.format(res.first)), new Float(df.format(res.second)), date, true);

        if(!binding.emltCommentAdvanced.getText().toString().equals(""))
            measurement.setComments(binding.emltCommentAdvanced.getText().toString());

        Call<Measurement> call = measurementService.save(measurement);

        call.enqueue(new Callback<Measurement>() {
            @Override
            public void onResponse(Call<Measurement> call, Response<Measurement> response) {
                if (response.code() == 201){
                    Toast.makeText(getContext(), "Medida guardada exitosamente.", Toast.LENGTH_SHORT).show();

                    Measurement measurementResponse = response.body();
                    CustomDialog dialog = new CustomDialog();
                    for (Challenge challenge: measurementResponse.getCompletedChallenges()){
                        dialog.create(getActivity(), "Reto completado", "Has completado el reto: " + challenge.getName() + " y has ganado " + challenge.getReward() + " puntos.");
                    }

                    replaceFragment(new MeasurementList());
                } else {
                    Log.d("ADVANCE", response.toString() + res.first.floatValue() + res.second.floatValue());
                    Toast.makeText(getContext(), "Ocurrio un error. Error " + response.code(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Measurement> call, Throwable t) {
                Toast.makeText(getContext(), "ERROR", Toast.LENGTH_LONG).show();
            }
        });
    }

    private Pair<Number, Number> calculateMeasurement() {
        Log.d("TAG", "MEDIR");


//        if (measurements == null) {
//            throw new IllegalArgumentException("La lista no puede ser nula o vacía.");
//        }

        if (binding.etSystolicAd1.getText().toString().equals("")) binding.etSystolicAd1.setText("0");
        if (binding.etSystolicAd2.getText().toString().equals("")) binding.etSystolicAd2.setText("0");
        if (binding.etSystolicAd3.getText().toString().equals("")) binding.etSystolicAd3.setText("0");
        if (binding.etDiastolicAd1.getText().toString().equals("")) binding.etDiastolicAd1.setText("0");
        if (binding.etDiastolicAd2.getText().toString().equals("")) binding.etDiastolicAd2.setText("0");
        if (binding.etDiastolicAd3.getText().toString().equals("")) binding.etDiastolicAd3.setText("0");

        measurements[0] = new Pair<>(Float.valueOf(binding.etSystolicAd1.getText().toString()), Float.valueOf(binding.etDiastolicAd1.getText().toString()));
        measurements[1] = new Pair<>(Float.valueOf(binding.etSystolicAd2.getText().toString()), Float.valueOf(binding.etDiastolicAd2.getText().toString()));
        measurements[2] = new Pair<>(Float.valueOf(binding.etSystolicAd3.getText().toString()), Float.valueOf(binding.etDiastolicAd3.getText().toString()));


        float sumaDis = 0;
        float sumaSys = 0;
        int cont = 0;
        // Iterar sobre la lista y sumar los segundos elementos de cada par
        for (Pair<Number, Number> par : measurements) {
            if (cont>0){
                sumaDis += par.second.doubleValue();
                sumaSys += par.first.doubleValue();
            }
            cont+=1;
        }

        float sr = sumaSys/2;
        float dr = sumaDis/2;

        DecimalFormat df = new DecimalFormat("0.00");

        binding.tvSysRes.setText(df.format(sr));
        binding.tvDiasRes.setText(df.format(dr));


        return new Pair<Number,Number>(sr, dr);

//        for ()
    }
}
