package com.xempre.pressurelesshealth.views.settings.contacts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.ListAddBinding;
import com.xempre.pressurelesshealth.interfaces.ContactService;
import com.xempre.pressurelesshealth.models.Contact;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactList extends Fragment {


    private ListAddBinding binding;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> medicationList = new ArrayList<Contact>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        binding = ListAddBinding.inflate(inflater, container, false);
        binding.textView19.setText("Mis Contactos");

        recyclerView = binding.rvMedicationList;
        contactAdapter = new ContactAdapter(getContext(), medicationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(contactAdapter);

        binding.btnAddListAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment.change(getContext(), R.id.frame_layout, new ContactAdd());
            }
        });

        binding.btnBackList.setVisibility(View.VISIBLE);

        binding.btnBackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment.back(getContext());
            }
        });

        callAPI();


        return binding.getRoot();

    }

    public void callAPI(){

        ContactService contactService = ApiClient.createService(getContext(), ContactService.class,1);

        Call<List<Contact>> call = contactService.getAll();

        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                try {
                    List<Contact> responseFromAPI = response.body();
                    if (responseFromAPI == null) {
                        // Manejar el caso en que la respuesta es nula
                        Log.e("ERROR", "La respuesta del servidor es nula.");
                        return;
                    }
                    if (responseFromAPI.isEmpty()) {
                        if (getContext()!=null) {
                            binding.tvMessageAddList.setVisibility(View.VISIBLE);
                            binding.tvMessageAddList.setText("No se encontraron contactos registrados.");
                            //Toast.makeText(getContext(), "No se encontraron registros.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        binding.tvMessageAddList.setVisibility(View.INVISIBLE);
                        medicationList.clear();
                        //                            Log.d("PERRUNO", element.getName());
                        //                            Log.d("PERRUNO", element.getDescription());
                        //                            LeaderboardItem temp = new LeaderboardItem(element);
                        medicationList.addAll(responseFromAPI);
                        contactAdapter.notifyDataSetChanged();
                    }

                } catch (Exception ignored){
                    if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista de contactos.", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR", ignored.getMessage());
                    onDestroyView();
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
                if (getContext()!=null) Toast.makeText(getContext(), "Error al obtener la lista de contactos.", Toast.LENGTH_SHORT).show();
                onDestroyView();
            }
        });
    }
}