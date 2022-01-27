package com.example.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PatientRegistration extends AppCompatActivity {

    //Declare variables
    private EditText patCardCode, patUniqueCode, patFirstName, patLastName, patEmailReg, patPassReg, patConfPassReg;
    private TextView tVPatHospNameReg, tVPatDocNameReg;

    private String pat_CardCode, pat_UniqueCode, pat_FirstName, pat_LastName, pat_EmailReg, pat_PassReg, pat_ConfPassReg;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference databaseReference;

    private String patHospName = "";
    private String patHospitalKey = "";

    private String patDoctorName = "";
    private String patDoctorKey = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_registration);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Create Patients table into database
        databaseReference = FirebaseDatabase.getInstance().getReference("Patients");

        tVPatHospNameReg = findViewById(R.id.tvPatHospNameReg);
        tVPatDocNameReg = findViewById(R.id.tvPatDocNameReg);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            patHospName = bundle.getString("HOSPName");
            patHospitalKey = bundle.getString("HOSPKey");

            patDoctorName = bundle.getString("DOCName");
            patDoctorKey = bundle.getString("DOCKey");
        }

        tVPatHospNameReg.setText(patHospName + " Hospital");
        tVPatDocNameReg.setText("Add patient to Dr: " + patDoctorName);

        patCardCode = findViewById(R.id.etPatCardCode);
        patUniqueCode = findViewById(R.id.etPatUniqueCode);
        patFirstName = findViewById(R.id.etPatFirstName);
        patLastName = findViewById(R.id.etPatLastName);
        patEmailReg = findViewById(R.id.etPatEmailReg);
        patPassReg = findViewById(R.id.etPatPassReg);
        patConfPassReg = findViewById(R.id.etPatConfPassReg);

        Button buttonPatReg = findViewById(R.id.btnAddPatient);
        buttonPatReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePatientData()) {

                    progressDialog.setMessage("Register Patient Details");
                    progressDialog.show();

                    //create new patient into FirebaseDatabase
                    firebaseAuth.createUserWithEmailAndPassword(pat_EmailReg, pat_PassReg)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        uploadPatientData();

                                    } else {
                                        try {
                                            throw Objects.requireNonNull(task.getException());
                                        } catch (Exception e) {
                                            Toast.makeText(PatientRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    progressDialog.dismiss();
                                }
                            })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PatientRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    //validate data in the input fields
    private Boolean validatePatientData() {

        boolean result = false;

        pat_CardCode = patCardCode.getText().toString().trim();
        pat_UniqueCode = patUniqueCode.getText().toString().trim();
        pat_FirstName = patFirstName.getText().toString().trim();
        pat_LastName = patLastName.getText().toString().trim();
        pat_EmailReg = patEmailReg.getText().toString().trim();
        pat_PassReg = patPassReg.getText().toString().trim();
        pat_ConfPassReg = patConfPassReg.getText().toString().trim();

        if (TextUtils.isEmpty(pat_UniqueCode)) {
            patUniqueCode.setError("Enter Patients Unique code");
            patUniqueCode.requestFocus();
        } else if (TextUtils.isEmpty(pat_FirstName)) {
            patFirstName.setError("Enter Patient's First Name");
            patFirstName.requestFocus();
        } else if (TextUtils.isEmpty(pat_LastName)) {
            patLastName.setError("Enter Patient's Last Name");
            patLastName.requestFocus();
        } else if (TextUtils.isEmpty(pat_EmailReg)) {
            patEmailReg.setError("Enter Patient's Email Address");
            patEmailReg.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(pat_EmailReg).matches()) {
            Toast.makeText(PatientRegistration.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            patEmailReg.setError("Enter a valid Email Address");
            patEmailReg.requestFocus();
        } else if (TextUtils.isEmpty(pat_PassReg)) {
            patPassReg.setError("Enter Patient's password");
            patPassReg.requestFocus();
        } else if (pat_PassReg.length() > 0 && pat_PassReg.length() < 6) {
            patPassReg.setError("The password is too short, enter minimum 6 character long");
        } else if (TextUtils.isEmpty(pat_ConfPassReg)) {
            patConfPassReg.setError("Enter Password Confirmation");
            patConfPassReg.requestFocus();
        } else if (!pat_ConfPassReg.equals(pat_PassReg)) {
            Toast.makeText(PatientRegistration.this, "Confirm Password does not match Password", Toast.LENGTH_SHORT).show();
            patConfPassReg.setError("The Password does not match");
            patConfPassReg.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    private void uploadPatientData() {

        String pat_Id = firebaseUser.getUid();
        Patients patients = new Patients(pat_CardCode, pat_UniqueCode, pat_FirstName, pat_LastName,
                pat_EmailReg, patHospName, patHospitalKey, patDoctorName, patDoctorKey);

        databaseReference.child(pat_Id).setValue(patients)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            sendEmailVerification();
                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (Exception e) {
                                Toast.makeText(PatientRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PatientRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //send email to user to verify if the email is real
    private void sendEmailVerification() {

        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(PatientRegistration.this, "Patient successfully registered.\nVerification email has been sent", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PatientRegistration.this, Login.class));
                            finish();

                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (Exception e) {
                                Toast.makeText(PatientRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        progressDialog.dismiss();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PatientRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}