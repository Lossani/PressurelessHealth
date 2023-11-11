package com.xempre.pressurelesshealth.views.profile.goals;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.ActivityListMeasurementBinding;
import com.xempre.pressurelesshealth.databinding.GoalListBinding;
import com.xempre.pressurelesshealth.interfaces.GoalService;
import com.xempre.pressurelesshealth.interfaces.MeasurementService;
import com.xempre.pressurelesshealth.models.Goal;
import com.xempre.pressurelesshealth.models.Measurement;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoalList extends Fragment {
    private GoalListBinding binding;
    private LineChart lineChart;

    private List<String> xValues;

    private TextView promDiastolic;
    private TextView promSystolic;

    List<Entry> entries1;
    List<Entry> entries2;

    private RecyclerView recyclerView;
    private GoalAdapter goalAdapter;
    private List<Goal> goalList = new ArrayList<Goal>();
    MaterialDatePicker picker;
    ApiClient apiClient;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


//        View view = inflater.inflate(R.layout.measurement_list, container, false);

        binding = GoalListBinding.inflate(inflater, container, false);
//        lineChart = binding.chart;
//        apiClient = new ApiClient();

        recyclerView = binding.reciclerviewProfile;
        goalAdapter = new GoalAdapter(getContext(), goalList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(goalAdapter);

        callAPI();


        return binding.getRoot();

    }

    public void callAPI(){

            GoalService goalService = ApiClient.createService(GoalService.class);

            Call<List<Goal>> call = goalService.getAll();

            call.enqueue(new Callback<List<Goal>>() {
                @Override
                public void onResponse(Call<List<Goal>> call, Response<List<Goal>> response) {
                    // this method is called when we get response from our api.
                    try {
                        List<Goal> responseFromAPI = response.body();
                        int i = 0;
                        float prom1 = 0;
                        float prom2 = 0;
                        assert responseFromAPI != null;
                        clearRecyclerView();
                        if (responseFromAPI.isEmpty()) {
                            if (getContext()!=null) Toast.makeText(getContext(), "No se encontraron registros.", Toast.LENGTH_SHORT).show();
                        } else {
                            for (Goal element : responseFromAPI) {
                                Log.d("PERRUNO", element.toString());
                                i +=1;
                                Goal temp = new Goal(element);
                                goalList.add(temp);
                            }
//                            DecimalFormat df = new DecimalFormat("0.00");
////                            promDiastolic.setText(df.format(prom2/i)+"");
////                            promSystolic.setText(df.format(prom1/i)+"");
                            goalAdapter.notifyDataSetChanged();
                        }

                    } catch (Exception ignored){
                        if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista1.", Toast.LENGTH_SHORT).show();
                        Log.d("ERROR", ignored.getMessage());
                        onDestroyView();
                    }
                }

                @Override
                public void onFailure(Call<List<Goal>> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                    if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista.", Toast.LENGTH_SHORT).show();
                    onDestroyView();
                    // setting text to our text view when
                    // we get error response from API.
//                responseTV.setText("Error found is : " + t.getMessage());
                }
            });
    }

    public void clearRecyclerView(){

        int size = goalList.size();
        goalList.clear();
        goalAdapter.notifyItemRangeRemoved(0,size);
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
    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}