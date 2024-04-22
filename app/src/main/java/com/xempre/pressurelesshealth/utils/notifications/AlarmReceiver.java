package com.xempre.pressurelesshealth.utils.notifications;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.xempre.pressurelesshealth.views.MainActivityView;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent nextActivity = new Intent(context, MainActivityView.class);

        // Toast.makeText(context, "From alarm receiver", Toast.LENGTH_LONG).show();

        NotificationGenerator notificationGenerator = new NotificationGenerator(context.getSystemService(NotificationManager.class));

        notificationGenerator.sendNotificationWithActionIntent(nextActivity, context, "Alarma", "Es hora de su medicación.");
    }
}
