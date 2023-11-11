package com.xempre.pressurelesshealth.views.reports.MeasurementList;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.databinding.ActivityListMeasurementBinding;
import com.xempre.pressurelesshealth.interfaces.MeasurementService;
import com.xempre.pressurelesshealth.models.Measurement;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeasurementList extends Fragment {
    private ActivityListMeasurementBinding binding;
    private LineChart lineChart;

    private List<String> xValues;

    private TextView promDiastolic;
    private TextView promSystolic;

    List<Entry> entries1;
    List<Entry> entries2;

    private RecyclerView recyclerView;
    private MeasurementAdapter measurementAdapter;
    private List<Measurement> listaNombres = new ArrayList<Measurement>();
    MaterialDatePicker picker;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


//        View view = inflater.inflate(R.layout.measurement_list, container, false);

        binding = ActivityListMeasurementBinding.inflate(inflater, container, false);
//        lineChart = binding.chart;


        recyclerView = binding.recyclerView;
        measurementAdapter = new MeasurementAdapter(getContext(), listaNombres);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(measurementAdapter);
        promDiastolic = binding.tvDiasProm;
        promSystolic = binding.tvSysProm;

        binding.btnChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picker = MaterialDatePicker.Builder.dateRangePicker()
                        .setTheme(R.style.ThemeMaterialCalendar)
                        .setTitleText("Seleccionar rango de fechas.")
                        .setSelection(Pair.create(null, null))
                        .build();
                picker.show(getActivity().getSupportFragmentManager(), "TAG");

                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {

                        if(Objects.equals(selection.first, selection.second)) binding.tvDateRange.setText("Fecha: "+convertDateToString(selection.first));
                        else binding.tvDateRange.setText("Entre: "+convertDateToString(selection.first)+" - "+convertDateToString(selection.second));
                        callAPI(convertDateToString(selection.first), convertDateToString(selection.second));
                    }
                });
            }
        });
        long millis = Instant.now().toEpochMilli();
        callAPI(convertDateToString(millis), convertDateToString(millis));
        // Agrega algunos nombres a la lista
//        listaNombres.add("Juan");
//        listaNombres.add("María");
//        listaNombres.add("Luis");

//        Toast.makeText(getContext(), "PERRITO", Toast.LENGTH_SHORT).show();

        //callAPI();

        return binding.getRoot();

    }

    public String convertDateToString(Long time){
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.setTimeInMillis(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(utc.getTime());
    }

    public void callAPI(String startDate, String endDate){
        Log.d("PERRUNO", startDate+" "+endDate);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://health.xempre.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MeasurementService measurementService = retrofit.create(MeasurementService.class);

            // calling a method to create a post and passing our modal class.
            Call<List<Measurement>> call = measurementService.getAllByDateRange(startDate+"T00:00:00", endDate+"T23:59:59");
            Log.d("PERRUNO", startDate+" "+endDate);
            // on below line we are executing our method.
            call.enqueue(new Callback<List<Measurement>>() {
                @Override
                public void onResponse(Call<List<Measurement>> call, Response<List<Measurement>> response) {
                    // this method is called when we get response from our api.
                    try {
                        List<Measurement> responseFromAPI = response.body();
                        int i = 0;
                        float prom1 = 0;
                        float prom2 = 0;
                        assert responseFromAPI != null;
                        clearRecyclerView();
                        if (responseFromAPI.isEmpty()) {
                            if (getContext()!=null) Toast.makeText(getContext(), "No se encontraron registros.", Toast.LENGTH_SHORT).show();
                        } else {
                            for (Measurement element : responseFromAPI) {
                                i +=1;
                                Measurement temp = new Measurement(element);
                                listaNombres.add(temp);
                                prom1 += element.getDiastolicRecord();
                                prom2 += element.getSystolicRecord();
                            }
                            DecimalFormat df = new DecimalFormat("0.00");
                            promDiastolic.setText(df.format(prom2/i)+"");
                            promSystolic.setText(df.format(prom1/i)+"");
                            measurementAdapter.notifyDataSetChanged();
                        }

                    } catch (Exception ignored){}
                }

                @Override
                public void onFailure(Call<List<Measurement>> call, Throwable t) {
                    if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista.", Toast.LENGTH_SHORT).show();
                    // setting text to our text view when
                    // we get error response from API.
//                responseTV.setText("Error found is : " + t.getMessage());
                }
            });
    }

    public void clearRecyclerView(){
        promDiastolic.setText("0.00");
        promSystolic.setText("0.00");
        int size = listaNombres.size();
        listaNombres.clear();
        measurementAdapter.notifyItemRangeRemoved(0,size);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(MeasurementsList.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}