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
import android.app.Dialog;
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
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.databinding.ActivityMainBinding;
import com.xempre.pressurelesshealth.databinding.ActivityMainViewBinding;
import com.xempre.pressurelesshealth.interfaces.MeasurementService;
import com.xempre.pressurelesshealth.interfaces.UserService;
import com.xempre.pressurelesshealth.models.IntentExtra;
import com.xempre.pressurelesshealth.models.Measurement;
import com.xempre.pressurelesshealth.models.Medication;
import com.xempre.pressurelesshealth.models.User;
import com.xempre.pressurelesshealth.utils.Utils;
import com.xempre.pressurelesshealth.utils.notifications.NotificationGenerator;
import com.xempre.pressurelesshealth.views.add.AddMeasurementBasic;
import com.xempre.pressurelesshealth.views.add.SelectAddMode;
import com.xempre.pressurelesshealth.views.add.advanced.AddMeasurementAdvanced;
import com.xempre.pressurelesshealth.views.medication.MedicationList;
import com.xempre.pressurelesshealth.views.profile.UserProfile;
import com.xempre.pressurelesshealth.views.reports.MeasurementList.MeasurementList;
import com.xempre.pressurelesshealth.views.settings.SettingsFragment;
import com.xempre.pressurelesshealth.utils.Constants;
import com.xempre.pressurelesshealth.views.settings.contacts.ContactList;
import com.xempre.pressurelesshealth.views.shared.ChangeDate;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;
import com.xempre.pressurelesshealth.views.shared.CustomDialog;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityView extends AppCompatActivity {

    ActivityMainViewBinding binding;
    public BottomNavigationView bottomNavigationView;

    public NotificationManager notificationManager;

    public AlarmManager alarmManager;

    private GoogleFitApi googleFitApi = null;

    public GoogleFitApi getGoogleFitApi() {
        return googleFitApi;
    }
    public void setGoogleFitApi(GoogleFitApi googleFitApi) { this.googleFitApi = googleFitApi; }

    private SharedPreferences sharedPreferences;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.notificationGenerator = new NotificationGenerator(this.getSystemService(NotificationManager.class));;
        setTheme(R.style.Theme_PressurelessHealth);
        context = this;

        Calendar test = Calendar.getInstance();
        test.add(Calendar.SECOND, 2);

        // notificationGenerator.sendNotification(this.getApplicationContext(), "Test", "Prueba");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        notificationManager = getSystemService(NotificationManager.class);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // IntentExtra[] extras = new IntentExtra[] {new IntentExtra("identifier", 777), new IntentExtra("scheduledTime", test.getTimeInMillis())};

        // NotificationGenerator notificationGenerator = new NotificationGenerator(notificationManager);
        // notificationGenerator.scheduleNotification(this, test, 777, "Hora de su medicación", "Enalapril un pastillon", extras);

        updatePermissions();
        checkNotificationPermissions();

        if (sharedPreferences.getBoolean(Constants.SETTINGS_GOOGLE_AUTH_SIGNED_IN, false)) {
            setGoogleFitApi(new GoogleFitApi(this));
        }

        checkLatestMeasurement();
        // googleFitApi = new GoogleFitApi(this);

        reloadFragment();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                if (currentFragment instanceof UserProfile) {
                    bottomNavigationView.setSelectedItemId(R.id.bb_profile);
                } else if (currentFragment instanceof AddMeasurementBasic || currentFragment instanceof AddMeasurementAdvanced || currentFragment instanceof SelectAddMode) {
                    bottomNavigationView.setSelectedItemId(R.id.bb_add);
                } else if (currentFragment instanceof MeasurementList) {
                    bottomNavigationView.setSelectedItemId(R.id.bb_report);
                } else if (currentFragment instanceof MedicationList) {
                    bottomNavigationView.setSelectedItemId(R.id.bb_medication);
                } else if (currentFragment instanceof SettingsFragment || currentFragment instanceof ContactList) {
                    bottomNavigationView.setSelectedItemId(R.id.bb_config);
                }

            }
        });
    }

    public void checkLatestMeasurement(){
        MeasurementService userService = ApiClient.createService(this, MeasurementService.class,1);

        Call<Measurement> call = userService.getLatest();

        call.enqueue(new Callback<Measurement>() {
            @Override
            public void onResponse(Call<Measurement> call, Response<Measurement> response) {

                if (response.code() == 200 && response.body()!=null){
                    Measurement measurement = response.body();

                    if(measurement.getMeasurementDate()!=null){
                        // Obtener la fecha y hora actual en formato ZonedDateTime
                        ZonedDateTime now = ZonedDateTime.now();

                        // Supongamos que tienes la "medition_date" almacenada en otro ZonedDateTime llamado meditionDate
                        //ZonedDateTime meditionDate = ZonedDateTime.parse(measurement.getMeasurementDate()); // Ejemplo

                        ZonedDateTime meditionDate = ChangeDate.change(measurement.getMeasurementDate());

                        // Calcular la diferencia entre las dos fechas en horas
                        Duration duration = Duration.between(meditionDate, now);
                        long hoursDifference = duration.toHours();

                        // Comprobar si la diferencia es igual o mayor a 12 horas
                        if (hoursDifference >= 12) {
                            CustomDialog dialog = new CustomDialog();
                            dialog.create(context, "Recordatorio",
                                    "Se ha detectado que la última medida registrada fue hace mas de 12 horas." +
                                            "<br>Recuerda que para realizar un seguimiento adecuado es recomendable realizar dos mediciones al día.");

                            Log.d("ERROR HORA","La medición se realizó hace 12 horas o más.");
                        } else {
                            Log.d("ERROR HORA","La medición se realizó hace MENOS 12 horas.");
                        }
                    }





                }



            }

            @Override
            public void onFailure(Call<Measurement> call, Throwable t) {
                //Toast.makeText(getContext(), "ERROR"+t.toString(), Toast.LENGTH_LONG).show();
                Log.d("a",t.getMessage());
            }
        });

    }


    private void checkNotificationPermissions() {
        if ((!notificationManager.areNotificationsEnabled() || !sharedPreferences.getBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION, false)) && !sharedPreferences.getBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION_REJECTED, false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permiso para notificaciones");
            builder.setMessage("Utilizamos las notificaciones para mostrarle alertas y recordatorios, por favor, otórgenos el permiso en el sistema.")
                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ContextCompat.checkSelfPermission(MainActivityView.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MainActivityView.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, Constants.NOTIFICATIONS_PERMISSION_REQUEST_CODE);
                                } else {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION, true);
                                    editor.putBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION_REJECTED, false);
                                    editor.apply();
                                }
                            } else if (!notificationManager.areNotificationsEnabled()) {
                                Toast.makeText(MainActivityView.this, "Active las notificaciones desde la configuración de la app.", Toast.LENGTH_LONG).show();
                                Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .putExtra(Settings.EXTRA_APP_PACKAGE, MainActivityView.this.getPackageName());
                                MainActivityView.this.startActivity(settingsIntent);
                            } else if (notificationManager.areNotificationsEnabled()) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION, true);
                                editor.putBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION_REJECTED, false);
                                editor.apply();

                                /*
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    if (!alarmManager.canScheduleExactAlarms()) {
                                        Toast.makeText(MainActivityView.this, "Active las notificaciones desde la configuración de la app.", Toast.LENGTH_LONG).show();
                                        Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                .putExtra(Settings.EXTRA_APP_PACKAGE, MainActivityView.this.getPackageName());
                                        MainActivityView.this.startActivity(settingsIntent);
                                    }
                                }
                                */
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
                                    editor.putBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION, false);
                                    editor.putBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION_REJECTED, true);
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
        } else if (notificationManager.areNotificationsEnabled() && !sharedPreferences.getBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION_REJECTED, false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION, true);
            editor.putBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION_REJECTED, false);
            editor.apply();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms() && !sharedPreferences.getBoolean(Constants.SETTINGS_ALARM_PERMISSION, false) && !sharedPreferences.getBoolean(Constants.SETTINGS_ALARM_PERMISSION_REJECTED, false)) {
                    Utils.requestAlarmPermission(this);
                } else if (alarmManager.canScheduleExactAlarms() && !sharedPreferences.getBoolean(Constants.SETTINGS_ALARM_PERMISSION_REJECTED, false)) {
                    editor.putBoolean(Constants.SETTINGS_ALARM_PERMISSION, true);
                    editor.putBoolean(Constants.SETTINGS_ALARM_PERMISSION_REJECTED, false);
                    editor.apply();
                } else if (!alarmManager.canScheduleExactAlarms()) {
                    editor.putBoolean(Constants.SETTINGS_ALARM_PERMISSION, false);
                    editor.apply();
                }
            }
        }
    }

    private void reloadFragment(){
        binding = ActivityMainViewBinding.inflate(getLayoutInflater());
        bottomNavigationView = binding.bottomNavigationMain;
        setContentView(binding.getRoot());
        ChangeFragment.change(this,R.id.frame_layout,new UserProfile());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            MenuItem current = bottomNavigationView.getMenu().findItem(bottomNavigationView.getSelectedItemId());

            if (current == item)
                return true;

            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

            if (item.getItemId() == R.id.bb_add && !(currentFragment instanceof SelectAddMode)){
                ChangeFragment.change(this,R.id.frame_layout, new SelectAddMode());
            } else if (item.getItemId() == R.id.bb_report && !(currentFragment instanceof MeasurementList)) {
                ChangeFragment.change(this,R.id.frame_layout, new MeasurementList());
            } else if (item.getItemId() == R.id.bb_profile && !(currentFragment instanceof UserProfile)) {
                ChangeFragment.change(this,R.id.frame_layout, new UserProfile());
            } else if (item.getItemId() == R.id.bb_config && !(currentFragment instanceof SettingsFragment || currentFragment instanceof ContactList)) {
                updatePermissions();
                ChangeFragment.change(this,R.id.frame_layout, new SettingsFragment());
            } else if (item.getItemId() == R.id.bb_medication && !(currentFragment instanceof MedicationList)) {
                ChangeFragment.change(this,R.id.frame_layout, new MedicationList());
            }
            return true;
        });
    }

