package com.example.danut.smartdoctor;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginBy extends AppCompatActivity {

       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by);

        //log in by using log in details
        Button btn_EnterDetails = findViewById(R.id.btnEnterDetails);
           btn_EnterDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginBy.this, Login.class));
            }
        });

        Button btn_FingerPrint = (Button)findViewById(R.id.btnFingerPrint);
        btn_FingerPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginBy.this, FingerPrintScan.class));
            }
        });
    }
}
