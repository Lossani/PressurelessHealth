package com.xempre.pressurelesshealth.views.profile.goals;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.models.Goal;
import com.xempre.pressurelesshealth.models.Measurement;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.NombreViewHolder> {

    private Context context;
    private List<Goal> listMeasurements;

    private Handler handler = new Handler(Looper.getMainLooper());

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
        getBitmapFromURL(holder, goal.getImage());
    }

    @Override
    public int getItemCount() {
        return listMeasurements.size();
    }

    public class NombreViewHolder extends RecyclerView.ViewHolder {


        TextView textViewDesc;
        TextView textViewName;

        TextView textViewReward;
        ImageView imageView;

        public NombreViewHolder(View itemView) {
            super(itemView);
            textViewDesc = itemView.findViewById(R.id.tvDescGoal);
            textViewName = itemView.findViewById(R.id.tvNameGoal);
            textViewReward = itemView.findViewById(R.id.tvPoints);
            imageView = itemView.findViewById(R.id.imageView2);
        }
    }

    public void getBitmapFromURL(NombreViewHolder holder, String src) {

        // Ejecutar operaciones de red en un hilo separado usando Runnable
        final Bitmap[] myBitmap = new Bitmap[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Coloca aquí tu código de operaciones de red

                // Ejemplo:
                // HttpURLConnection connection = ...
                // Realizar operaciones de red...
                try{
                    Log.e("src",src);
                    URL url = new URL(src);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    myBitmap[0] = BitmapFactory.decodeStream(input);}
                catch (Exception e){}
                // Si necesitas actualizar la interfaz de usuario, usa el Handler
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Actualizar la interfaz de usuario si es necesario
                        holder.imageView.setImageBitmap(myBitmap[0]);
                    }
                });
            }
        }).start();
    }
}