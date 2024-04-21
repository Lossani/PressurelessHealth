package com.xempre.pressurelesshealth.views;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.databinding.ActivityMainBinding;
import com.xempre.pressurelesshealth.databinding.ActivityMainViewBinding;
import com.xempre.pressurelesshealth.utils.notifications.NotificationGenerator;
import com.xempre.pressurelesshealth.views.add.SelectAddMode;
import com.xempre.pressurelesshealth.views.medication.MedicationList;
import com.xempre.pressurelesshealth.views.profile.UserProfile;
import com.xempre.pressurelesshealth.views.reports.MeasurementList.MeasurementList;
import com.xempre.pressurelesshealth.views.settings.SettingsFragment;
import com.xempre.pressurelesshealth.utils.Constants;

public class MainActivityView extends AppCompatActivity {

    ActivityMainViewBinding binding;
    BottomNavigationView bottomNavigationView;

    public NotificationManager notificationManager;

    public AlarmManager alarmManager;

    private GoogleFitApi googleFitApi = null;

    public GoogleFitApi getGoogleFitApi() {
        return googleFitApi;
    }
    public void setGoogleFitApi(GoogleFitApi googleFitApi) { this.googleFitApi = googleFitApi; }

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_PressurelessHealth);
        // NotificationGenerator notificationGenerator = new NotificationGenerator(notificationManager);
        // notificationGenerator.scheduleNotification((AlarmManager) getSystemService(Context.ALARM_SERVICE), this);
        // notificationGenerator.sendNotification(this.getApplicationContext(), "Test", "Prueba");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        notificationManager = getSystemService(NotificationManager.class);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        checkNotificationPermissions();

        if (sharedPreferences.getBoolean("syncGoogleFit", false)) {
            setGoogleFitApi(new GoogleFitApi(this));
        }
        // googleFitApi = new GoogleFitApi(this);

        reloadFragment();

    }


    private void checkNotificationPermissions() {
        if ((!notificationManager.areNotificationsEnabled() || !sharedPreferences.getBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION, false)) && !sharedPreferences.getBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION_REJECTED, false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permiso para notificaciones");
            builder.setMessage("Utilizamos las notificaciones para mostrarle alertas y recordatorios, por favor, otórgenos el permiso en el sistema.")
                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= 33) {
                                if (ContextCompat.checkSelfPermission(MainActivityView.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MainActivityView.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, Constants.NOTIFICATIONS_PERMISSION_REQUEST_CODE);
                                }
                            } else if (!notificationManager.areNotificationsEnabled()) {
                                Toast.makeText(MainActivityView.this, "Active las notificaciones desde la configuración de la app.", Toast.LENGTH_LONG).show();
                                Intent settingsIntent = null;
                                settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .putExtra(Settings.EXTRA_APP_PACKAGE, MainActivityView.this.getPackageName());
                                MainActivityView.this.startActivity(settingsIntent);
                            } else if (notificationManager.areNotificationsEnabled()) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("NOTIFICATION_PERMISSION", true);
                                editor.putBoolean("NOTIFICATION_PERMISSION_REJECTED", false);
                                editor.apply();
                            }
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builderMsgReject = new AlertDialog.Builder(MainActivityView.this);
                            builderMsgReject.setTitle("Permiso de notificaciones denegado");
                            builderMsgReject.setMessage("No podremos enviarle notificaciones esenciales para su cuidado. Si cambia de opinión, activelo desde la configuración del sistema o desde el panel de configuración de la app.");
                            builderMsgReject.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    // If request is cancelled, the result arrays are empty
                                    // Permission denied
                                    // You can handle this situation, e.g., show an explanation to the user
                                    editor.putBoolean("NOTIFICATION_PERMISSION", false);
                                    editor.putBoolean("NOTIFICATION_PERMISSION_REJECTED", true);
                                    editor.apply(); // or editor.commit();
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog dialogReject = builderMsgReject.create();
                            dialogReject.show();
                        }
                    });
            // Crear y mostrar el diálogo
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (notificationManager.areNotificationsEnabled()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("NOTIFICATION_PERMISSION", true);
            editor.putBoolean("NOTIFICATION_PERMISSION_REJECTED", false);
            editor.apply();
        }

    }
    private void reloadFragment(){
        binding = ActivityMainViewBinding.inflate(getLayoutInflater());
        bottomNavigationView = binding.bottomNavigationMain;
        setContentView(binding.getRoot());
        replaceFragment(new UserProfile());
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bb_add){
                replaceFragment(new SelectAddMode());
            } else if (item.getItemId() == R.id.bb_report) {
                replaceFragment(new MeasurementList());
            } else if (item.getItemId() == R.id.bb_profile) {
                replaceFragment(new UserProfile());
            } else if (item.getItemId() == R.id.bb_config) {
                replaceFragment(new SettingsFragment());
            } else if (item.getItemId() == R.id.bb_medication) {
                replaceFragment(new MedicationList());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.GOOGLE_AUTH_REQUEST_CODE) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.SETTINGS_GOOGLE_AUTH_SIGNED_IN, googleFitApi.onActivityResult(data));
            editor.apply();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (requestCode == Constants.NOTIFICATIONS_PERMISSION_REQUEST_CODE) {

            // If request is cancelled, the result arrays are empty
            // Permission denied
            // You can handle this situation, e.g., show an explanation to the user
            editor.putBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION, grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED);
            editor.putBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION_REJECTED, grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_DENIED);
        }
        if (requestCode == Constants.CALLS_PERMISSION_REQUEST_CODE) {
            editor.putBoolean(Constants.SETTINGS_CALL_PERMISSION, grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED);
            editor.putBoolean(Constants.SETTINGS_CALL_PERMISSION_REJECTED, grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_DENIED);
        }

        editor.apply(); // or editor.commit();
    }
}