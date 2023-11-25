package com.xempre.pressurelesshealth.views.leaderboard;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.models.LeaderboardItem;
import com.xempre.pressurelesshealth.models.Measurement;
import com.xempre.pressurelesshealth.views.shared.GetImageByUrl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardItemHolder> {

    private Context context;
    private List<LeaderboardItem> leaderboardItemList;

    private Handler handler = new Handler(Looper.getMainLooper());

    public LeaderboardAdapter(Context context, List<LeaderboardItem> leaderboardItemList) {
        this.context = context;
        this.leaderboardItemList = leaderboardItemList;
    }



    @Override
    public LeaderboardItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_element, parent, false);
        return new LeaderboardItemHolder(view);
    }

    @Override
    public void onBindViewHolder(LeaderboardItemHolder holder, int position) {
        LeaderboardItem leaderboardItem = leaderboardItemList.get(position);
        if (leaderboardItem.getFirstName().equals("") && leaderboardItem.getFirstName().equals("")) holder.tvName.setText(leaderboardItem.getUsername());
        else holder.tvName.setText(leaderboardItem.getFirstName()+" "+leaderboardItem.getLastName());
        holder.tvPosition.setText((position+1)+"");
        holder.tvPoints.setText(leaderboardItem.getPoints().toString());
        GetImageByUrl.getBitmapFromURL(holder, leaderboardItem.getAvatarURL(), handler, holder.imageView);
    }

    @Override
    public int getItemCount() {
        return leaderboardItemList.size();
    }

    public class LeaderboardItemHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvPosition;
        TextView tvPoints;

        ImageView imageView;
        public LeaderboardItemHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvLeaderboardName);
            tvPoints = itemView.findViewById(R.id.tvLeaderboardPoints);
            tvPosition = itemView.findViewById(R.id.tvLeadeboardTop);
            imageView = itemView.findViewById(R.id.ivLeaderboard);
        }
    }
}
