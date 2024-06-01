package com.xempre.pressurelesshealth.views.reports.MeasurementList;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.interfaces.MeasurementService;
import com.xempre.pressurelesshealth.models.Measurement;
import com.xempre.pressurelesshealth.views.shared.ChangeDate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementAdapter.MeasurementItemHolder> {

    private Context context;
    private List<Measurement> listMeasurements;
    private List<Measurement> listMeasurementsFilter;

    public MeasurementAdapter(Context context, List<Measurement> listMeasurements) {
        this.context = context;
        this.listMeasurements = listMeasurements;
        this.listMeasurementsFilter = new ArrayList<>(listMeasurements);
    }



    @Override
    public MeasurementItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.measurement_element, parent, false);
        return new MeasurementItemHolder(view);
    }

    @Override
    public void onBindViewHolder(MeasurementItemHolder holder, int position) {
        Measurement measurement = listMeasurementsFilter.get(position);

        ZonedDateTime fechaHoraLocal = ChangeDate.change(measurement.getMeasurementDate());


        String date = fechaHoraLocal.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String hour = fechaHoraLocal.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

//        if (measurement.getSystolicRecord() < 120.00 && measurement.getDiastolicRecord() < 80.00) {
//
//        } else if (measurement.getSystolicRecord() < 140.00 && measurement.getDiastolicRecord() < 90.00) {
//
//        } else if (measurement.getSystolicRecord() < 160.00 && measurement.getDiastolicRecord() < 100.00) {
//
//        } else if (measurement.getSystolicRecord() < 180.00 && measurement.getDiastolicRecord() < 110.00) {
//
//        } else{
//
//        }

        switch (measurement.categorizeBloodPressure()){
            case "NORMAL":
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.Normal));
                holder.btnDelete.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.principal)));
                break;
            case "ELEVATED":
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.Pre));
                holder.btnDelete.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.principal)));
                break;
            case "HYPERTENSION_STAGE_1":
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.Hip1));
                holder.btnDelete.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.principal)));
                break;
            case "HYPERTENSION_STAGE_2":
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.Hip2));
                holder.btnDelete.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.principal)));
                break;
            case "HYPERTENSIVE_CRISIS":
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.Crisis));
                holder.btnDelete.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.principal)));
                break;
            default:
                break;
        }

        if (!measurement.getComments().trim().isEmpty() && measurement.getComments() != null) holder.btnInfo.setVisibility(View.VISIBLE);
        else holder.btnInfo.setVisibility(View.GONE);
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!measurement.getComments().equals("")){
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setTitle("Comentarios");
//                    builder.setMessage(measurement.getComments())
//                            .setPositiveButton("Volver", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    // Ejecutar la función de eliminación aquí
//                                    dialog.dismiss();
//                                }
//                            });
//                    // Crear y mostrar el diálogo
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                } else {
//                    Toast.makeText(context, "Esta medición no tiene comentarios.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        holder.btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("INFO-COMENTARIO", String.valueOf(!measurement.getComments().trim().isEmpty()));
                Log.d("INFO-COMENTARIO", String.valueOf(measurement.getComments() != null));
                if (!measurement.getComments().trim().isEmpty() && measurement.getComments() != null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Comentarios");
                    builder.setMessage(measurement.getComments())
                            .setPositiveButton("Volver", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Ejecutar la función de eliminación aquí
                                    dialog.dismiss();
                                }
                            });
                    // Crear y mostrar el diálogo
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Log.d("INFO-COMENTARIO", measurement.getComments());
                    Toast.makeText(context, "Esta medición no tiene comentarios.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("¿Está seguro de eliminar?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Ejecutar la función de eliminación aquí
                                deleteMeasurement(measurement, position);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Cancelar la eliminación
                                dialog.dismiss();
                                notifyItemChanged(position);
                            }
                        });
                // Crear y mostrar el diálogo
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

        holder.textViewDate.setText(date);
        holder.textViewHour.setText(hour);
        holder.isAdvanced.setText(measurement.getIsAdvanced()?"Avanzada":"Básica");
        Log.d("ADVANCES", String.valueOf(measurement.getIsAdvanced()));

        holder.textViewDis.setText(String.valueOf(measurement.getDiastolicRecord()));
        holder.textViewSys.setText(String.valueOf(measurement.getSystolicRecord()));

    }

    public void updateList(List<Measurement> measurementList) {
        listMeasurementsFilter.clear();
        listMeasurementsFilter.addAll(measurementList);
        notifyDataSetChanged();
    }

    public void deleteItem(int id) {
        listMeasurementsFilter.remove(id);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listMeasurementsFilter.size();
    }

    public Measurement getByPosition(int id) {
        return listMeasurementsFilter.get(id);
    }

    private void deleteMeasurement(Measurement measurement, int position) {
        MeasurementService contactService = ApiClient.createService(context, MeasurementService.class,1);

//        Measurement temp = new Measurement();
//        temp.setDeleted(true);
        measurement.setDeleted(true);

        Call<Measurement> call = contactService.delete(String.valueOf(measurement.getId()), measurement);
        Log.d("ERROR", String.valueOf(measurement.getDeleted()));
        call.enqueue(new Callback<Measurement>() {
            @Override
            public void onResponse(Call<Measurement> call, Response<Measurement> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(context, "Se eliminó la medida.", Toast.LENGTH_SHORT).show();
                    deleteItem(position);
//                    if (getItemCount() == 0) {
//                        binding.tvMessageHistoryList.setVisibility(View.VISIBLE);
//                        binding.tvMessageHistoryList.setText("No se encontraron registros.");
//                    }
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Error al intentar eliminar la medida.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", response.message());
                    Log.d("ERROR", String.valueOf(response.body()));
                }
            }

            @Override
            public void onFailure(Call<Measurement> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
            }
        });
    }

    public class MeasurementItemHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textViewDis;
        TextView textViewSys;

        TextView textViewDate;
        TextView textViewHour;

        Button isAdvanced;

        FloatingActionButton btnInfo;
        FloatingActionButton btnDelete;

        public MeasurementItemHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cvBody);
            textViewDis = itemView.findViewById(R.id.tvDiastolic);
            textViewSys = itemView.findViewById(R.id.tvSystolic);
            textViewDate = itemView.findViewById(R.id.tvDate);
            textViewHour = itemView.findViewById(R.id.tvHour);
            isAdvanced = itemView.findViewById(R.id.btnIsAdvanced);
            btnInfo = itemView.findViewById(R.id.btnInfoMeasurementElement);
            btnDelete = itemView.findViewById(R.id.btnDeleteMeasurementElement);
        }
    }
}