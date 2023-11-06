package com.xempre.pressurelesshealth;

import static java.text.DateFormat.getTimeInstance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.databinding.ActivityMainBinding;
import com.xempre.pressurelesshealth.views.AddMeasurement;
import com.xempre.pressurelesshealth.views.profile.UserProfile;
import com.xempre.pressurelesshealth.views.reports.MeasurementList.MeasurementList;
import com.xempre.pressurelesshealth.views.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    BottomNavigationView bottomNavigationView;
    Button btnSave;
    private GoogleFitApi googleFitApi = null;

    EditText sys;
    EditText dis;

    public GoogleFitApi getGoogleFitApi() {
        return googleFitApi;
    }
    public void setGoogleFitApi(GoogleFitApi googleFitApi) { this.googleFitApi = googleFitApi; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // googleFitApi = new GoogleFitApi(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        bottomNavigationView = binding.bottomNavigationMain;
        setContentView(binding.getRoot());
        replaceFragment(new UserProfile());
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bb_add){
                replaceFragment(new AddMeasurement());
            } else if (item.getItemId() == R.id.bb_report) {
                replaceFragment(new MeasurementList());
            } else if (item.getItemId() == R.id.bb_profile) {
                replaceFragment(new UserProfile());
            } else if (item.getItemId() == R.id.bb_config) {
                replaceFragment(new SettingsFragment());
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