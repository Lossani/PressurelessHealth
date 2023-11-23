package com.xempre.pressurelesshealth.views.medication.frequency;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.models.MedicationFrequency;

import java.util.List;

public class MedicationFrequencyAdapter extends RecyclerView.Adapter<MedicationFrequencyAdapter.MedicationFrequencyItemHolder> {

    private Context context;
    private List<MedicationFrequency> leaderboardItemList;
    public MedicationFrequencyAdapter(Context context, List<MedicationFrequency> leaderboardItemList) {
        this.context = context;
        this.leaderboardItemList = leaderboardItemList;
    }



    @Override
    public MedicationFrequencyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.medication_frequency_element, parent, false);
        return new MedicationFrequencyAdapter.MedicationFrequencyItemHolder(view);
    }

    @Override
    public void onBindViewHolder(MedicationFrequencyItemHolder holder, int position) {
        MedicationFrequency medicationFrequency = leaderboardItemList.get(position);
        holder.tvHour.setText(medicationFrequency.getHour());
        holder.tvDose.setText(medicationFrequency.getDose());
        holder.tvDay.setText(numberToDay(medicationFrequency.getWeekday()));
    }

    public static String numberToDay(int dayNumber) {
        String nameDay;

        switch (dayNumber) {
            case 1:
                nameDay = "Lunes";
                break;
            case 2:
                nameDay = "Martes";
                break;
            case 3:
                nameDay = "Miércoles";
                break;
            case 4:
                nameDay = "Jueves";
                break;
            case 5:
                nameDay = "Viernes";
                break;
            case 6:
                nameDay = "Sábado";
                break;
            case 7:
                nameDay = "Domingo";
                break;
            default:
                nameDay = "Número de día inválido";
                break;
        }

        return nameDay;
    }

    @Override
    public int getItemCount() {
        return leaderboardItemList.size();
    }

    public class MedicationFrequencyItemHolder extends RecyclerView.ViewHolder {

        TextView tvDay;
        TextView tvHour;

        TextView tvDose;
        public MedicationFrequencyItemHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvMedicationDay);
            tvDose = itemView.findViewById(R.id.tvMedicationDose);
            tvHour = itemView.findViewById(R.id.tvMedicationHour);
//            frameLayout = itemView.findViewById(R.id.flMedication);
//            tvPosition = itemView.findViewById(R.id.tvLeadeboardTop);
        }
    }
}
