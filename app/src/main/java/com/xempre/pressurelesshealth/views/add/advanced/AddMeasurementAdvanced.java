package com.xempre.pressurelesshealth.views.add.advanced;

import static com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC;
import static com.google.android.gms.fitness.data.HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.fitness.data.Field;
import com.xempre.pressurelesshealth.MainActivity;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.api.GoogleFitCallback;
import com.xempre.pressurelesshealth.databinding.AdvancedAddMeasurementBinding;
import com.xempre.pressurelesshealth.interfaces.MeasurementService;
import com.xempre.pressurelesshealth.models.Measurement;
import com.xempre.pressurelesshealth.views.reports.MeasurementList.MeasurementList;

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
    MainActivity mainActivity;
    GoogleFitApi googleFitApi;

    EditText sys;
    EditText dis;

    List<Pair<Number, Number>> measurements;

    Number dateOld;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity)getActivity();
        googleFitApi = mainActivity.getGoogleFitApi();


        measurements = new ArrayList<>();
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
            }
        });

        binding.btnUpdate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_data(1);
            }
        });

        binding.btnUpdate3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_data(2);
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
                        Log.d("FECHA22", date.toString()+", "+ dateOld.toString());
                        if(this.measurements.size()<3) this.measurements.add(new Pair<>(systolicPressure, diastolicPressure));
                        if (this.measurements.size() == 3){
                            calculateMeasurement();
                        }
                    } else {
                        Toast.makeText(getActivity(), "No se ha detectado una nueva medida, vuelva a intentar en unos segundos."+ finalText, Toast.LENGTH_LONG).show();
                    }

                } else {
                    sys.setText(systolicPressure != null ? systolicPressure.toString() : null);
                    dis.setText(diastolicPressure != null ? diastolicPressure.toString() : null);
                    dateOld = date;
                    this.measurements.add(new Pair<>(systolicPressure, diastolicPressure));
                    Log.d("FECHA1", date.toString());
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

        MeasurementService measurementService = ApiClient.createService(MeasurementService.class);
        DecimalFormat df = new DecimalFormat("0.00");
        Measurement measurement = new Measurement(2, new Float(df.format(res.first)), new Float(df.format(res.second)), date, true);

        Call<Measurement> call = measurementService.save(measurement);

        call.enqueue(new Callback<Measurement>() {
            @Override
            public void onResponse(Call<Measurement> call, Response<Measurement> response) {
                if (response.code() == 201){
                    Toast.makeText(getContext(), "Medida guardada exitosamente.", Toast.LENGTH_SHORT).show();
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


        if (measurements == null || measurements.isEmpty()) {
            throw new IllegalArgumentException("La lista no puede ser nula o vacía.");
        }

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
