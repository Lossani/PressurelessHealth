package com.xempre.pressurelesshealth.views.medication.frequency;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xempre.pressurelesshealth.R;

import java.util.ArrayList;
import java.util.Arrays;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private ArrayList<String> mItems;
    private boolean[] checkedItems;

    public CustomSpinnerAdapter(Context context, ArrayList<String> items) {
        super(context, 0, items);
        mContext = context;
        mItems = items;
        checkedItems = new boolean[items.size()];
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.custom_spinner_item, parent, false);
        }

        CheckBox checkBox = convertView.findViewById(R.id.cbCustomItem);
        TextView textView = convertView.findViewById(R.id.tvCustomItem);
        checkBox.setVisibility(View.GONE);

        final String item = mItems.get(position);

        checkBox.setChecked(checkedItems[position]);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                checkedItems[position] = cb.isChecked();
            }
        });

        checkBox.setText(item);
        textView.setText("SELECCIONAR");

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.custom_spinner_item, parent, false);
        }

        CheckBox checkBox = convertView.findViewById(R.id.cbCustomItem);
        TextView textView = convertView.findViewById(R.id.tvCustomItem);
        textView.setVisibility(View.GONE);

        final String item = mItems.get(position);

        checkBox.setChecked(checkedItems[position]);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                checkedItems[position] = cb.isChecked();
            }
        });

        checkBox.setText(item);

        return convertView;
    }



    // Método para actualizar el estado de los checkboxes
    public void updateCheckedItems(boolean[] newCheckedItems) {
        if (newCheckedItems.length == checkedItems.length) {
            checkedItems = Arrays.copyOf(newCheckedItems, newCheckedItems.length);
            notifyDataSetChanged();
        }
    }

    public boolean[] getCheckedItems() {
        return checkedItems;
    }
}

