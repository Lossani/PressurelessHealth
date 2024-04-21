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
import com.xempre.pressurelesshealth.utils.Constants;
import com.xempre.pressurelesshealth.views.medication.MedicationList;
import com.xempre.pressurelesshealth.views.medication.MedicationView;

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

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ContactAdapterItemHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvPhone;
        FloatingActionButton btnCall;

        public ContactAdapterItemHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvContactName);
            tvPhone = itemView.findViewById(R.id.tvContactNumber);
            btnCall = itemView.findViewById(R.id.btnContactCall);;
//            btnMedicationElementMoreDelete = itemView.findViewById(R.id.btnMedicationElementMoreDelete);;
        }
    }
}