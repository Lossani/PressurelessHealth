package com.xempre.pressurelesshealth.views.reports.MeasurementList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.models.Measurement;

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
//        holder.textViewDate.setText(measurement.getDate());
        holder.textViewDis.setText(String.valueOf(measurement.getDiastolicRecord()));
        holder.textViewSys.setText(String.valueOf(measurement.getSystolicRecord()));
    }

    @Override
    public int getItemCount() {
        return listMeasurements.size();
    }

    public class NombreViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDis;
        TextView textViewSys;

        TextView textViewDate;

        public NombreViewHolder(View itemView) {
            super(itemView);
            textViewDis = itemView.findViewById(R.id.textView6);
            textViewSys = itemView.findViewById(R.id.textView7);
            textViewDate = itemView.findViewById(R.id.tvDate);
        }
    }
}