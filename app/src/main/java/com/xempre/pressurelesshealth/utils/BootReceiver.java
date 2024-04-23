package com.xempre.pressurelesshealth.utils;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED && notificationManager.areNotificationsEnabled()) {
                Utils.updateMedicationFrequencyNotifications(context, sharedPreferences.getBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION, true));
            }
            Toast.makeText(context, "Apulso: Configurando notificaciones.", Toast.LENGTH_LONG).show();
        }
    }
}

