package com.xempre.pressurelesshealth.views.reports.MeasurementList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.interfaces.RecordService;
import com.xempre.pressurelesshealth.models.Record;
import com.xempre.pressurelesshealth.views.AddMeasurement;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeasurementList extends AppCompatActivity {
    private LineChart lineChart;

    private List<String> xValues;

    List<Entry> entries1;
    List<Entry> entries2;

    private RecyclerView recyclerView;
    private MeasurementAdapter measurementAdapter;
    private List<Record> listMeasurements = new ArrayList<Record>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_measurement);
        recyclerView = findViewById(R.id.recyclerView);
        measurementAdapter = new MeasurementAdapter(MeasurementList.this, listMeasurements);
        recyclerView.setLayoutManager(new LinearLayoutManager(MeasurementList.this));
        recyclerView.setAdapter(measurementAdapter);
        callAPI();
    }

    public void callAPI(){
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
                Toast.makeText(MeasurementList.this, "test", Toast.LENGTH_LONG).show();



//                // below line is for hiding our progress bar.
//                loadingPB.setVisibility(View.GONE);
//
//                // on below line we are setting empty text
//                // to our both edit text.
//                jobEdt.setText("");
//                nameEdt.setText("");
//                listaNombres = response.body();
                // we are getting response from our body
                // and passing it to our modal class.
//                int i = 0;
                List<Record> responseFromAPI = response.body();
                int i = 0;
                for (Record element : responseFromAPI){
                    Record temp = new Record(element);
                    listMeasurements.add(temp);
                    Toast.makeText(MeasurementList.this, Integer.toString(temp.getSystolicRecord()), Toast.LENGTH_LONG).show();
                }
                measurementAdapter.notifyDataSetChanged();
//                createChart();

//                // on below line we are getting our data from modal class and adding it to our string.
                String responseString = "Response Code : " + response.code() + "\nName : "  + "\n" + "Job : ";

//                // below line we are setting our
//                // string to our text view.
//                message.setText(responseString);
            }

            @Override
            public void onFailure(Call<List<Record>> call, Throwable t) {
                Toast.makeText(MeasurementList.this, "Error", Toast.LENGTH_SHORT).show();
                // setting text to our text view when
                // we get error response from API.
//                responseTV.setText("Error found is : " + t.getMessage());
            }
        });
    }
}