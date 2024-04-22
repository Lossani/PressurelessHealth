package com.xempre.pressurelesshealth.views.settings.contacts;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.interfaces.ContactService;
import com.xempre.pressurelesshealth.interfaces.MedicationService;
import com.xempre.pressurelesshealth.models.Contact;
import com.xempre.pressurelesshealth.models.Medication;
import com.xempre.pressurelesshealth.models.MedicationFrequency;
import com.xempre.pressurelesshealth.utils.Constants;
import com.xempre.pressurelesshealth.views.medication.MedicationList;
import com.xempre.pressurelesshealth.views.medication.MedicationView;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactAdapterItemHolder> {

    private Context context;
    private List<Contact> contactList;
    MedicationList medicationList = new MedicationList();
    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }



    @Override
    public ContactAdapterItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_element, parent, false);
        return new ContactAdapter.ContactAdapterItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ContactAdapterItemHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.tvName.setText(contact.getFirstName());
        holder.tvPhone.setText(contact.getPhone());

        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED && !sharedPreferences.getBoolean(Constants.SETTINGS_CALL_PERMISSION_REJECTED, false)) {
                    // Si no tienes permiso, solicítalo
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, Constants.CALLS_PERMISSION_REQUEST_CODE);
                } else if (sharedPreferences.getBoolean(Constants.SETTINGS_CALL_PERMISSION_REJECTED, false)) {
                    // Si NO tiene permiso para llamar directamente, solo se mostrará el número.
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contact.getPhone()));
                    context.startActivity(intent);
                } else {
                    // Si ya se concedió el permiso, realiza la llamada
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + contact.getPhone()));
                    context.startActivity(intent);
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("¿Está seguro de eliminar?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Ejecutar la función de eliminación aquí
                                deleteContact(contact);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Cancelar la eliminación
                                dialog.dismiss();
                            }
                        });
                // Crear y mostrar el diálogo
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    public void deleteContact(Contact contact){

        ContactService contactService = ApiClient.createService(context, ContactService.class,1);

        Contact temp = new Contact();
        temp.setDeleted(true);

        Call<Contact> call = contactService.deleteContact(contact.getId(), temp);
        Log.d("ERROR", String.valueOf(temp.getDeleted()));
        call.enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(context, "Se eliminó el contacto.", Toast.LENGTH_SHORT).show();
                    ChangeFragment.change(context, R.id.frame_layout, new ContactList());
                } else {
                    Toast.makeText(context, "Error al intentar eliminar la frecuencia.", Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ContactAdapterItemHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvPhone;
        FloatingActionButton btnCall;

        FloatingActionButton btnDelete;

        public ContactAdapterItemHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvContactName);
            tvPhone = itemView.findViewById(R.id.tvContactNumber);
            btnCall = itemView.findViewById(R.id.btnContactCall);;
            btnDelete = itemView.findViewById(R.id.btnDeleteContact);
//            btnMedicationElementMoreDelete = itemView.findViewById(R.id.btnMedicationElementMoreDelete);;
        }
    }
}