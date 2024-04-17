package com.xempre.pressurelesshealth.views.medication.frequency;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.MedicationAddBinding;
import com.xempre.pressurelesshealth.databinding.MedicationFrequencyAddBinding;
import com.xempre.pressurelesshealth.interfaces.MedicationService;
import com.xempre.pressurelesshealth.models.Medication;
import com.xempre.pressurelesshealth.models.MedicationFrequency;
import com.xempre.pressurelesshealth.utils.notifications.NotificationGenerator;
import com.xempre.pressurelesshealth.views.MainActivityView;
import com.xempre.pressurelesshealth.views.add.SelectAddMode;
import com.xempre.pressurelesshealth.views.medication.MedicationList;
import com.xempre.pressurelesshealth.views.medication.MedicationView;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMedicationFrequency extends Fragment {
    private MedicationFrequencyAddBinding binding;

    Medication medication;

    int hour;
    int minute;

    public AddMedicationFrequency(Medication medication){
        this.medication = medication;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = MedicationFrequencyAddBinding.inflate(inflater, container, false);

        binding.btnSaveMedicationFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.button4.getText().toString().equals("Seleccionar Hora") && !binding.editTextText3.getText().toString().equals("")){
                    MedicationFrequency medicationFrequency = new MedicationFrequency();
                    medicationFrequency.setMedicationId(medication.getId());
                    medicationFrequency.setDeleted(false);
                    medicationFrequency.setWeekday(binding.spinnerDias.getSelectedItemPosition()+1);
                    medicationFrequency.setDose(binding.editTextText3.getText().toString());
                    medicationFrequency.setHour(binding.button4.getText().toString());
                    Log.d("PERRO", medicationFrequency.getMedicationId()+"");
                    Log.d("PERRO", medicationFrequency.getDose());
                    Log.d("PERRO", medicationFrequency.getHour());
                    Log.d("PERRO", medicationFrequency.getWeekday()+"");
                    callAPI(medicationFrequency);
                } else {
                    Toast.makeText(getContext(), "La hora y dosis son requeridas.", Toast.LENGTH_SHORT).show();
                }


//                callAPI(medicationFrequency);
            }
        });
        binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTimePicker();
            }
        });

        binding.btnBackAddFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectAddMode = new MedicationView(medication);
                ChangeFragment.change(getContext(), R.id.frame_layout, selectAddMode);
            }
        });


        return binding.getRoot();

    }

    public void popTimePicker(){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                MainActivityView mainActivityView = (MainActivityView) getContext();
                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                NotificationGenerator notificationGenerator = new NotificationGenerator(mainActivityView.notificationManager);
                notificationGenerator.scheduleNotification(mainActivityView.alarmManager, mainActivityView, calendar);
                Toast.makeText(getContext(), "AQUI.", Toast.LENGTH_SHORT).show();
                binding.button4.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Seleccionar Hora");

        timePickerDialog.show();

    }

    public void callAPI(MedicationFrequency medicationFrequency){

        MedicationService medicationService = ApiClient.createService(getContext(), MedicationService.class,1);

        Call<MedicationFrequency> call = medicationService.saveMedicationFrequency(medicationFrequency);

        call.enqueue(new Callback<MedicationFrequency>() {
            @Override
            public void onResponse(Call<MedicationFrequency> call, Response<MedicationFrequency> response) {
                try {
                    MedicationFrequency responseFromAPI = response.body();

                    if (response.code()==201) {
                        Toast.makeText(getContext(), "Medicamento guardado exitosamente.", Toast.LENGTH_SHORT).show();

                        Fragment fragment = new MedicationView(medication);
                        ChangeFragment.change(getContext(), R.id.frame_layout, fragment);
                    } else {
                        Log.d("Message", response.message());
                    }
                } catch (Exception ignored){
                    if (getContext()!=null) Toast.makeText(getContext(), "Error al guardar medicamento.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", ignored.getMessage());
                    onDestroyView();
                }
            }

            @Override
            public void onFailure(Call<MedicationFrequency> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
                if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista.", Toast.LENGTH_SHORT).show();
                onDestroyView();
            }
        });
    }
}
