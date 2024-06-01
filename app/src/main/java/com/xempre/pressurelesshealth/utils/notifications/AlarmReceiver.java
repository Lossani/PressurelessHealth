package com.xempre.pressurelesshealth.utils.notifications;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.xempre.pressurelesshealth.models.IntentExtra;
import com.xempre.pressurelesshealth.views.MainActivityView;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent nextActivity = new Intent(context, MainActivityView.class);

        // Toast.makeText(context, "From alarm receiver", Toast.LENGTH_LONG).show();

        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        NotificationGenerator notificationGenerator = new NotificationGenerator(context.getSystemService(NotificationManager.class));

        String identifier = intent.getStringExtra("identifier");

        try {
            notificationGenerator.sendNotificationWithActionIntent(nextActivity, context, title, content, identifier == null ? 0 : identifier.hashCode());
        } catch (Exception ex) {
            Toast.makeText(context, "Apulso: " + content + ".", Toast.LENGTH_LONG).show();
        }

        if (identifier != null) {
            try {
                Calendar calendar = Calendar.getInstance();
                Long currentScheduledTime = intent.getLongExtra("scheduledTime", calendar.getTimeInMillis());
                Long nextScheduledTime = currentScheduledTime + 604800000;
                calendar.setTimeInMillis(nextScheduledTime); // Siguiente semana.

                IntentExtra[] extras = new IntentExtra[] {new IntentExtra("identifier", identifier), new IntentExtra("scheduledTime", nextScheduledTime)};
                notificationGenerator.scheduleNotification(context, calendar, identifier, title, content, extras);
            } catch (Exception ex) {
                Toast.makeText(context, "Apulso: No se pudo programar la siguiente alarma.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
