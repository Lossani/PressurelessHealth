package com.xempre.pressurelesshealth.views.profile.challenge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.interfaces.ChallengeService;
import com.xempre.pressurelesshealth.interfaces.GoalService;
import com.xempre.pressurelesshealth.interfaces.MeasurementService;
import com.xempre.pressurelesshealth.models.Challenge;
import com.xempre.pressurelesshealth.models.ChallengeHistory;
import com.xempre.pressurelesshealth.models.Goal;
import com.xempre.pressurelesshealth.models.GoalHistory;
import com.xempre.pressurelesshealth.models.Measurement;
import com.xempre.pressurelesshealth.views.profile.goal.GoalAdapter;
import com.xempre.pressurelesshealth.views.reports.MeasurementList.MeasurementList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.NombreViewHolder>{
    private Context context;
    private List<Challenge> listMeasurements;

    private Handler handler = new Handler(Looper.getMainLooper());

    public ChallengeAdapter(Context context, List<Challenge> listMeasurements) {
        this.context = context;
        this.listMeasurements = listMeasurements;
    }



    @Override
    public ChallengeAdapter.NombreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.challenge_element, parent, false);
        return new ChallengeAdapter.NombreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChallengeAdapter.NombreViewHolder holder, int position) {
        Challenge challenge = listMeasurements.get(position);
        Log.d("DATA", challenge.toString());
        holder.textViewDesc.setText(String.valueOf(challenge.getDescription()));
        holder.textViewName.setText(String.valueOf(challenge.getName()));
        holder.textViewReward.setText(String.valueOf(challenge.getReward()));

        if (!challenge.isRepeatable()){
            ViewGroup.LayoutParams layoutParams = holder.button.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.button.setLayoutParams(layoutParams);
        }

        if (challenge.getLatestHistory().length > 0){
            ViewGroup.LayoutParams layoutParams = holder.button.getLayoutParams();
            layoutParams.height = 0;
            holder.button.setLayoutParams(layoutParams);
            try {
                holder.checkBox.setChecked((int) (challenge.getLatestHistory()[0].getProgress()) >= 100);
                holder.progressBar.setProgress((int) (challenge.getLatestHistory()[0].getProgress()));
            } catch (Exception ignore){}
        }

        if (challenge.getLatestHistory()[0].getProgress()>=100 || challenge.getLatestHistory()[0].getIsSucceeded()){
            ViewGroup.LayoutParams layoutParams = holder.button.getLayoutParams();
            holder.button.setText("Reiniciar");
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.button.setLayoutParams(layoutParams);
        }


        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveButton(holder, challenge);
                } catch (Exception ignored){
                    Toast.makeText(context, "Asegurece de ingresar números validos.", Toast.LENGTH_SHORT).show();
                }


            }
        });

//        holder.textViewReward.setText(String.valueOf(challenge.getTimeLimit()));
//        validateComplete(challenge, holder);
        getBitmapFromURL(holder, challenge.getImage());
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
        ProgressBar progressBar;
        CheckBox checkBox;

        Button button;

        public NombreViewHolder(View itemView) {
            super(itemView);
            textViewDesc = itemView.findViewById(R.id.tvChallengeDesc);
            textViewName = itemView.findViewById(R.id.tvChallengeName);
            textViewReward = itemView.findViewById(R.id.tvChallengePoints);
            imageView = itemView.findViewById(R.id.imageViewChallenge);
            progressBar= itemView.findViewById(R.id.pbChallenge);
            checkBox = itemView.findViewById(R.id.cbChallengeComplete);
            button = itemView.findViewById(R.id.btnStartChallenge);
        }
    }

