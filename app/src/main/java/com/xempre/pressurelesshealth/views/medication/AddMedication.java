package com.xempre.pressurelesshealth.views.medication;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.MedicationAddBinding;
import com.xempre.pressurelesshealth.interfaces.MedicationService;
import com.xempre.pressurelesshealth.models.Medication;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddMedication extends Fragment {
    private MedicationAddBinding binding;

    Medication medication;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        binding = MedicationAddBinding.inflate(inflater, container, false);

        binding.btnMedicationSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.editTextText.getText().toString().equals("") && !binding.editTextText2.getText().toString().equals("") ){
                    Medication medication = new Medication();
                    medication.setUserId(2);
                    medication.setDeleted(false);
                    medication.setName(binding.editTextText.getText().toString());
                    medication.setDescription(binding.editTextText2.getText().toString());
                    callAPI(medication);
                } else {
                    Toast.makeText(getContext(), "Verifique que los campos no estén vacíos.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return binding.getRoot();

    }

    public void callAPI(Medication medication){

        MedicationService medicationService = ApiClient.createService(MedicationService.class);

        Call<Medication> call = medicationService.saveMedication(medication);

        call.enqueue(new Callback<Medication>() {
            @Override
            public void onResponse(Call<Medication> call, Response<Medication> response) {
                try {
                    Medication responseFromAPI = response.body();

                    if (response.code()==201) {
                        Toast.makeText(getContext(), "Medicamento guardado exitosamente.", Toast.LENGTH_SHORT).show();

                        Fragment medicationView = new MedicationList();
                        ChangeFragment.change(getContext(), R.id.frame_layout, medicationView);
                    }
                } catch (Exception ignored){
                    if (getContext()!=null) Toast.makeText(getContext(), "Error al guardar medicamento.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", ignored.getMessage());
                    onDestroyView();
                }
            }

            @Override
            public void onFailure(Call<Medication> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
                if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista.", Toast.LENGTH_SHORT).show();
                onDestroyView();
            }
        });
    }
}