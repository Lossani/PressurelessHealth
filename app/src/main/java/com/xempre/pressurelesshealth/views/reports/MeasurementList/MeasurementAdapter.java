package com.xempre.pressurelesshealth.views.reports.MeasurementList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.models.Measurement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementAdapter.NombreViewHolder> {

    private Context context;
    private List<Measurement> listMeasurements;

    public MeasurementAdapter(Context context, List<Measurement> listMeasurements) {
        this.context = context;
        this.listMeasurements = listMeasurements;
    }



    @Override
    public NombreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.measurement_element, parent, false);
        return new NombreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NombreViewHolder holder, int position) {
        Measurement measurement = listMeasurements.get(position);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        LocalDateTime fechaHora = LocalDateTime.parse(measurement.getMeasurementDate(), formatter);

        String date = fechaHora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String hour = fechaHora.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        // Aquí comparas el valor y estableces el color del fondo según el valor
        if (measurement.getSystolicRecord() < 120.00 && measurement.getDiastolicRecord() < 80.00) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.Normal));
        } else if (measurement.getSystolicRecord() < 140.00 && measurement.getDiastolicRecord() < 90.00) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.Pre));
        } else if (measurement.getSystolicRecord() < 160.00 && measurement.getDiastolicRecord() < 100.00) {
            // Puedes establecer un color predeterminado para otros valores
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.Hip1));
        } else if (measurement.getSystolicRecord() < 180.00 && measurement.getDiastolicRecord() < 110.00) {
            // Puedes establecer un color predeterminado para otros valores
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.Hip2));
        } else{
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.Crisis));
        }


        holder.textViewDate.setText(date);
        holder.textViewHour.setText(hour);

        holder.textViewDis.setText(String.valueOf(measurement.getDiastolicRecord()));
        holder.textViewSys.setText(String.valueOf(measurement.getSystolicRecord()));
    }

    @Override
    public int getItemCount() {
        return listMeasurements.size();
    }

    public class NombreViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textViewDis;
        TextView textViewSys;

        TextView textViewDate;
        TextView textViewHour;

        public NombreViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cvBody);
            textViewDis = itemView.findViewById(R.id.tvDiastolic);
            textViewSys = itemView.findViewById(R.id.tvSystolic);
            textViewDate = itemView.findViewById(R.id.tvDate);
            textViewHour = itemView.findViewById(R.id.tvHour);
        }
    }
}