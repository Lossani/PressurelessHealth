package com.xempre.pressurelesshealth.views.profile.goal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.GoalListBinding;
import com.xempre.pressurelesshealth.interfaces.GoalService;
import com.xempre.pressurelesshealth.models.Goal;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoalList extends Fragment {
    private GoalListBinding binding;
    private RecyclerView recyclerView;
    private GoalAdapter goalAdapter;
    private List<Goal> goalList = new ArrayList<Goal>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = GoalListBinding.inflate(inflater, container, false);

        recyclerView = binding.reciclerviewProfile;
        goalAdapter = new GoalAdapter(getContext(), goalList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(goalAdapter);

        callAPI();

        binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Aquí puedes realizar la carga de datos nuevamente, por ejemplo, consultando una API
                // Después de cargar los nuevos datos, asegúrate de llamar a setRefreshing(false) para indicar que la recarga ha terminado.
                // En este ejemplo, simplemente simulo una recarga con un retardo de 2 segundos
                callAPI();
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Agrega aquí tu lógica para recargar los datos
                        // Por ejemplo, adapter.notifyDataSetChanged();


                        // Después de actualizar los datos, asegúrate de llamar a setRefreshing(false)
                        if (binding!=null) binding.swiperefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });


        return binding.getRoot();

    }

    public void callAPI(){

            GoalService goalService = ApiClient.createService(getContext(), GoalService.class,1);

            Call<List<Goal>> call = goalService.getAll();

            call.enqueue(new Callback<List<Goal>>() {
                @Override
                public void onResponse(Call<List<Goal>> call, Response<List<Goal>> response) {
                    try {
                        List<Goal> responseFromAPI = response.body();
                        //assert responseFromAPI != null;
                        clearRecyclerView();
                        if (responseFromAPI.isEmpty()) {
                            if (getContext()!=null) Toast.makeText(getContext(), "No se encontraron registros.", Toast.LENGTH_SHORT).show();
                        } else {
                            for (Goal element : responseFromAPI) {
                                Log.d("PERRUNO", element.toString());
                                Goal temp = new Goal(element);
                                goalList.add(temp);
                            }
                            goalAdapter.notifyDataSetChanged();
                        }

                    } catch (Exception ignored){
                        if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista de Logros.", Toast.LENGTH_SHORT).show();
                        Log.d("ERROR", ignored.getMessage());
                        onDestroyView();
                    }
                }

                @Override
                public void onFailure(Call<List<Goal>> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                    if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista.", Toast.LENGTH_SHORT).show();
                    onDestroyView();
                }
            });
    }

    public void clearRecyclerView(){

        int size = goalList.size();
        goalList.clear();
        goalAdapter.notifyItemRangeRemoved(0,size);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("PERRO", "PAUSA");
        if (binding.swiperefresh.isRefreshing()){
            binding.swiperefresh.setRefreshing(false);
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("PERRO", "PAUSA");
        if (binding.swiperefresh.isRefreshing()){
            binding.swiperefresh.setRefreshing(false);
        }

    }
}