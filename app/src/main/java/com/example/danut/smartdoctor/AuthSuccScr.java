package com.example.danut.smartdoctor;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AuthSuccScr extends AppCompatActivity {

    private Button buttonDoctor, buttonHospital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_succ_scr);

        buttonDoctor = findViewById(R.id.btnCheckCode);
        buttonDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AuthSuccScr.this, CheckUniqueCode.class));
            }
        });

        buttonHospital = findViewById(R.id.btnBackDoctor);
        buttonHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AuthSuccScr.this, HospitalPage.class));
            }
        });
    }
}
