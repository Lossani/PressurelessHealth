package com.xempre.pressurelesshealth.views.leaderboard;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.GoalListBinding;
import com.xempre.pressurelesshealth.databinding.ListTitleBinding;
import com.xempre.pressurelesshealth.interfaces.GoalService;
import com.xempre.pressurelesshealth.interfaces.LeaderboardService;
import com.xempre.pressurelesshealth.models.Goal;
import com.xempre.pressurelesshealth.models.LeaderboardItem;
import com.xempre.pressurelesshealth.views.profile.goal.GoalAdapter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardList extends Fragment {

    private ListTitleBinding binding;
    private RecyclerView recyclerView;
    private LeaderboardAdapter leaderboardAdapter;
    private List<LeaderboardItem> leaderboardItemList = new ArrayList<LeaderboardItem>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        binding = ListTitleBinding.inflate(inflater, container, false);
        binding.tvListTitle.setText("Tabla de Clasificación");

        recyclerView = binding.rvListTitle;
        leaderboardAdapter = new LeaderboardAdapter(getContext(), leaderboardItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(leaderboardAdapter);

        callAPI();


        return binding.getRoot();

    }

    public void callAPI(){

        LeaderboardService leaderboardService = ApiClient.createService(getContext(), LeaderboardService.class, 1);

        Call<List<LeaderboardItem>> call = leaderboardService.getAll();

        call.enqueue(new Callback<List<LeaderboardItem>>() {
            @Override
            public void onResponse(Call<List<LeaderboardItem>> call, Response<List<LeaderboardItem>> response) {
                try {
                    List<LeaderboardItem> responseFromAPI = response.body();
                    assert responseFromAPI != null;
                    if (responseFromAPI.isEmpty()) {
                        if (getContext()!=null) Toast.makeText(getContext(), "No se encontraron registros.", Toast.LENGTH_SHORT).show();
                    } else {
                        for (LeaderboardItem element : responseFromAPI) {
                            Log.d("PERRUNO", element.getUsername());
                            Log.d("PERRUNO", element.getFirstName());
//                            LeaderboardItem temp = new LeaderboardItem(element);
                            leaderboardItemList.add(element);
                        }
                        leaderboardAdapter.notifyDataSetChanged();
                    }

                } catch (Exception ignored){
                    if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista1.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", ignored.getMessage());
                    onDestroyView();
                }
            }

            @Override
            public void onFailure(Call<List<LeaderboardItem>> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
                if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista.", Toast.LENGTH_SHORT).show();
                onDestroyView();
            }
        });
    }
}
