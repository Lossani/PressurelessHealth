package com.xempre.pressurelesshealth.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.interfaces.UserService;
import com.xempre.pressurelesshealth.models.IntentExtra;
import com.xempre.pressurelesshealth.models.Reminder;
import com.xempre.pressurelesshealth.utils.notifications.NotificationGenerator;

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
                                Calendar calendar = Calendar.getInstance();
                                String[] time = reminder.getMedicationFrequency().getHour().split(":");

                                Integer day = reminder.getMedicationFrequency().getWeekday();
                                // Calendar Sunday es 1, el API trabaja con lunes = 1 hasta domingo = 7
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


                                IntentExtra[] extras = new IntentExtra[] {new IntentExtra("identifier", reminder.getId()), new IntentExtra("scheduledTime", calendar.getTimeInMillis())};
                                String content = reminder.getMedicationFrequency().getMedication().getName() + " " + reminder.getMedicationFrequency().getDose() + " - " + reminder.getMedicationFrequency().getHour();

                                notificationGenerator.scheduleNotification(context, calendar, reminder.getId(), "Hora de su medicación", content, extras);
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
}
