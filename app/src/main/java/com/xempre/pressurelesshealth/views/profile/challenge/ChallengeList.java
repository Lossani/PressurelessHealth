package com.xempre.pressurelesshealth.views.profile.challenge;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.GoalListBinding;
import com.xempre.pressurelesshealth.interfaces.ChallengeService;
import com.xempre.pressurelesshealth.interfaces.GoalService;
import com.xempre.pressurelesshealth.models.Challenge;
import com.xempre.pressurelesshealth.models.Goal;
import com.xempre.pressurelesshealth.views.profile.goal.GoalAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChallengeList extends Fragment {

    private GoalListBinding binding;
    private RecyclerView recyclerView;
    private ChallengeAdapter challengeAdapter;
    private List<Challenge> challengeList = new ArrayList<Challenge>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = GoalListBinding.inflate(inflater, container, false);

        recyclerView = binding.reciclerviewProfile;
        challengeAdapter = new ChallengeAdapter(getContext(), challengeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(challengeAdapter);

        callAPI();

        binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Aquí puedes realizar la carga de datos nuevamente, por ejemplo, consultando una API
                // Después de cargar los nuevos datos, asegúrate de llamar a setRefreshing(false) para indicar que la recarga ha terminado.
                // En este ejemplo, simplemente simulo una recarga con un retardo de 2 segundos

                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Agrega aquí tu lógica para recargar los datos
                        // Por ejemplo, adapter.notifyDataSetChanged();
                        callAPI();

                        // Después de actualizar los datos, asegúrate de llamar a setRefreshing(false)
                        if (binding!=null) binding.swiperefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });


        return binding.getRoot();

    }

    public void callAPI(){

        ChallengeService challengeService = ApiClient.createService(getContext(), ChallengeService.class,1);

        Call<List<Challenge>> call = challengeService.getAllEnabled();

        call.enqueue(new Callback<List<Challenge>>() {
            @Override
            public void onResponse(Call<List<Challenge>> call, Response<List<Challenge>> response) {
                try {
                    List<Challenge> responseFromAPI = response.body();
                    Gson gson = new Gson();
                    Log.d("INFO", gson.toJson(responseFromAPI));
                    if (responseFromAPI == null) {
                        // Manejar el caso en que la respuesta es nula
                        Log.e("ERROR", "La respuesta del servidor es nula.");
                        return;
                    }
                    clearRecyclerView();
                    if (responseFromAPI.isEmpty()) {
                        if (getContext()!=null) Toast.makeText(getContext(), "No se encontraron registros.", Toast.LENGTH_SHORT).show();
                    } else {
                        for (Challenge element : responseFromAPI) {
                            Challenge temp = new Challenge(element);
                            challengeList.add(temp);
                        }
                        challengeAdapter.notifyDataSetChanged();
                    }

                } catch (Exception ignored){
                    if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista de retos.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", ignored.getMessage());
                    onDestroyView();
                }
            }

            @Override
            public void onFailure(Call<List<Challenge>> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
                if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista de retos.", Toast.LENGTH_SHORT).show();
                onDestroyView();
            }
        });
    }

    public void clearRecyclerView(){

        int size = challengeList.size();
        challengeList.clear();
        challengeAdapter.notifyItemRangeRemoved(0,size);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding.swiperefresh.isRefreshing()){
            binding.swiperefresh.setRefreshing(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding.swiperefresh.isRefreshing()){
            binding.swiperefresh.setRefreshing(false);
        }
    }
}
