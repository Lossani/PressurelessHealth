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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.MedicationAddBinding;
import com.xempre.pressurelesshealth.databinding.MedicationFrequencyAddBinding;
import com.xempre.pressurelesshealth.interfaces.MedicationService;
import com.xempre.pressurelesshealth.models.IntentExtra;
import com.xempre.pressurelesshealth.models.Medication;
import com.xempre.pressurelesshealth.models.MedicationFrequency;
import com.xempre.pressurelesshealth.utils.notifications.NotificationGenerator;
import com.xempre.pressurelesshealth.views.MainActivityView;
import com.xempre.pressurelesshealth.views.add.SelectAddMode;
import com.xempre.pressurelesshealth.views.medication.MedicationList;
import com.xempre.pressurelesshealth.views.medication.MedicationView;
import com.xempre.pressurelesshealth.views.medication.frequency.CustomSpinnerAdapter;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
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

        // Obtener el array de strings desde strings.xml
        String[] weekdaysArray = getResources().getStringArray(R.array.weekday);

// Convertir el array en una lista
        List<String> weekdaysList = Arrays.asList(weekdaysArray);

// Crear un nuevo ArrayList a partir de la lista
        ArrayList<String> weekdaysArrayList = new ArrayList<>(weekdaysList);

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), weekdaysArrayList);
        binding.spinnerDias.setAdapter(adapter);

        binding.spinnerDias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Al abrir o cerrar la lista desplegable, actualiza los estados de los checkboxes
                adapter.updateCheckedItems(adapter.getCheckedItems());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

//        binding.spinnerDias.setAdapter(new CustomSpinnerAdapter(getContext(),weekdaysArrayList ));

        binding.btnSaveMedicationFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean[] checkedItems = adapter.getCheckedItems();

                boolean selectedDay = false;

                for (boolean checkedItem : checkedItems) {
                    selectedDay = checkedItem;
                    if (selectedDay) break;
                }

                if (!selectedDay) {
                    Toast.makeText(getContext(), "Debe seleccionar almenos un día.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!binding.button4.getText().toString().equals("Seleccionar Hora") && !binding.editTextText3.getText().toString().equals("")){
                    MedicationFrequency medicationFrequency = new MedicationFrequency();
                    medicationFrequency.setMedicationId(medication.getId());
                    medicationFrequency.setDeleted(false);
                    medicationFrequency = convertDays(adapter, medicationFrequency);
//                    medicationFrequency.setWeekday(binding.spinnerDias.getSelectedItemPosition()+1);
                    medicationFrequency.setDose(binding.editTextText3.getText().toString());
                    medicationFrequency.setHour(binding.button4.getText().toString());
                    medicationFrequency.setReminderNotificationEnabled(binding.switchFrequencyNotification.isChecked());
                    Log.d("PERRO", medicationFrequency.getMedicationId()+"");
                    Log.d("PERRO", medicationFrequency.getDose());
                    Log.d("PERRO", medicationFrequency.getHour());
//                    Log.d("PERRO", medicationFrequency.getWeekday()+"");
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

    public MedicationFrequency convertDays(CustomSpinnerAdapter adapter, MedicationFrequency medicationFrequency){
        boolean[] checkedItems = adapter.getCheckedItems();

        for (int i = 0; i < checkedItems.length; i++) {
            switch (i) {
                case 0:
                    medicationFrequency.setMonday(checkedItems[0]);
                    break;
                case 1:
                    medicationFrequency.setTuesday(checkedItems[1]);
                    break;
                case 2:
                    medicationFrequency.setWednesday(checkedItems[2]);
                    break;
                case 3:
                    medicationFrequency.setThursday(checkedItems[3]);
                    break;
                case 4:
                    medicationFrequency.setFriday(checkedItems[4]);
                    break;
                case 5:
                    medicationFrequency.setSaturday(checkedItems[5]);
                    break;
                case 6:
                    medicationFrequency.setSunday(checkedItems[6]);
                    break;
                default:
                    System.out.println("Número fuera de rango");
            }
        }

        return medicationFrequency;
    }

    public void popTimePicker(){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                AddMedicationFrequency.this.hour = hourOfDay;
                AddMedicationFrequency.this.minute = minute;
                binding.button4.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Seleccionar Hora");

        timePickerDialog.show();

    }

    public void generateAlert(Integer day, MedicationFrequency responseFromAPI){
        Log.d("GENERANDO ALARMA DIA ", String.valueOf(day));
        MainActivityView mainActivityView = (MainActivityView) getContext();
        Calendar calendar = Calendar.getInstance();
        // Calendar Sunday es 1, el spinner tiene de lunes = 0 hasta domingo = 7
        if (day < 6)
            day += 2;
        else
            day = 1;

        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, AddMedicationFrequency.this.hour);
        calendar.set(Calendar.MINUTE, AddMedicationFrequency.this.minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar now = Calendar.getInstance();

        if (now.compareTo(calendar) > 0)
            calendar.add(Calendar.DAY_OF_YEAR, 7);

        IntentExtra[] extras = new IntentExtra[] { new IntentExtra("identifier", responseFromAPI.getReminder().getId() + "-" + day), new IntentExtra("scheduledTime", calendar.getTimeInMillis())};
        NotificationGenerator notificationGenerator = new NotificationGenerator(mainActivityView.notificationManager);
        String content = responseFromAPI.getMedication().getName() + " " + responseFromAPI.getDose() + " - " + responseFromAPI.getHour();
        notificationGenerator.scheduleNotification(mainActivityView, calendar, responseFromAPI.getReminder().getId() + "-" + day, "Hora de su medicación", content, extras);

    }

    public void callAPI(MedicationFrequency medicationFrequency){

        MedicationService medicationService = ApiClient.createService(getContext(), MedicationService.class,1);

        Call<MedicationFrequency> call = medicationService.saveMedicationFrequency(medicationFrequency);
        Gson gson = new Gson();
        JsonObject temp = gson.toJsonTree(medicationFrequency).getAsJsonObject();
        Log.d("ERRRRRRROR", temp.toString());
        call.enqueue(new Callback<MedicationFrequency>() {
            @Override
            public void onResponse(Call<MedicationFrequency> call, Response<MedicationFrequency> response) {
                try {
                    MedicationFrequency responseFromAPI = response.body();

                    if (response.code()==201) {
                        if (binding.switchFrequencyNotification.isChecked() && responseFromAPI.getReminder() != null) {


                            boolean[] checkedItems = responseFromAPI.getDaysArray();

                            for (int i = 0; i < checkedItems.length; i++){
                                if (checkedItems[i]) generateAlert(i, responseFromAPI);
                            }

//                            Integer day = binding.spinnerDias.getSelectedItemPosition();

                        }

                        Toast.makeText(getContext(), "Medicamento actualizado exitosamente.", Toast.LENGTH_LONG).show();

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
