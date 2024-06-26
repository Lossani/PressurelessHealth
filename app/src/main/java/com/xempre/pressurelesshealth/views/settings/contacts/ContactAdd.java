package com.xempre.pressurelesshealth.views.settings.contacts;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.Spanned;
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

    boolean isEditMode;

    ContactAdd(){
        isEditMode = false;
    }

    ContactAdd(Contact contact){
        isEditMode=true;
        this.contact = contact;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        binding = FragmentContactAddBinding.inflate(inflater, container, false);

        InputFilter letterFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i)) && !Character.isSpaceChar(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        binding.btnContactAddSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.etAddContactName.getText().toString().equals("") && !binding.etAddContactNumber.getText().toString().equals("") ){

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref",MODE_PRIVATE);

                    int userId = sharedPreferences.getInt("userId", 0);

                    Contact contactTemp = new Contact();
                    contactTemp.setFirstName(binding.etAddContactName.getText().toString());
                    contactTemp.setPhone(binding.etAddContactNumber.getText().toString());
                    contactTemp.setUserId(userId);
                    if (!isEditMode)
                    callAPI(contactTemp);
                    else {
                        contactTemp.setId(contact.getId());
                        updateContact(contactTemp);
                    }
                } else {
                    Toast.makeText(getContext(), "Verifique que los campos no estén vacíos.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.btnContactAddBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment.back(getContext());
            }
        });

        binding.etAddContactName.setFilters(new InputFilter[]{letterFilter});

        if (isEditMode){
            binding.etAddContactName.setText(contact.getFirstName());
            binding.etAddContactNumber.setText(contact.getPhone());
            binding.tbAddContactTitle.setText("Editar Contacto");
        }

        return binding.getRoot();

    }

    public void updateContact(Contact contact){

        ContactService contactService = ApiClient.createService(getContext(), ContactService.class,1);

//        Contact temp = new Contact();
//        temp.setDeleted(true);

        Call<Contact> call = contactService.updateContact(contact.getId(), contact);
//        Log.d("ERROR", String.valueOf(temp.getDeleted()));
        call.enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getContext(), "Se modificó el contacto.", Toast.LENGTH_SHORT).show();
                    ChangeFragment.change(getContext(), R.id.frame_layout, new ContactList());
                } else {
                    Toast.makeText(getContext(), "Error al intentar eliminar la frecuencia.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", response.message());
                    Log.d("ERROR", String.valueOf(response.body()));
                }
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
            }
        });
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
                if (getContext()!=null) Toast.makeText(getContext(), "Error al guardar contacto.", Toast.LENGTH_SHORT).show();
                onDestroyView();
            }
        });
    }
}