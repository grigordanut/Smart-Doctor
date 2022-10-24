package com.example.danut.smartdoctor;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginBy extends AppCompatActivity {

    private Button buttonEnterDetails, buttonFingerPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by);

        //log in by using log in details
        buttonEnterDetails = (Button)findViewById(R.id.btnEnterDetails);
        buttonEnterDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginBy.this, LogIn.class));
            }
        });

        buttonFingerPrint = (Button)findViewById(R.id.btnScanFinger);
        buttonFingerPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginBy.this, FingerPrintScan.class));
            }
        });
    }
}
