package com.example.aimusicplayer;

import android.app.Dialog;
import android.content.Context;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;


public class MyDialog extends Dialog {

    SharedPreferences sharedPreferences;
    public MyDialog(Context context) {
        super(context);

    }

    @Override
    public void show() {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCancelable(false);
        setContentView(R.layout.dialog_layout);
        Button button = findViewById(R.id.buttonOk);
        final CheckBox checkBox = findViewById(R.id.checkBox);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {


                   Container.isChecked = true;



                }
                cancel();

            }
        });
        super.show();
    }

}
