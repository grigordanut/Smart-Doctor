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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class DoctorRegistration extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    //private FirebaseUser firebaseUser;

    private DatabaseReference databaseReference;

    //Declare variables
    private TextInputEditText docUniqueCode, docFirstName, docLastName, docPhone, docEmailReg, docPassReg, docConfPassReg;
    private TextView tVHospNameDoctorReg;

    private String doc_UniqueCode, doc_FirstName, doc_LastName, doc_Phone, doc_EmailReg, doc_PassReg, doc_ConfPassReg;


    private String docHospitalName = "";
    private String docHospitalKey = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_registration);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        //firebaseUser = firebaseAuth.getCurrentUser();

        //Create Doctors table into database
        databaseReference = FirebaseDatabase.getInstance().getReference("Doctors");

        tVHospNameDoctorReg = findViewById(R.id.tvHospNameDoctorReg);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            docHospitalName = bundle.getString("HOSPName");
            docHospitalKey = bundle.getString("HOSPKey");
        }

        tVHospNameDoctorReg.setText("Add doctor to: " + docHospitalName + " Hospital");

        Button buttonDocLogReg = findViewById(R.id.btnDocLogReg);
        buttonDocLogReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(DoctorRegistration.this, Login.class));
            }
        });

        docUniqueCode = findViewById(R.id.etDocUniqueCode);
        docFirstName = findViewById(R.id.etDocFirstName);
        docLastName = findViewById(R.id.etDocLastName);
        docPhone = findViewById(R.id.etDocPhoneNumber);
        docEmailReg = findViewById(R.id.etDocEmailReg);
        docPassReg = findViewById(R.id.etDocPassReg);
        docConfPassReg = findViewById(R.id.etDocConfPassReg);

        Button buttonDoctorReg = findViewById(R.id.btnDoctorReg);
        buttonDoctorReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerDoctor();
            }
        });
    }

    private void registerDoctor() {

        if (validateDoctorData()) {

            progressDialog.setMessage("Registering of Doctor details!");
            progressDialog.show();

            //create new user into Firebase Database
            firebaseAuth.createUserWithEmailAndPassword(doc_EmailReg, doc_PassReg)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DoctorRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    //validate data in the input fields
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

    private void uploadDoctorData() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        assert firebaseUser != null;
        String doc_Id = firebaseUser.getUid();
        Doctors doctors = new Doctors(doc_UniqueCode, doc_FirstName, doc_LastName, doc_Phone, doc_EmailReg, docHospitalName, docHospitalKey);

        databaseReference.child(doc_Id).setValue(doctors)
                .addOnCompleteListener(DoctorRegistration.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            firebaseUser.sendEmailVerification();

                            Toast.makeText(DoctorRegistration.this, "Doctor Successfully registered. Verification Email sent", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DoctorRegistration.this, MainActivity.class));
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

//    //send email to user to verify if the email is real
//    private void sendEmailVerification() {
//
//        firebaseUser.sendEmailVerification()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//
//                        if (task.isSuccessful()) {
//
//                            Toast.makeText(DoctorRegistration.this, "Doctor Successfully registered. Verification Email sent", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(DoctorRegistration.this, MainActivity.class));
//                            finish();
//
//                        } else {
//                            try {
//                                throw Objects.requireNonNull(task.getException());
//                            } catch (Exception e) {
//                                Toast.makeText(DoctorRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                            }
//                        }
//
//                        progressDialog.dismiss();
//                    }
//                })
//
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(DoctorRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//    }
}