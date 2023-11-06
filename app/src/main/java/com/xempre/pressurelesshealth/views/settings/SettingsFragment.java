package com.xempre.pressurelesshealth.views.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.xempre.pressurelesshealth.MainActivity;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.GoogleFitApi;

public class SettingsFragment extends PreferenceFragmentCompat {

    MainActivity mainActivity;
    GoogleFitApi googleFitApi;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity)getActivity();
        googleFitApi = mainActivity.getGoogleFitApi();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
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
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }


}