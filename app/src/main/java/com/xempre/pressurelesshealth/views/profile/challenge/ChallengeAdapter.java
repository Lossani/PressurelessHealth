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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.models.Challenge;
import com.xempre.pressurelesshealth.models.Goal;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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
        View view = LayoutInflater.from(context).inflate(R.layout.goal_element, parent, false);
        return new ChallengeAdapter.NombreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChallengeAdapter.NombreViewHolder holder, int position) {
        Challenge challenge = listMeasurements.get(position);
        Log.d("DATA", challenge.toString());
        holder.textViewDesc.setText(String.valueOf(challenge.getDescription()));
        holder.textViewName.setText(String.valueOf(challenge.getName()));
//        holder.textViewReward.setText(String.valueOf(challenge.getTimeLimit()));
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

        public NombreViewHolder(View itemView) {
            super(itemView);
            textViewDesc = itemView.findViewById(R.id.tvDescGoal);
            textViewName = itemView.findViewById(R.id.tvNameGoal);
            textViewReward = itemView.findViewById(R.id.tvPoints);
            imageView = itemView.findViewById(R.id.imageView2);
        }
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
