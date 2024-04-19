package com.xempre.pressurelesshealth.views.settings.contacts;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.FragmentContactAddBinding;

import com.xempre.pressurelesshealth.interfaces.ContactService;

import com.xempre.pressurelesshealth.models.Contact;
import com.xempre.pressurelesshealth.views.settings.SettingsFragment;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ContactAdd extends Fragment {



    private FragmentContactAddBinding binding;

    Contact contact;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        binding = FragmentContactAddBinding.inflate(inflater, container, false);

        binding.btnContactAddSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.etAddContactName.getText().toString().equals("") && !binding.etAddContactNumber.getText().toString().equals("") ){

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref",MODE_PRIVATE);

                    int userId = sharedPreferences.getInt("userId", 0);

                    Contact contact = new Contact();
                    contact.setFirstName(binding.etAddContactName.getText().toString());
                    contact.setPhone(binding.etAddContactNumber.getText().toString());
                    contact.setUserId(userId);
                    callAPI(contact);
                } else {
                    Toast.makeText(getContext(), "Verifique que los campos no estén vacíos.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.btnContactAddBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment.change(getContext(), R.id.frame_layout, new ContactList());
            }
        });

        return binding.getRoot();

    }

    public void callAPI(Contact contact){

        ContactService contactService = ApiClient.createService(getContext(), ContactService.class, 1);

        Call<Contact> call = contactService.save(contact);

        call.enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                try {
                    Contact responseFromAPI = response.body();

                    if (response.code()==201) {
                        Toast.makeText(getContext(), "Contacto guardado exitosamente.", Toast.LENGTH_SHORT).show();

                        ChangeFragment.change(getContext(), R.id.frame_layout, new ContactList());
                    } else {
                        if (getContext()!=null) Toast.makeText(getContext(), "Error al guardar contacto.", Toast.LENGTH_SHORT).show();
                        Log.d("Error-Save", String.valueOf(response.errorBody()));
                        onDestroyView();
                    }
                } catch (Exception ignored){
                    if (getContext()!=null) Toast.makeText(getContext(), "Error al guardar contacto.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR-Backend", ignored.getMessage());
                    onDestroyView();
                }
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
                if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista.", Toast.LENGTH_SHORT).show();
                onDestroyView();
            }
        });
    }
}