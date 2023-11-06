package com.xempre.pressurelesshealth.views.reports.MeasurementList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.databinding.ActivityListMeasurementBinding;
import com.xempre.pressurelesshealth.interfaces.RecordService;
import com.xempre.pressurelesshealth.models.Record;
import com.xempre.pressurelesshealth.views.AddMeasurement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private List<Record> listaNombres = new ArrayList<Record>();

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
                    .baseUrl("http://192.168.0.3:5000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RecordService recordService = retrofit.create(RecordService.class);

            // calling a method to create a post and passing our modal class.
            Call<List<Record>> call = recordService.getAll();

            // on below line we are executing our method.
            call.enqueue(new Callback<List<Record>>() {
                @Override
                public void onResponse(Call<List<Record>> call, Response<List<Record>> response) {
                    // this method is called when we get response from our api.
                    try {
                        List<Record> responseFromAPI = response.body();
                        int i = 0;
                        for (Record element : responseFromAPI) {
                            Record temp = new Record(element);
                            listaNombres.add(temp);
                            if (getContext()!=null) Toast.makeText(getContext(), Integer.toString(temp.getSystolicRecord()), Toast.LENGTH_LONG).show();
                        }
                        measurementAdapter.notifyDataSetChanged();
                    } catch (Exception ignored){}
                }

                @Override
                public void onFailure(Call<List<Record>> call, Throwable t) {
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