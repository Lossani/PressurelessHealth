package com.xempre.pressurelesshealth.views.settings;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

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
import com.xempre.pressurelesshealth.utils.Utils;
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
                Utils.updateMedicationFrequencyNotifications(mainActivity, (boolean) newValue);
                Utils.disableScheduled12HourMeasurementReminder(mainActivity);
                return true;
            }
        });

        Preference switchAlarmsEnabled = findPreference(Constants.SETTINGS_ALARM_PERMISSION);

        if (switchAlarmsEnabled != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                PreferenceCategory accountCategory = (PreferenceCategory) findPreference("ACCOUNT");
                if (accountCategory != null) {
                    accountCategory.removePreference(switchAlarmsEnabled);
                }
            }
            switchAlarmsEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if ((boolean) newValue && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        editor.putBoolean(Constants.SETTINGS_ALARM_PERMISSION_REJECTED, false);
                        if (!mainActivity.alarmManager.canScheduleExactAlarms()) {
                            Utils.requestAlarmPermission(mainActivity);
                        } else {
                            editor.putBoolean(Constants.SETTINGS_ALARM_PERMISSION, true);
                        }
                    } else {
                        editor.putBoolean(Constants.SETTINGS_ALARM_PERMISSION, false);
                        editor.putBoolean(Constants.SETTINGS_ALARM_PERMISSION_REJECTED, true);
                    }

                    editor.apply();
                    return true;
                }
            });
        }

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
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("¿Está seguro de cerrar sesión?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", getContext().MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("token");
                                editor.remove("userId");
                                editor.apply();

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                mainActivity.finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Cancelar la eliminación
                                dialog.dismiss();
                            }
                        });
                // Crear y mostrar el diálogo
                AlertDialog dialog = builder.create();
                dialog.show();

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




}