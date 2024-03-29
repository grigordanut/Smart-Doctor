package com.example.danut.smartdoctor;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class DoctorRegistration extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    //Declare variables
    private EditText docUniqueCode, docFirstName, docLastName, docPhone, docEmailReg, docPassReg, docConfPassReg;

    private String doc_UniqueCode, doc_FirstName, doc_LastName, doc_Phone, doc_EmailReg, doc_PassReg, doc_ConfPassReg;

    private TextView tVDoctorRegName;

    private String hospital_Name;
    private String hospital_Key;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_registration);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Doctor Registration");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Doctors");

        docUniqueCode = findViewById(R.id.etDocUniqueCode);
        docFirstName = findViewById(R.id.etDocFirstName);
        docLastName = findViewById(R.id.etDocLastName);
        docPhone = findViewById(R.id.etDocPhoneNumber);
        docEmailReg = findViewById(R.id.etDocEmailReg);
        docPassReg = findViewById(R.id.etDocPassReg);
        docConfPassReg = findViewById(R.id.etDocConfPassReg);

        tVDoctorRegName = findViewById(R.id.tvDoctorRegName);

        Button btn_DocReg = findViewById(R.id.btnDocReg);
        Button btn_DocLog = findViewById(R.id.btnDocLog);

        getIntent().hasExtra("HOSPName");
        hospital_Name = getIntent().getExtras().getString("HOSPName");

        getIntent().hasExtra("HOSPKey");
        hospital_Key = getIntent().getExtras().getString("HOSPKey");

        tVDoctorRegName.setText("Add Doctor to: " + hospital_Name + " hospital.");

        btn_DocLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DoctorRegistration.this, Login.class));
                finish();
            }
        });

        btn_DocReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerDoctor();
            }
        });
    }

    private void registerDoctor() {

        if (validateDoctorData()) {

            progressDialog.setMessage("Registering Doctor details!");
            progressDialog.show();

            //Create new Doctor user into database
            firebaseAuth.createUserWithEmailAndPassword(doc_EmailReg, doc_PassReg).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        uploadDoctorData();

                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(DoctorRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    progressDialog.dismiss();
                }
            });
        }
    }

    private void uploadDoctorData() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            String doc_Id = firebaseUser.getUid();
            Doctors doc_Data = new Doctors(doc_UniqueCode, doc_FirstName, doc_LastName, doc_Phone, doc_EmailReg, hospital_Name, hospital_Key);

            databaseReference.child(doc_Id).setValue(doc_Data).addOnCompleteListener(DoctorRegistration.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        firebaseUser.sendEmailVerification();

                        Toast.makeText(DoctorRegistration.this, "Doctor successfully registered.\nVerification Email has been sent!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DoctorRegistration.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(DoctorRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    progressDialog.dismiss();
                }
            });
        }
    }

    private Boolean validateDoctorData() {

        boolean result = false;

        doc_UniqueCode = Objects.requireNonNull(docUniqueCode.getText()).toString().trim();
        doc_FirstName = Objects.requireNonNull(docFirstName.getText()).toString().trim();
        doc_LastName = Objects.requireNonNull(docLastName.getText()).toString().trim();
        doc_Phone = Objects.requireNonNull(docPhone.getText()).toString().trim();
        doc_EmailReg = Objects.requireNonNull(docEmailReg.getText()).toString().trim();
        doc_PassReg = Objects.requireNonNull(docPassReg.getText()).toString();
        doc_ConfPassReg = Objects.requireNonNull(docConfPassReg.getText()).toString().trim();

        if (TextUtils.isEmpty(doc_UniqueCode)) {
            docUniqueCode.setError("Enter Doctor's Unique Code");
            docUniqueCode.requestFocus();
        } else if (TextUtils.isEmpty(doc_FirstName)) {
            docFirstName.setError("Enter Doctor's First Name");
            docFirstName.requestFocus();
        } else if (TextUtils.isEmpty(doc_LastName)) {
            docLastName.setError("Enter Doctor's Last Name");
            docLastName.requestFocus();
        } else if (TextUtils.isEmpty(doc_Phone)) {
            docPhone.setError("Enter Doctor's Phone Number");
            docPhone.requestFocus();
        } else if (TextUtils.isEmpty(doc_EmailReg)) {
            docEmailReg.setError("Enter Doctor's Email Address");
            docEmailReg.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(doc_EmailReg).matches()) {
            docEmailReg.setError("Enter a valid Email Address");
            docEmailReg.requestFocus();
        } else if (TextUtils.isEmpty(doc_PassReg)) {
            docPassReg.setError("Enter the Password");
            docPassReg.requestFocus();
        } else if (doc_PassReg.length() < 6) {
            docPassReg.setError("Password too short, enter minimum 6 character long");
            docPassReg.requestFocus();
        } else if (TextUtils.isEmpty(doc_ConfPassReg)) {
            docConfPassReg.setError("Enter the Confirm Password");
            docConfPassReg.requestFocus();
        } else if (!doc_ConfPassReg.equals(doc_PassReg)) {
            docConfPassReg.setError("The Confirm Password does not match Password");
            docConfPassReg.requestFocus();
        } else {
            result = true;
        }
        return result;
    }
}
