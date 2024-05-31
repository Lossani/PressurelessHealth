package com.xempre.pressurelesshealth.views.shared;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.views.medication.MedicationList;
import com.xempre.pressurelesshealth.views.profile.UserProfile;

public class ChangeFragment {
    public static void change(Context context, int id, Fragment fragment){
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public static void back(Context context){
        if (((FragmentActivity) context).getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Fragment currentFragment = ((FragmentActivity) context).getSupportFragmentManager().findFragmentById(R.id.frame_layout);

            if (!(currentFragment instanceof UserProfile))
                ((FragmentActivity) context).getSupportFragmentManager().popBackStackImmediate();
            else
                ((FragmentActivity) context).finish();
        }
    }
}
