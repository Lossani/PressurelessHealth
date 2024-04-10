package com.xempre.pressurelesshealth.views.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.xempre.pressurelesshealth.MainActivity;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.views.MainActivityView;

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

        Preference buttonPreference = findPreference("button_preference");
        buttonPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
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

        return super.onCreateView(inflater, container, savedInstanceState);
    }


}