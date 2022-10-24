package com.example.danut.smartdoctor;

import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class AddMedicalRecord extends AppCompatActivity {

    private EditText etMedRecPPSNo, etMedRecAddress, etMedRecDateBirth;

    private RadioGroup radioGroup;
    private RadioButton radioButton, radioButtonMale, radioButtonFemale;
    private TextView textViewMedRec, textViewGenderSelected;

    Button buttonSaveNewRecordLast;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    String patientID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_record);

        getIntent().hasExtra("PATID");
        patientID = getIntent().getExtras().getString("PATID");

        textViewMedRec = (TextView)findViewById(R.id.tvMedRec);
        textViewMedRec.setText("Add Med Record to "+patientID);

        textViewGenderSelected = (TextView)findViewById(R.id.tVGenderSelected);

        radioButtonMale = (RadioButton)findViewById(R.id.radioMale);
        radioButtonFemale = (RadioButton)findViewById(R.id.radioFemale);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroupGender);

        etMedRecDateBirth = (EditText) findViewById(R.id.etDateBirthMedRecord);
        etMedRecPPSNo = (EditText)findViewById(R.id.etPPSMedRecord);
        etMedRecAddress = (EditText)findViewById(R.id.etAddressMedRecord);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Record");

        buttonSaveNewRecordLast = (Button)findViewById(R.id.btnSaveMedRecord);
        buttonSaveNewRecordLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedicalRecord();
            }
        });
    }

    public void saveMedicalRecord(){

        String gender="";
        String medRec_DateBirth = etMedRecDateBirth.getText().toString().trim();
        String medRec_PPSNo =  etMedRecPPSNo.getText().toString().trim();
        String medRec_Address = etMedRecAddress.getText().toString().trim();

        if(radioGroup.getCheckedRadioButtonId()==-1){
            Toast.makeText(AddMedicalRecord.this, "Please select your Gender", Toast.LENGTH_SHORT).show();
            radioGroup.setBackgroundColor(Color.RED);
        }

        else if(medRec_DateBirth.isEmpty()){
            etMedRecDateBirth.setError("Enter patient's Date of Birth");
            etMedRecDateBirth.requestFocus();
        }

        else if (medRec_PPSNo.isEmpty()){
            etMedRecPPSNo.setError("Please enter patient's PPS");
            etMedRecPPSNo.requestFocus();
        }

        else if(medRec_Address.isEmpty()){
            etMedRecAddress.setError("Please enter patient's Address");
            etMedRecAddress.requestFocus();
        }

        else {
            if (radioButtonMale.isChecked()) {
                gender = "Male";
            } else if (radioButtonFemale.isChecked()) {
                gender = "Female";
            }
            String recordID = databaseReference.push().getKey();

            MedicalRecord medRec = new MedicalRecord(gender,medRec_DateBirth, medRec_PPSNo, medRec_Address,patientID);

            databaseReference.child(recordID).setValue(medRec).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        finish();
                        Toast.makeText(AddMedicalRecord.this, "Record Added", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddMedicalRecord.this, DoctorPage.class));
                    }
                }
            })

            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddMedicalRecord.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void checkButton(View v){
        int radioID = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);

        if(radioButtonMale.isChecked()) {
            textViewGenderSelected.setText("Male");
            radioButton.setBackgroundColor(Color.WHITE);
        }

        else if(radioButtonFemale.isChecked()){
            textViewGenderSelected.setText("Female");
            radioButton.setBackgroundColor(Color.WHITE);
        }
    }
}
