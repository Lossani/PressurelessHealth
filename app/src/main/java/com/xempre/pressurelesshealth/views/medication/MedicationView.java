package com.xempre.pressurelesshealth.views.medication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.ListAddBinding;
import com.xempre.pressurelesshealth.databinding.MedicationViewBinding;
import com.xempre.pressurelesshealth.interfaces.MedicationService;
import com.xempre.pressurelesshealth.models.Medication;
import com.xempre.pressurelesshealth.views.medication.frequency.MedicationFrequencyList;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicationView extends Fragment {

    private MedicationViewBinding binding;
    private RecyclerView recyclerView;
    private MedicationAdapter leaderboardAdapter;
    private List<Medication> medicationList = new ArrayList<Medication>();

    Medication medication;

    private void loadChildFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.flMedication, fragment);
        transaction.commit();
    }

    public MedicationView(Medication medication){
        this.medication = medication;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        binding = MedicationViewBinding.inflate(inflater, container, false);
        binding.tvMedicationViewName.setText(medication.getName());
        binding.tvMedicationViewDescription.setText(medication.getDescription());

        binding.btnBackMedicationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment.back(getContext());
            }
        });

        Fragment medicationView = new MedicationFrequencyList(medication);
        // ChangeFragment.change(getContext(), R.id.flMedication, medicationView);
        loadChildFragment(medicationView);

        return binding.getRoot();

    }

}
