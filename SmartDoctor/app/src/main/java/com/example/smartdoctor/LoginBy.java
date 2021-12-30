package com.example.smartdoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LoginBy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by);

        TextView tVPersonalDetails = findViewById(R.id.tvPersonalDetails);
        tVPersonalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginBy.this, Login.class));
            }
        });

        TextView tVFingerPrint = findViewById(R.id.tvFingerPrint);
        tVFingerPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginBy.this, FingerPrintScan.class));
            }
        });

    }
}