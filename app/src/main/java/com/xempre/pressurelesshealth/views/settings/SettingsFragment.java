package com.xempre.pressurelesshealth.views.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.xempre.pressurelesshealth.MainActivity;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.interfaces.ChallengeService;
import com.xempre.pressurelesshealth.interfaces.MedicationService;
import com.xempre.pressurelesshealth.interfaces.UserService;
import com.xempre.pressurelesshealth.models.IntentExtra;
import com.xempre.pressurelesshealth.models.Measurement;
import com.xempre.pressurelesshealth.models.MedicationFrequency;
import com.xempre.pressurelesshealth.models.Reminder;
import com.xempre.pressurelesshealth.utils.Constants;
import com.xempre.pressurelesshealth.utils.notifications.NotificationGenerator;
import com.xempre.pressurelesshealth.views.MainActivityView;
import com.xempre.pressurelesshealth.views.medication.MedicationView;
import com.xempre.pressurelesshealth.views.medication.frequency.AddMedicationFrequency;
import com.xempre.pressurelesshealth.views.settings.contacts.ContactList;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends PreferenceFragmentCompat {

    MainActivityView mainActivity;
    GoogleFitApi googleFitApi;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivityView)getActivity();
        googleFitApi = mainActivity.getGoogleFitApi();

        Preference switchSyncGoogleFit = findPreference(Constants.SETTINGS_GOOGLE_AUTH_SIGNED_IN);
        switchSyncGoogleFit.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                if ((boolean) newValue) {
                    if (googleFitApi == null) {
                        mainActivity.setGoogleFitApi(new GoogleFitApi(mainActivity));
                    }
                } else {
                    mainActivity.setGoogleFitApi(null);
                }

                return true;
            }
        });

        Preference switchNotificationsEnabled = findPreference(Constants.SETTINGS_NOTIFICATION_PERMISSION);

        switchNotificationsEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                updateMedicationFrequencyNotifications((boolean) newValue);
                return true;
            }
        });

        /*getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String s) {
                boolean syncGoogleFit = sharedPreferences.getBoolean("syncGoogleFit", false);

                if (syncGoogleFit) {
                    if (googleFitApi == null) {
                        mainActivity.setGoogleFitApi(new GoogleFitApi(mainActivity));
                    }
                } else {
                    mainActivity.setGoogleFitApi(null);
                }
            }
        });*/

        Preference buttonCloseSession = findPreference("button_preference_close_session");
        buttonCloseSession.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Realiza alguna acción al hacer clic en el botón

                SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token");
                editor.remove("userId");
                editor.apply();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                return true;
            }
        });

        Preference buttonContacts = findPreference("button_preference_contacts");
        buttonContacts.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Realiza alguna acción al hacer clic en el botón

                ChangeFragment.change(getContext(), R.id.frame_layout, new ContactList());
                return true;
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void updateMedicationFrequencyNotifications(boolean activate){

        UserService userService = ApiClient.createService(getContext(), UserService.class,1);

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

                            NotificationGenerator notificationGenerator = new NotificationGenerator(mainActivity.notificationManager);
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

                                notificationGenerator.scheduleNotification(mainActivity, calendar, reminder.getId(), "Hora de su medicación", content, extras);
                            } else {
                                notificationGenerator.disableNotification(mainActivity.alarmManager, mainActivity, reminder.getId());
                            }
                        }

                        Toast.makeText(getContext(), "Preferencias actualizadas.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("Message", response.message());
                    }
                } catch (Exception ignored){
                    if (getContext()!=null) Toast.makeText(getContext(), "Error al desactivar las notificaciones.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", ignored.getMessage());
                    onDestroyView();
                }
            }

            @Override
            public void onFailure(Call<List<Reminder>> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
                if (getContext()!=null) Toast.makeText(getContext(), "Error al desactivar las notificaciones.", Toast.LENGTH_SHORT).show();
                onDestroyView();
            }
        });
    }


}