package com.xempre.pressurelesshealth.views.profile.goals;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.models.Goal;
import com.xempre.pressurelesshealth.models.Measurement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.NombreViewHolder> {

    private Context context;
    private List<Goal> listMeasurements;

    public GoalAdapter(Context context, List<Goal> listMeasurements) {
        this.context = context;
        this.listMeasurements = listMeasurements;
    }



    @Override
    public NombreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.goal_element, parent, false);
        return new NombreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NombreViewHolder holder, int position) {
        Goal goal = listMeasurements.get(position);
        Log.d("DATA", goal.toString());
        holder.textViewDesc.setText(String.valueOf(goal.getDescription()));
        holder.textViewName.setText(String.valueOf(goal.getName()));
        holder.textViewReward.setText(String.valueOf(goal.getReward()));
    }

    @Override
    public int getItemCount() {
        return listMeasurements.size();
    }

    public class NombreViewHolder extends RecyclerView.ViewHolder {


        TextView textViewDesc;
        TextView textViewName;

        TextView textViewReward;

        public NombreViewHolder(View itemView) {
            super(itemView);
            textViewDesc = itemView.findViewById(R.id.tvDescGoal);
            textViewName = itemView.findViewById(R.id.tvNameGoal);
            textViewReward = itemView.findViewById(R.id.tvPoints);
        }
    }
}