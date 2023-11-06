package com.xempre.pressurelesshealth.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.databinding.ActivityMainBinding;
import com.xempre.pressurelesshealth.databinding.ActivityMainViewBinding;
import com.xempre.pressurelesshealth.views.profile.FirstFragment;
import com.xempre.pressurelesshealth.views.reports.MeasurementList.MeasurementList;

public class MainView extends AppCompatActivity {

    ActivityMainViewBinding binding;
    BottomNavigationView bottomNavigationView;
    Button btnSave;
    private GoogleFitApi googleFitApi;

    EditText sys;
    EditText dis;

    public GoogleFitApi getGoogleFitApi() {
        return googleFitApi;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleFitApi = new GoogleFitApi(this);
        binding = ActivityMainViewBinding.inflate(getLayoutInflater());
        bottomNavigationView = binding.bottomNavigationMain;
        setContentView(binding.getRoot());
        replaceFragment(new FirstFragment());
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bb_add){
                replaceFragment(new AddMeasurement());
            } else if (item.getItemId() == R.id.bb_report) {
                replaceFragment(new MeasurementList());
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