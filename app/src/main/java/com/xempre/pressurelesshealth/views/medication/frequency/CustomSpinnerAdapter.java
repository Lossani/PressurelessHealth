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

    CheckBox checkBox;

    TextView textView;
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

        checkBox = convertView.findViewById(R.id.cbCustomItem);
        textView = convertView.findViewById(R.id.tvCustomItem);
        checkBox.setVisibility(View.GONE);

        final String item = mItems.get(position);

        checkBox.setChecked(checkedItems[position]);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                checkedItems[position] = cb.isChecked();
                notifyDataSetChanged();
            }
        });

        checkBox.setText(item);

        updateText();
        //textView.setText("SELECCIONAR");



        return convertView;
    }



    public void updateText(){
        String text = "";

        for (int i = 0; i< checkedItems.length;i++){
            if (i== 0 && checkedItems[i]) text += " Lu ";
            if (i== 1 && checkedItems[i]) text += " Ma ";
            if (i== 2 && checkedItems[i]) text += " Mi ";
            if (i== 3 && checkedItems[i]) text += " Ju ";
            if (i== 4 && checkedItems[i]) text += " Vi ";
            if (i== 5 && checkedItems[i]) text += " Sa ";
            if (i== 6 && checkedItems[i]) text += " Do ";
        }

//        textView.invalidate();
//        textView.requestLayout();

        if (text.equals("")) textView.setText("SELECCIONAR");
        else textView.setText(text);

    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.custom_spinner_item, parent, false);
        }

        checkBox = convertView.findViewById(R.id.cbCustomItem);
        textView = convertView.findViewById(R.id.tvCustomItem);
        textView.setVisibility(View.GONE);

        final String item = mItems.get(position);

        checkBox.setChecked(checkedItems[position]);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                checkedItems[position] = cb.isChecked();
                notifyDataSetChanged();
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

