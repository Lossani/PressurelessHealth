package com.xempre.pressurelesshealth.views.reports.MeasurementList;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.models.Measurement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        LocalDateTime fechaHora = LocalDateTime.parse(measurement.getMeasurementDate(), formatter);

        String date = fechaHora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String hour = fechaHora.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        if (measurement.getSystolicRecord() < 120.00 && measurement.getDiastolicRecord() < 80.00) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.Normal));
        } else if (measurement.getSystolicRecord() < 140.00 && measurement.getDiastolicRecord() < 90.00) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.Pre));
        } else if (measurement.getSystolicRecord() < 160.00 && measurement.getDiastolicRecord() < 100.00) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.Hip1));
        } else if (measurement.getSystolicRecord() < 180.00 && measurement.getDiastolicRecord() < 110.00) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.Hip2));
        } else{
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.Crisis));
        }


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

    @Override
    public int getItemCount() {
        return listMeasurementsFilter.size();
    }

    public class MeasurementItemHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textViewDis;
        TextView textViewSys;

        TextView textViewDate;
        TextView textViewHour;

        Button isAdvanced;

        public MeasurementItemHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cvBody);
            textViewDis = itemView.findViewById(R.id.tvDiastolic);
            textViewSys = itemView.findViewById(R.id.tvSystolic);
            textViewDate = itemView.findViewById(R.id.tvDate);
            textViewHour = itemView.findViewById(R.id.tvHour);
            isAdvanced = itemView.findViewById(R.id.btnIsAdvanced);
        }
    }
}