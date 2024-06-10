package com.xempre.pressurelesshealth.utils;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.interfaces.UserService;
import com.xempre.pressurelesshealth.models.DebugLog;
import com.xempre.pressurelesshealth.models.IntentExtra;
import com.xempre.pressurelesshealth.models.MedicationFrequency;
import com.xempre.pressurelesshealth.models.Reminder;
import com.xempre.pressurelesshealth.models.User;
import com.xempre.pressurelesshealth.utils.notifications.NotificationGenerator;
import com.xempre.pressurelesshealth.views.MainActivityView;
import com.xempre.pressurelesshealth.views.medication.frequency.AddMedicationFrequency;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class Utils {
    public static NotificationGenerator notificationGenerator = null;

    public static void updateMedicationFrequencyNotifications(Context context, boolean activate){

        UserService userService = ApiClient.createService(context, UserService.class,1);

        Call<List<Reminder>> call = userService.getReminders();

        call.enqueue(new Callback<List<Reminder>>() {
            @Override
            public void onResponse(Call<List<Reminder>> call, Response<List<Reminder>> response) {
                try {
                    List<Reminder> responseFromAPI = response.body();

                    if (response.code() == 200) {
                        for (Reminder reminder : responseFromAPI) {
                            if (!reminder.isActive()) {
                                continue;
                            }

                            updateScheduledMedicationFrequencyAlarms(context, reminder, activate);
                        }

                        Toast.makeText(context, "Preferencias actualizadas.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("Message", response.message());
                    }
                } catch (Exception ignored){
                    if (context != null) Toast.makeText(context, "Error al desactivar las notificaciones.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", ignored.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<Reminder>> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
                if (context != null) Toast.makeText(context, "Error al desactivar las notificaciones.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void scheduleRepeatingAlarm(Context context, int day, String identifier, String title, String content, int hour, int minute) {
        // day: SUNDAY = 1, MONDAY = 2, TUESDAY = 3, WEDNESDAY = 4 ... SATURDAY = 7
        Calendar calendar = getNextCalendar(day, hour, minute);

        IntentExtra[] extras = new IntentExtra[] {new IntentExtra("identifier", identifier), new IntentExtra("scheduledTime", calendar.getTimeInMillis())};

        notificationGenerator.scheduleNotification(context, calendar, identifier, title, content, extras);
    }

    @NonNull
    private static Calendar getNextCalendar(int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar now = Calendar.getInstance();

        if (now.compareTo(calendar) > 0)
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        return calendar;
    }

    public static void updateScheduledMedicationFrequencyAlarms(Context context, Reminder reminder, boolean activate) {
        
        boolean[] checkedItems = reminder.getMedicationFrequency().getDaysArray();
        
        for (int i = 0; i < checkedItems.length; i++){
            if (activate) {
                String[] time = reminder.getMedicationFrequency().getHour().split(":");
                if (checkedItems[i]) {
                    String content = reminder.getMedicationFrequency().getMedication().getName() + " " + reminder.getMedicationFrequency().getDose() + " - " + reminder.getMedicationFrequency().getHour();

                    scheduleRepeatingAlarm(context, i + 1, reminder.getMedicationFrequency().getId() + "-" + (i + 1), "Hora de su medicación", content, Integer.parseInt(time[0]), Integer.parseInt(time[1]));
                }
            } else {
                notificationGenerator.disableNotification(context, reminder.getMedicationFrequency().getId() + "-" + (i + 1));
            }
        }
    }

    public static void updateScheduledMedicationFrequencyAlarms(Context context, MedicationFrequency medicationFrequency, boolean activate) {

        boolean[] checkedItems = medicationFrequency.getDaysArray();

        for (int i = 0; i < checkedItems.length; i++){
            if (activate) {
                String[] time = medicationFrequency.getHour().split(":");
                if (checkedItems[i]) {
                    String content = medicationFrequency.getMedication().getName() + " " + medicationFrequency.getDose() + " - " + medicationFrequency.getHour();

                    scheduleRepeatingAlarm(context, i + 1, medicationFrequency.getId() + "-" + (i + 1), "Hora de su medicación", content, Integer.parseInt(time[0]), Integer.parseInt(time[1]));
                }
            } else {
                notificationGenerator.disableNotification(context, medicationFrequency.getId() + "-" + (i + 1));
            }
        }
    }

    public static void schedule12HourMeasurementReminder(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 12);

        String content = "Recordatorio: Registró su última medición hace más de 12 horas. Recuerde realizar un seguimiento continuo.";

        NotificationGenerator notificationGenerator = new NotificationGenerator(context.getSystemService(NotificationManager.class));
        notificationGenerator.scheduleNotification(context, calendar, String.valueOf(Constants.NOTIFICATIONS_LAST_MEASUREMENT_REMINDER_IDENTIFIER), "12 horas desde su última medición", content, new IntentExtra[0]);
    }

    public static void disableScheduled12HourMeasurementReminder(Context context) {
        NotificationGenerator notificationGenerator = new NotificationGenerator(context.getSystemService(NotificationManager.class));
        notificationGenerator.disableNotification(context, String.valueOf(Constants.NOTIFICATIONS_LAST_MEASUREMENT_REMINDER_IDENTIFIER));
    }


    public static void requestAlarmPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Permiso para alarmas");
            builder.setMessage("Utilizamos el sistema de alarmas y recordatorios para notificarle a la hora exacta de sus medicaciones configuradas, para ello necesitamos que nos otorgue el permiso correspondiente.")
                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                            context.startActivity(settingsIntent);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(Constants.SETTINGS_ALARM_PERMISSION, false);
                            editor.putBoolean(Constants.SETTINGS_ALARM_PERMISSION_REJECTED, true);
                            editor.apply();
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    public static void createDebugLog(Context context, DebugLog log) {
        try {
            UserService userService = ApiClient.createService(context, UserService.class,1);

            Call<DebugLog> call = userService.createDebugLog(log);

            call.enqueue(new Callback<DebugLog>() {

                @Override
                public void onResponse(Call<DebugLog> call, Response<DebugLog> response) {

                }

                @Override
                public void onFailure(Call<DebugLog> call, Throwable t) {

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Apulso", ex.getMessage());
        }
    }

    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
