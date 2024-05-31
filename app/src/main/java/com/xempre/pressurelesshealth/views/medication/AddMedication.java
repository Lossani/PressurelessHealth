package com.xempre.pressurelesshealth.views.medication;
import static android.content.Context.MODE_PRIVATE;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
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

import org.apache.commons.math3.analysis.function.Add;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddMedication extends Fragment {
    private MedicationAddBinding binding;

    Medication medication;

    boolean isEditMode;

    AddMedication(){
        isEditMode = false;
    }
    AddMedication(Medication medication){
        this.medication = medication;
        isEditMode = true;
    }

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

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref",MODE_PRIVATE);

                    int userId = sharedPreferences.getInt("userId", 0);

                    Medication medicationTemp = new Medication();

                    medicationTemp.setUserId(userId);
                    medicationTemp.setDeleted(false);
                    medicationTemp.setName(binding.editTextText.getText().toString());
                    medicationTemp.setDescription(binding.editTextText2.getText().toString());
                    if (!isEditMode) callAPI(medicationTemp);
                    else {
                        medicationTemp.setId(medication.getId());
                        updateMedication(medicationTemp);}
                } else {
                    Toast.makeText(getContext(), "Verifique que los campos no estén vacíos.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.btnBackAddMedicina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment.back(getContext());
            }
        });

        if (isEditMode){
            binding.editTextText.setText(medication.getName());
            binding.editTextText2.setText(medication.getDescription());
            binding.textView20.setText("Editar Medicina");
        }

        return binding.getRoot();

    }

    public void updateMedication(Medication medication){

        MedicationService medicationService = ApiClient.createService(getContext(), MedicationService.class,1);

//        Medication temp = new Medication();
//        temp.setDeleted(true);

        Call<Medication> call = medicationService.updateMedication(medication.getId(), medication);

        call.enqueue(new Callback<Medication>() {
            @Override
            public void onResponse(Call<Medication> call, Response<Medication> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getContext(), "Se editó la medicación.", Toast.LENGTH_SHORT).show();
                    ChangeFragment.change(getContext(), R.id.frame_layout, new MedicationList());
                } else {
                    Toast.makeText(getContext(), "Error al intentar eliminar la medicación.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", response.message());
                    Log.d("ERROR", String.valueOf(response.body()));
                }
            }

            @Override
            public void onFailure(Call<Medication> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
            }
        });
    }

    public void callAPI(Medication medication){

        MedicationService medicationService = ApiClient.createService(getContext(), MedicationService.class, 1);

        Call<Medication> call = medicationService.saveMedication(medication);

        call.enqueue(new Callback<Medication>() {
            @Override
            public void onResponse(Call<Medication> call, Response<Medication> response) {
                try {
                    Medication responseFromAPI = response.body();

                    if (response.code()==201) {
                        Toast.makeText(getContext(), "Medicamento guardado exitosamente.", Toast.LENGTH_SHORT).show();

                        Fragment medicationList = new MedicationList();
                        ChangeFragment.change(getContext(), R.id.frame_layout, medicationList);
                    } else {
                        if (getContext()!=null) Toast.makeText(getContext(), "Error al guardar medicamento.", Toast.LENGTH_SHORT).show();
                        Log.d("Error-Save", response.errorBody().toString());
                        onDestroyView();
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
                if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista de medicamentos.", Toast.LENGTH_SHORT).show();
                onDestroyView();
            }
        });
    }
}
