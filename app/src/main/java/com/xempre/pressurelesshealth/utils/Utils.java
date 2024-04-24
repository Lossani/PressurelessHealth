package com.xempre.pressurelesshealth.utils;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.interfaces.UserService;
import com.xempre.pressurelesshealth.models.IntentExtra;
import com.xempre.pressurelesshealth.models.MedicationFrequency;
import com.xempre.pressurelesshealth.models.Reminder;
import com.xempre.pressurelesshealth.utils.notifications.NotificationGenerator;
import com.xempre.pressurelesshealth.views.MainActivityView;
import com.xempre.pressurelesshealth.views.medication.frequency.AddMedicationFrequency;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class Utils {

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

                            NotificationGenerator notificationGenerator = new NotificationGenerator(context.getSystemService(NotificationManager.class));
                            if (activate) {


                                boolean[] checkedItems = reminder.getMedicationFrequency().getDaysArray();

                                for (int i = 0; i < checkedItems.length; i++){
                                    if (checkedItems[i]) generateAlert(i, reminder, notificationGenerator, context);
                                }
                                // Integer day = reminder.getMedicationFrequency().getWeekday();
                                // Calendar Sunday es 1, el API trabaja con lunes = 1 hasta domingo = 7


                            } else {
                                notificationGenerator.disableNotification(context, reminder.getId());
                            }
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

    public static void generateAlert(Integer day, Reminder reminder, NotificationGenerator notificationGenerator, Context context){
        Calendar calendar = Calendar.getInstance();
        String[] time = reminder.getMedicationFrequency().getHour().split(":");


        if (day < 7)
            day += 1;
        else
            day = 1;

        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar now = Calendar.getInstance();

        if (now.compareTo(calendar) > 0)
            calendar.add(Calendar.DAY_OF_YEAR, 7);


        IntentExtra[] extras = new IntentExtra[] {new IntentExtra("identifier", reminder.getId() + "-" + day), new IntentExtra("scheduledTime", calendar.getTimeInMillis())};
        String content = reminder.getMedicationFrequency().getMedication().getName() + " " + reminder.getMedicationFrequency().getDose() + " - " + reminder.getMedicationFrequency().getHour();

        notificationGenerator.scheduleNotification(context, calendar, reminder.getId() + "-" + day, "Hora de su medicación", content, extras);

    }

    public static void requestAlarmPermission(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Permiso para notificaciones");
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
}