//    public boolean validateComplete(Challenge challenge, ChallengeAdapter.NombreViewHolder holder){
//        final boolean[] validate = {false};
//        ChallengeService challengeService = ApiClient.createService(ChallengeService.class);
//
//        Call<List<ChallengeHistory>> call = challengeService.getAllCompleted();
//
//        call.enqueue(new Callback<List<ChallengeHistory>>() {
//            @Override
//            public void onResponse(Call<List<ChallengeHistory>> call, Response<List<ChallengeHistory>> response) {
//                try {
//                    List<ChallengeHistory> responseFromAPI = response.body();
//                    assert responseFromAPI != null;
//
//                    if (responseFromAPI.isEmpty()) {
//                        if (context!=null) Toast.makeText(context, "No se encontraron registros.", Toast.LENGTH_SHORT).show();
//                    } else {
////                        int totalChallenges = 0;
////                        float totalCompleted = 0;
//                        for (ChallengeHistory element : responseFromAPI) {
//                            Log.d("PERRUNO", element.toString());
//                            ChallengeHistory challengeHistory = new ChallengeHistory(element);
//                            Log.d("PERRUNO", element.getId()+"");
//                            Log.d("PERRUNO", element.getProgress()+"");
//                            Log.d("PERRUNO", element.getIsSucceeded()+"");
//
//                            //REMPLAZAR CON ID DE USUARIO
//                            if (Objects.equals(challengeHistory.getChallenge().getId(), challenge.getId())) {
//                                ViewGroup.LayoutParams layoutParams = holder.button.getLayoutParams();
//                                layoutParams.height = 0;
//                                holder.button.setLayoutParams(layoutParams);
//
////                                totalChallenges +=1;
////                                totalCompleted += challengeHistory.getProgress();
////                                Log.d("TOTAL", totalCompleted+"");
////                                Log.d("SUMA", totalChallenges+"");
//                                holder.checkBox.setChecked(challengeHistory.getIsSucceeded() || challengeHistory.getProgress()>=100);
//                                holder.progressBar.setProgress((int)challengeHistory.getProgress());
//                            }
//                        }
////                        holder.checkBox.setChecked((int) (totalCompleted / totalChallenges) >= 100);
////                        holder.progressBar.setProgress((int)(totalCompleted/totalChallenges));
//                    }
//
//                } catch (Exception ignored){
//                    if (context!=null) Toast.makeText(context, "Error al obtener la lista challenge.", Toast.LENGTH_SHORT).show();
//                    Log.d("ERROR-Response", ignored.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<ChallengeHistory>> call, Throwable t) {
//                Log.d("ERROR-Failure", t.getMessage());
//                if (context!=null) Toast.makeText(context, "Error al obtener la lista.", Toast.LENGTH_SHORT).show();
//            }
//        });
//        return validate[0];
//    };

    public void saveButton(ChallengeAdapter.NombreViewHolder holder, Challenge challenge){

        ChallengeService challengeService = ApiClient.createService(ChallengeService.class);
        String pattern = "yyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());


        ChallengeHistory challengeHistory = new ChallengeHistory(2, challenge.getId(), date);

        // calling a method to create a post and passing our modal class.
        Call<ChallengeHistory> call = challengeService.startChallenge(challengeHistory);

        // on below line we are executing our method.
        call.enqueue(new Callback<ChallengeHistory>() {
            @Override
            public void onResponse(Call<ChallengeHistory> call, Response<ChallengeHistory> response) {
                // this method is called when we get response from our api.
                if (response.code() == 201){
                    Toast.makeText(context, "Ha iniciado un nuevo reto.", Toast.LENGTH_SHORT).show();
                    ViewGroup.LayoutParams layoutParams = holder.button.getLayoutParams();
                    layoutParams.height = 0;
                    holder.button.setLayoutParams(layoutParams);
//                    sys.setText("");
//                    dis.setText("");
                } else {
                    Toast.makeText(context, "Ocurrio un error. Error " + response.code(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ChallengeHistory> call, Throwable t) {
                Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show();

                // setting text to our text view when
                // we get error response from API.
//                responseTV.setText("Error found is : " + t.getMessage());
            }
        });
    }

    public void getBitmapFromURL(ChallengeAdapter.NombreViewHolder holder, String src) {

        final Bitmap[] myBitmap = new Bitmap[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Log.e("src",src);
                    URL url = new URL(src);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    myBitmap[0] = BitmapFactory.decodeStream(input);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.imageView.setImageBitmap(myBitmap[0]);
                        }
                    });
                }
                catch (Exception ignored){}

            }
        }).start();
    }
}
