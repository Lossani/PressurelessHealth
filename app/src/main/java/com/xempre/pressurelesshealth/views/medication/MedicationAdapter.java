package com.xempre.pressurelesshealth.views.medication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.interfaces.MedicationService;
import com.xempre.pressurelesshealth.models.LeaderboardItem;
import com.xempre.pressurelesshealth.models.Medication;
import com.xempre.pressurelesshealth.models.MedicationFrequency;
import com.xempre.pressurelesshealth.views.medication.frequency.MedicationFrequencyList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        holder.btnMedicationElementMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment medicationView = new MedicationView(medication);
                fragmentTransaction.replace(R.id.frame_layout, medicationView);
                fragmentTransaction.commit();
            }
        });

        holder.btnMedicationElementMoreDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("¿Está seguro de eliminar?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Ejecutar la función de eliminación aquí
                                deleteMedication(medication);
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

//        holder.tvPosition.setText(medication.getDescription());
    }

    public void deleteMedication(Medication medication){

        MedicationService medicationService = ApiClient.createService(context, MedicationService.class,1);

        Medication temp = new Medication();
        temp.setDeleted(true);

        Call<Medication> call = medicationService.deleteMedication(medication.getId(), temp);
        Log.d("ERROR", String.valueOf(temp.getDeleted()));
        call.enqueue(new Callback<Medication>() {
            @Override
            public void onResponse(Call<Medication> call, Response<Medication> response) {
                if(response.isSuccessful()) {
                        Toast.makeText(context, "Se eliminó la medicación.", Toast.LENGTH_SHORT).show();
                        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment medicationList = new MedicationList();
                        fragmentTransaction.replace(R.id.frame_layout, medicationList);
                        fragmentTransaction.commit();
                } else {
                    Toast.makeText(context, "Error al intentar eliminar la medicación.", Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return leaderboardItemList.size();
    }

    public class MedicationAdapterItemHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        FloatingActionButton btnMedicationElementMore;
        FloatingActionButton btnMedicationElementMoreDelete;
//        TextView tvDescription;

//        FrameLayout frameLayout;
//        TextView tvPoints;
        public MedicationAdapterItemHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMedicationElementName);
            btnMedicationElementMore = itemView.findViewById(R.id.btnMedicationElementMore);;
            btnMedicationElementMoreDelete = itemView.findViewById(R.id.btnMedicationElementMoreDelete);;
//            tvDescription = itemView.findViewById(R.id.tvMedicationViewDescription);
//            frameLayout = itemView.findViewById(R.id.flMedication);
//            tvPosition = itemView.findViewById(R.id.tvLeadeboardTop);
        }
    }
}
