package com.xempre.pressurelesshealth.views.shared;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xempre.pressurelesshealth.R;

public class CustomDialog {
    public static void create(Context activity, String title, String content){
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.ok_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvTitle;
        tvTitle = dialog.findViewById(R.id.tvTitleOkDialog);
        tvTitle.setText(title);

        TextView tvContent;
        tvContent = dialog.findViewById(R.id.tvContentOkDialog);
        tvContent.setText(Html.fromHtml(content));


        dialog.findViewById(R.id.btnOkDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
