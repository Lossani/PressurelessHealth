package com.xempre.pressurelesshealth.views.medication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.ListAddBinding;
import com.xempre.pressurelesshealth.interfaces.MedicationService;
import com.xempre.pressurelesshealth.models.Medication;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicationList extends Fragment {

    private ListAddBinding binding;
    private RecyclerView recyclerView;
    private MedicationAdapter medicationAdapter;
    private List<Medication> medicationList = new ArrayList<Medication>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        binding = ListAddBinding.inflate(inflater, container, false);
        binding.textView19.setText("Mi Medicación");

        recyclerView = binding.rvMedicationList;
        medicationAdapter = new MedicationAdapter(getContext(), medicationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(medicationAdapter);

        binding.btnAddListAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment medicationView = new AddMedication();
                ChangeFragment.change(getContext(), R.id.frame_layout, medicationView);
            }
        });

        callAPI();


        return binding.getRoot();

    }

    public void callAPI(){

        MedicationService medicationService = ApiClient.createService(getContext(), MedicationService.class,1);

        Call<List<Medication>> call = medicationService.getAll();

        call.enqueue(new Callback<List<Medication>>() {
            @Override
            public void onResponse(Call<List<Medication>> call, Response<List<Medication>> response) {
                try {
                    List<Medication> responseFromAPI = response.body();
                    assert responseFromAPI != null;
                    if (responseFromAPI.isEmpty()) {
                        if (getContext()!=null){
                            binding.tvMessageAddList.setVisibility(View.VISIBLE);
                            binding.tvMessageAddList.setText("No se encontraron medicamentos.");
                            //Toast.makeText(getContext(), "No se encontraron registros.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        binding.tvMessageAddList.setVisibility(View.INVISIBLE);
                        medicationList.clear();
                        medicationList.addAll(responseFromAPI);
                        /*for (Medication element : responseFromAPI) {
//                            LeaderboardItem temp = new LeaderboardItem(element);
                            medicationList.add(element);
                        }*/
                        medicationAdapter.notifyDataSetChanged();
                    }

                } catch (Exception ignored){
                    if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista de medicamentos.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", ignored.getMessage());
                    onDestroyView();
                }
            }

            @Override
            public void onFailure(Call<List<Medication>> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
                if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista de medicamentos.", Toast.LENGTH_SHORT).show();
                onDestroyView();
            }
        });
    }
}
