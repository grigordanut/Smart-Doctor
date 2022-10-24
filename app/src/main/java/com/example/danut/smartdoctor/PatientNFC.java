package com.example.danut.smartdoctor;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class PatientNFC extends AppCompatActivity {

    private TextView textViewPatFName, textViewPatLName, textViewPatDocName, textViewPatHospName;

    private String patient_FirstName = "";
    private String patient_LastName = "";
    private String patient_DocName = "";
    private String patient_HosptName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_nfc);

        textViewPatFName = findViewById(R.id.tvPatNFCFirstName);
        textViewPatLName = findViewById(R.id.tvPatNFCLastName);
        textViewPatDocName = findViewById(R.id.tvPatNFCDocName);
        textViewPatHospName = findViewById(R.id.tvPatNFCHospName);

        getIntent().hasExtra("FIRSTNAME");
        patient_FirstName = getIntent().getExtras().getString("FIRSTNAME");
        textViewPatFName.setText("First Name: "+patient_FirstName);

        getIntent().hasExtra("LASTNAME");
        patient_LastName = getIntent().getExtras().getString("LASTNAME");
        textViewPatLName.setText("Last Name: "+patient_LastName);

        getIntent().hasExtra("DOCTORNAME");
        patient_DocName = getIntent().getExtras().getString("DOCTORNAME");
        textViewPatDocName.setText("Doctor Name: "+patient_DocName);

        getIntent().hasExtra("HOSPNAME");
        patient_HosptName = getIntent().getExtras().getString("HOSPNAME");
        textViewPatHospName.setText("Hospital : "+patient_HosptName);
    }
}
