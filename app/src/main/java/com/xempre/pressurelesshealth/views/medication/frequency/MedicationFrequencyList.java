package com.xempre.pressurelesshealth.views.medication.frequency;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.ListAddBinding;
import com.xempre.pressurelesshealth.interfaces.MedicationService;
import com.xempre.pressurelesshealth.models.Medication;
import com.xempre.pressurelesshealth.models.MedicationFrequency;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicationFrequencyList extends Fragment {

    private ListAddBinding binding;
    private RecyclerView recyclerView;
    private MedicationFrequencyAdapter medicationFrequencyAdapter;
    private List<MedicationFrequency> medicationFrequencyList = new ArrayList<MedicationFrequency>();

    Medication medication;

    public MedicationFrequencyList(Medication medication){
        this.medication = medication;
//        Log.d("RECIBI", id+"");
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        binding = ListAddBinding.inflate(inflater, container, false);
        binding.textView19.setText("Frecuencia de dosis:");

        recyclerView = binding.rvMedicationList;
        medicationFrequencyAdapter = new MedicationFrequencyAdapter(getContext(), medicationFrequencyList, medication);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(medicationFrequencyAdapter);

        binding.btnAddListAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMedicationFrequency addMedicationFrequency = new AddMedicationFrequency(medication);
                ChangeFragment.change(getContext(), R.id.frame_layout, addMedicationFrequency);
            }
        });

        callAPI();


        return binding.getRoot();

    }

    public void callAPI(){

        MedicationService medicationService = ApiClient.createService(getContext(), MedicationService.class,1);

        Call<List<MedicationFrequency>> call = medicationService.getAllMedicationFrequencies(medication.getId());

        call.enqueue(new Callback<List<MedicationFrequency>>() {
            @Override
            public void onResponse(Call<List<MedicationFrequency>> call, Response<List<MedicationFrequency>> response) {
                try {
                    List<MedicationFrequency> responseFromAPI = response.body();
                    assert responseFromAPI != null;
                    if (responseFromAPI.isEmpty()) {
                        if (getContext()!=null) {
                            binding.tvMessageAddList.setVisibility(View.VISIBLE);
                            binding.tvMessageAddList.setText("No se encontraron frecuencias registradas.");
                            //Toast.makeText(getContext(), "No se encontraron registros.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        binding.tvMessageAddList.setVisibility(View.INVISIBLE);
                        for (MedicationFrequency element : responseFromAPI) {
//                            LeaderboardItem temp = new LeaderboardItem(element);
                            medicationFrequencyList.add(element);
                        }
                        medicationFrequencyAdapter.notifyDataSetChanged();
                    }

                } catch (Exception ignored){
                    if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista de medicaciones.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", ignored.getMessage());
                    onDestroyView();
                }
            }

            @Override
            public void onFailure(Call<List<MedicationFrequency>> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
                if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista de medicaciones.", Toast.LENGTH_SHORT).show();
                onDestroyView();
            }
        });
    }

}
