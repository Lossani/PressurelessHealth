package com.xempre.pressurelesshealth.views;


import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.os.Bundle;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_PressurelessHealth);

        this.notificationManager = getSystemService(NotificationManager.class);
        this.alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        NotificationGenerator notificationGenerator = new NotificationGenerator(notificationManager);

        // notificationGenerator.scheduleNotification((AlarmManager) getSystemService(Context.ALARM_SERVICE), this);
        // notificationGenerator.sendNotification(this.getApplicationContext(), "Test", "Prueba");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean("syncGoogleFit", false)) {
            setGoogleFitApi(new GoogleFitApi(this));
        }
        // googleFitApi = new GoogleFitApi(this);

        reloadFragment();

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

        googleFitApi.onActivityResult(data);

    }
}