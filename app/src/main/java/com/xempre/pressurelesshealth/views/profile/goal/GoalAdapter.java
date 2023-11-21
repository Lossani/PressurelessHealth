package com.xempre.pressurelesshealth.views.profile.goal;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.interfaces.GoalService;
import com.xempre.pressurelesshealth.models.Goal;
import com.xempre.pressurelesshealth.models.GoalHistory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
//        holder.progressBar.setProgress(10);
        validateComplete(goal, holder);
        getBitmapFromURL(holder, goal.getImage());
    }

    public boolean validateComplete(Goal goal, NombreViewHolder holder){
        final boolean[] validate = {false};
        GoalService goalService = ApiClient.createService(GoalService.class);

        Call<List<GoalHistory>> call = goalService.getAllComplete();

        call.enqueue(new Callback<List<GoalHistory>>() {
            @Override
            public void onResponse(Call<List<GoalHistory>> call, Response<List<GoalHistory>> response) {
                try {
                    List<GoalHistory> responseFromAPI = response.body();
                    assert responseFromAPI != null;

                    if (responseFromAPI.isEmpty()) {
                        if (context!=null) Toast.makeText(context, "No se encontraron registros.", Toast.LENGTH_SHORT).show();
                    } else {
                        for (GoalHistory element : responseFromAPI) {
                            Log.d("PERRUNO", element.toString());
                            GoalHistory goalHistory = new GoalHistory(element);
                            Log.d("PERRUNO", element.getProgress()+"");
                            Log.d("PERRUNO", element.getIsSucceeded()+"");

                            //REMPLAZAR CON ID DE USUARIO
                            if (Objects.equals(goalHistory.getGoal(), goal.getId())) {
                                holder.checkBox.setChecked(!Objects.equals(goalHistory.getReachedOn(), ""));
                                holder.date.setVisibility(View.VISIBLE);
                                holder.date.setText(goalHistory.getReachedOn().substring(0,10));
                            }
                        }
                    }

                } catch (Exception ignored){
                    if (context!=null) Toast.makeText(context, "Error al obtener la lista1.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR-Response", ignored.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<GoalHistory>> call, Throwable t) {
                Log.d("ERROR-Failure", t.getMessage());
                if (context!=null) Toast.makeText(context, "Error al obtener la lista.", Toast.LENGTH_SHORT).show();
            }
        });
        return validate[0];
    };

    @Override
    public int getItemCount() {
        return listMeasurements.size();
    }

    public class NombreViewHolder extends RecyclerView.ViewHolder {


        TextView textViewDesc;
        TextView textViewName;

        TextView textViewReward;
        ImageView imageView;

        CheckBox checkBox;

        TextView date;

        public NombreViewHolder(View itemView) {
            super(itemView);
            textViewDesc = itemView.findViewById(R.id.tvDescGoal);
            textViewName = itemView.findViewById(R.id.tvNameGoal);
            textViewReward = itemView.findViewById(R.id.tvPoints);
            imageView = itemView.findViewById(R.id.imageView2);
            checkBox = itemView.findViewById(R.id.cbGoal);
            date = itemView.findViewById(R.id.tvDateReached);
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