package com.xempre.pressurelesshealth.views.reports.MeasurementList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.xempre.pressurelesshealth.databinding.ActivityListMeasurementBinding;
import com.xempre.pressurelesshealth.interfaces.MeasurementService;
import com.xempre.pressurelesshealth.models.Measurement;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeasurementList extends Fragment {
    private ActivityListMeasurementBinding binding;
    private LineChart lineChart;

    private List<String> xValues;

    List<Entry> entries1;
    List<Entry> entries2;

    private RecyclerView recyclerView;
    private MeasurementAdapter measurementAdapter;
    private List<Measurement> listaNombres = new ArrayList<Measurement>();

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

        callAPI();
        // Agrega algunos nombres a la lista
//        listaNombres.add("Juan");
//        listaNombres.add("María");
//        listaNombres.add("Luis");

//        Toast.makeText(getContext(), "PERRITO", Toast.LENGTH_SHORT).show();

        //callAPI();

        return binding.getRoot();

    }

    public void callAPI(){
        try{
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://health.xempre.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MeasurementService measurementService = retrofit.create(MeasurementService.class);

            // calling a method to create a post and passing our modal class.
            Call<List<Measurement>> call = measurementService.getAll();

            // on below line we are executing our method.
            call.enqueue(new Callback<List<Measurement>>() {
                @Override
                public void onResponse(Call<List<Measurement>> call, Response<List<Measurement>> response) {
                    // this method is called when we get response from our api.
                    try {
                        List<Measurement> responseFromAPI = response.body();
                        int i = 0;
                        assert responseFromAPI != null;
                        if (responseFromAPI.isEmpty()) {
                            if (getContext()!=null) Toast.makeText(getContext(), "No se encontraron registros.", Toast.LENGTH_LONG).show();
                        }
                        for (Measurement element : responseFromAPI) {
                            Measurement temp = new Measurement(element);
                            listaNombres.add(temp);
                            if (getContext()!=null) Toast.makeText(getContext(), String.valueOf(temp.getSystolicRecord()), Toast.LENGTH_LONG).show();
                        }
                        measurementAdapter.notifyDataSetChanged();
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
        } catch (Exception ignore){}

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