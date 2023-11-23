package com.xempre.pressurelesshealth.views.shared;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.views.medication.MedicationList;

public class ChangeFragment {
    public static void change(Context context,int id, Fragment fragment){
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
    }
}