//    private void replaceFragment(Fragment fragment){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout, fragment);
//        fragmentTransaction.commit();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.GOOGLE_AUTH_REQUEST_CODE) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            boolean activationResult = googleFitApi.onActivityResult(data);
            editor.putBoolean(Constants.SETTINGS_GOOGLE_AUTH_SIGNED_IN, activationResult);
            editor.apply();

            if (activationResult) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

                if (currentFragment instanceof SettingsFragment) {
                    getSupportFragmentManager().popBackStackImmediate();
                    bottomNavigationView.setSelectedItemId(R.id.bb_config);
                }
            }
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

            Utils.requestAlarmPermission(this);
        }
        if (requestCode == Constants.CALLS_PERMISSION_REQUEST_CODE) {
            editor.putBoolean(Constants.SETTINGS_CALL_PERMISSION, grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED);
            editor.putBoolean(Constants.SETTINGS_CALL_PERMISSION_REJECTED, grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_DENIED);
        }

        editor.apply(); // or editor.commit();
    }

    public void updatePermissions() {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (notificationManager.areNotificationsEnabled() && !sharedPreferences.getBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION_REJECTED, false)) {
                editor.putBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION, true);
                editor.putBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION_REJECTED, false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms() && !sharedPreferences.getBoolean(Constants.SETTINGS_ALARM_PERMISSION_REJECTED, false)) {
                        editor.putBoolean(Constants.SETTINGS_ALARM_PERMISSION, true);
                        editor.putBoolean(Constants.SETTINGS_ALARM_PERMISSION_REJECTED, false);
                    } else if (!alarmManager.canScheduleExactAlarms()) {
                        editor.putBoolean(Constants.SETTINGS_ALARM_PERMISSION, false);
                    }
                }
            } else {
                editor.putBoolean(Constants.SETTINGS_NOTIFICATION_PERMISSION, false);
            }
            editor.apply();

        } catch (Exception ex) {
            Log.d("Apulso MainActivityView OnResume:", String.valueOf(ex.getMessage()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePermissions();

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

            if (!(currentFragment instanceof UserProfile))
                getSupportFragmentManager().popBackStackImmediate();
            else
                finish();
        } else {
            super.onBackPressed();
        }
    }
}