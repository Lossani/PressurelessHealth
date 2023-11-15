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


        return binding.getRoot();

    }

    public void callAPI(){

        ChallengeService challengeService = ApiClient.createService(ChallengeService.class);

        Call<List<Challenge>> call = challengeService.getAll();

        call.enqueue(new Callback<List<Challenge>>() {
            @Override
            public void onResponse(Call<List<Challenge>> call, Response<List<Challenge>> response) {
                try {
                    List<Challenge> responseFromAPI = response.body();
                    assert responseFromAPI != null;
                    clearRecyclerView();
                    if (responseFromAPI.isEmpty()) {
                        if (getContext()!=null) Toast.makeText(getContext(), "No se encontraron registros.", Toast.LENGTH_SHORT).show();
                    } else {
                        for (Challenge element : responseFromAPI) {
                            Log.d("PERRUNO", element.toString());
                            Challenge temp = new Challenge(element);
                            challengeList.add(temp);
                        }
                        challengeAdapter.notifyDataSetChanged();
                    }

                } catch (Exception ignored){
                    if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista1.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", ignored.getMessage());
                    onDestroyView();
                }
            }

            @Override
            public void onFailure(Call<List<Challenge>> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
                if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista.", Toast.LENGTH_SHORT).show();
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
        binding = null;
    }

}
