package com.xempre.pressurelesshealth.views.medication.frequency;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.interfaces.MedicationService;
import com.xempre.pressurelesshealth.models.Medication;
import com.xempre.pressurelesshealth.models.MedicationFrequency;
import com.xempre.pressurelesshealth.views.medication.MedicationList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        if (medicationFrequency.getMonday()) holder.chipLu.setChipBackgroundColorResource(R.color.selected_day);
        if (medicationFrequency.getTuesday()) holder.chipMa.setChipBackgroundColorResource(R.color.selected_day);
        if (medicationFrequency.getWednesday()) holder.chipMi.setChipBackgroundColorResource(R.color.selected_day);
        if (medicationFrequency.getThursday()) holder.chipJu.setChipBackgroundColorResource(R.color.selected_day);
        if (medicationFrequency.getFriday()) holder.chipVi.setChipBackgroundColorResource(R.color.selected_day);
        if (medicationFrequency.getSaturday()) holder.chipSa.setChipBackgroundColorResource(R.color.selected_day);
        if (medicationFrequency.getSunday()) holder.chipDo.setChipBackgroundColorResource(R.color.selected_day);

//        holder.tvDay.setText(numberToDay(medicationFrequency.getWeekday()));
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("¿Está seguro de eliminar?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Ejecutar la función de eliminación aquí
                                deleteFrequency(medicationFrequency);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Cancelar la eliminación
                                dialog.dismiss();
                            }
                        });
                // Crear y mostrar el diálogo
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void deleteFrequency(MedicationFrequency frequency){

        MedicationService medicationService = ApiClient.createService(context, MedicationService.class,1);

        MedicationFrequency temp = new MedicationFrequency();
        temp.setDeleted(true);

        Call<MedicationFrequency> call = medicationService.deleteMedicationFrequency(frequency.getId(), temp);
        Log.d("ERROR", String.valueOf(temp.getDeleted()));
        call.enqueue(new Callback<MedicationFrequency>() {
            @Override
            public void onResponse(Call<MedicationFrequency> call, Response<MedicationFrequency> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(context, "Se eliminó la frecuencia.", Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment medicationList = new MedicationList();
                    fragmentTransaction.replace(R.id.frame_layout, medicationList);
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(context, "Error al intentar eliminar la frecuencia.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", response.message());
                    Log.d("ERROR", String.valueOf(response.body()));
                }
            }

            @Override
            public void onFailure(Call<MedicationFrequency> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
            }
        });
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

        Chip chipLu;
        Chip chipMa;
        Chip chipMi;
        Chip chipJu;
        Chip chipVi;
        Chip chipSa;
        Chip chipDo;

        FloatingActionButton btnDelete;
        public MedicationFrequencyItemHolder(View itemView) {
            super(itemView);
//            tvDay = itemView.findViewById(R.id.tvMedicationDay);
            tvDose = itemView.findViewById(R.id.tvMedicationDose);
            tvHour = itemView.findViewById(R.id.tvMedicationHour);
            chipLu = itemView.findViewById(R.id.chipLu);
            chipMa = itemView.findViewById(R.id.chipMa);
            chipMi = itemView.findViewById(R.id.chipMi);
            chipJu = itemView.findViewById(R.id.chipJu);
            chipVi = itemView.findViewById(R.id.chipVi);
            chipSa = itemView.findViewById(R.id.chipSa);
            chipDo = itemView.findViewById(R.id.chipDo);
            btnDelete = itemView.findViewById(R.id.btnDeleteFrequency);

//            frameLayout = itemView.findViewById(R.id.flMedication);
//            tvPosition = itemView.findViewById(R.id.tvLeadeboardTop);
        }
    }
}
