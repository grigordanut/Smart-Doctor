package com.example.smartdoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class LoginBy extends AppCompatActivity {

    private Button buttonPersonalDetails, buttonFingerPrint;

    private CheckBox checkBoxPersonalDetails, checkBoxFingerPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by);

        buttonPersonalDetails = findViewById(R.id.btnPersonalDetails);
        buttonFingerPrint = findViewById(R.id.btnFingerPrint);

        checkBoxPersonalDetails = findViewById(R.id.cBoxPersonalDetails);
        checkBoxPersonalDetails.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (checkBoxPersonalDetails.isChecked()) {
                    checkBoxFingerPrint.setChecked(false);
                    buttonPersonalDetails.setEnabled(true);
                    buttonPersonalDetails.setText("Enter");
                    buttonFingerPrint.setText("Disabled");
                    buttonPersonalDetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(LoginBy.this, Login.class));
                        }
                    });
                }
                else{
                    buttonPersonalDetails.setText("Disabled");
                    buttonPersonalDetails.setEnabled(false);
                }
            }
        });

        checkBoxFingerPrint = findViewById(R.id.cBoxFingerPrint);
        checkBoxFingerPrint.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (checkBoxFingerPrint.isChecked()) {
                    checkBoxPersonalDetails.setChecked(false);
                    buttonFingerPrint.setEnabled(true);
                    buttonFingerPrint.setText("Enter");
                    buttonPersonalDetails.setText("Disabled");
                    buttonFingerPrint.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(LoginBy.this, FingerPrintScan.class));
                        }
                    });
                }

                else{
                    buttonFingerPrint.setText("Disabled");
                    buttonFingerPrint.setEnabled(false);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();

        checkBoxPersonalDetails.setChecked(false);
        checkBoxFingerPrint.setChecked(false);

        buttonPersonalDetails.setEnabled(false);
        buttonPersonalDetails.setText("Disabled");

        buttonFingerPrint.setEnabled(false);
        buttonFingerPrint.setText("Disabled");
    }
}