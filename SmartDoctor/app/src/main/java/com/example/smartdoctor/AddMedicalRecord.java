package com.example.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class AddMedicalRecord extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private TextView tVMedRecPatName;
    private AutoCompleteTextView tVPatientGender;

    private EditText medRecPPSNo, medRecAddress, medRecDateBirth;

    private String medRec_Gender, medRec_PPSNo, medRec_Address, medRec_DateBirth;

    String patientNameMedRec = "";
    String patientKeyMedRec = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_record);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Medical Record!");

        progressDialog = new ProgressDialog(this);

        //Create table Medical Record into database
        databaseReference = FirebaseDatabase.getInstance().getReference("Medical Records");

        tVMedRecPatName = findViewById(R.id.tvMedRecPatName);
        tVPatientGender = findViewById(R.id.tvPatientGender);
        String[] gender = getResources().getStringArray(R.array.Gender);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item_gender, gender);
        tVPatientGender.setAdapter(genderAdapter);

        medRecDateBirth = findViewById(R.id.etDateBirthMedRecord);
        medRecPPSNo = findViewById(R.id.etPPSMedRecord);
        medRecAddress = findViewById(R.id.etAddressMedRecord);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            patientNameMedRec = bundle.getString("PATName");
            patientKeyMedRec = bundle.getString("PATKey");
        }

        tVMedRecPatName.setText("Patient: " + patientNameMedRec);

        Button buttonSaveMedRecord = findViewById(R.id.btnSaveMedRecord);
        buttonSaveMedRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMedicalRecordData();
            }
        });
    }

    private void uploadMedicalRecordData() {

        if (validateMedRecordData()) {

            progressDialog.setMessage("The Medical Record is adding!");
            progressDialog.show();

            String record_ID = databaseReference.push().getKey();
            MedicalRecords medRec = new MedicalRecords(medRec_Gender, medRec_DateBirth, medRec_PPSNo, medRec_Address, patientNameMedRec, patientKeyMedRec);

            if (record_ID != null) {
                databaseReference.child(record_ID).setValue(medRec).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(AddMedicalRecord.this, "Medical Record successfully added!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddMedicalRecord.this, DoctorPage.class));
                            finish();

                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (Exception e) {
                                Toast.makeText(AddMedicalRecord.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        progressDialog.dismiss();
                    }
                });
            }
        }
    }

    //validate data in the input fields
    private Boolean validateMedRecordData() {

        boolean result = false;

        medRec_Gender = tVPatientGender.getText().toString();
        medRec_DateBirth = medRecDateBirth.getText().toString().trim();
        medRec_PPSNo = medRecPPSNo.getText().toString().trim();
        medRec_Address = medRecAddress.getText().toString().trim();

        if (TextUtils.isEmpty(medRec_Gender)) {
            alertDialogGender();
        } else if (TextUtils.isEmpty(medRec_DateBirth)) {
            medRecDateBirth.setError("Enter patient's Date of Birth");
            medRecDateBirth.requestFocus();
        } else if (TextUtils.isEmpty(medRec_PPSNo)) {
            medRecPPSNo.setError("Please enter patient's PPS");
            medRecPPSNo.requestFocus();
        } else if (TextUtils.isEmpty(medRec_Address)) {
            medRecAddress.setError("Please enter patient's Address");
            medRecAddress.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    //Notify if the Gender is missing
    private void alertDialogGender() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("Select Gender")
                .setMessage("Please select the Gender!")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}