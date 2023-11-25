package com.xempre.pressurelesshealth.views.medication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.models.LeaderboardItem;
import com.xempre.pressurelesshealth.models.Medication;
import com.xempre.pressurelesshealth.views.medication.frequency.MedicationFrequencyList;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationAdapterItemHolder> {

    private Context context;
    private List<Medication> leaderboardItemList;
    MedicationList medicationList = new MedicationList();
    public MedicationAdapter(Context context, List<Medication> leaderboardItemList) {
        this.context = context;
        this.leaderboardItemList = leaderboardItemList;
    }



    @Override
    public MedicationAdapterItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.medication_element, parent, false);
        return new MedicationAdapter.MedicationAdapterItemHolder(view);
    }

    @Override
    public void onBindViewHolder(MedicationAdapterItemHolder holder, int position) {
        Medication medication = leaderboardItemList.get(position);
        holder.tvName.setText(medication.getName());
//        holder.tvDescription.setText(medication.getDescription());

//        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Fragment medicationFrequencyList = new MedicationFrequencyList();
//        fragmentTransaction.replace(R.id.frame_layout, medicationFrequencyList);
//        fragmentTransaction.commit();

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment medicationView = new MedicationView(medication);
                fragmentTransaction.replace(R.id.frame_layout, medicationView);
                fragmentTransaction.commit();
            }
        });

//        holder.tvPosition.setText(medication.getDescription());
    }

    @Override
    public int getItemCount() {
        return leaderboardItemList.size();
    }

    public class MedicationAdapterItemHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        FloatingActionButton button;
//        TextView tvDescription;

//        FrameLayout frameLayout;
//        TextView tvPoints;
        public MedicationAdapterItemHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMedicationElementName);
            button = itemView.findViewById(R.id.btnMedicationElementMore);;
//            tvDescription = itemView.findViewById(R.id.tvMedicationViewDescription);
//            frameLayout = itemView.findViewById(R.id.flMedication);
//            tvPosition = itemView.findViewById(R.id.tvLeadeboardTop);
        }
    }
}
