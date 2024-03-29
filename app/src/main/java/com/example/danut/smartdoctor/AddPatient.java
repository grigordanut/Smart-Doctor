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
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AddPatient extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private EditText patCardCode, patUniqueCode, patFirstName, patLastName, patEmailReg, patPassReg, patConfPassReg;
    private TextView tVHospNamePatReg, tVDocNamePatReg;

    private String pat_CardCode, pat_UniqueCode, pat_FirstName, pat_LastName, pat_EmailReg, pat_PassReg, pat_ConfPassReg;

    private String patHosp_Name = "";
    private String patHosp_Key = "";

    private String patDoctor_Name = "";
    private String patDoctor_Key = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Patient");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        //Create Patients table into database
        databaseReference = FirebaseDatabase.getInstance().getReference("Patients");

        patCardCode = findViewById(R.id.etPatCardCode);
        patUniqueCode = findViewById(R.id.etPatUniqueCode);
        patFirstName = findViewById(R.id.etPatFirstName);
        patLastName = findViewById(R.id.etPatLastName);
        patEmailReg = findViewById(R.id.etPatEmailReg);
        patPassReg = findViewById(R.id.etPatPassReg);
        patConfPassReg = findViewById(R.id.etPatConfPassReg);

        tVHospNamePatReg = findViewById(R.id.tvHospNamePatReg);
        tVDocNamePatReg = findViewById(R.id.tvDocNamePatReg);

        getIntent().hasExtra("HOSPName");
        patHosp_Name = getIntent().getExtras().getString("HOSPName");

        getIntent().hasExtra("HOSPKey");
        patHosp_Key = getIntent().getExtras().getString("HOSPKey");

        getIntent().hasExtra("DOCName");
        patDoctor_Name = getIntent().getExtras().getString("DOCName");

        getIntent().hasExtra("DOCKey");
        patDoctor_Key = getIntent().getExtras().getString("DOCKey");

        tVHospNamePatReg.setText(patHosp_Name + " Hospital");
        tVDocNamePatReg.setText("Doctor: " + patDoctor_Name);

        Button btn_BackDoctor = findViewById(R.id.btnBackDoctor);
        btn_BackDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddPatient.this, DoctorPage.class));
            }
        });

        Button btn_AddPatient = findViewById(R.id.btnAddPatient);
        btn_AddPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPatient();
            }
        });
    }

    private void registerPatient() {

        if (validatePatientData()) {

            progressDialog.setMessage("Register Patient details");
            progressDialog.show();

            //Create new Patient user into database
            firebaseAuth.createUserWithEmailAndPassword(pat_EmailReg, pat_PassReg).addOnCompleteListener(AddPatient.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        uploadPatientData();

                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(AddPatient.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    progressDialog.dismiss();
                }
            });
        }
    }

    private void uploadPatientData() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            String pat_Id = firebaseUser.getUid();
            Patients pat_Data = new Patients(pat_CardCode, pat_UniqueCode, pat_FirstName, pat_LastName,
                    pat_EmailReg, patHosp_Name, patHosp_Key, patDoctor_Name, patDoctor_Key);

            databaseReference.child(pat_Id).setValue(pat_Data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        firebaseUser.sendEmailVerification();

                        Toast.makeText(AddPatient.this, "Patient successfully registered.\nVerification Email has been sent!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddPatient.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(AddPatient.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

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
            patUniqueCode.setError("Enter Patient's Unique code");
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
            patEmailReg.setError("Enter a valid Email Address");
            patEmailReg.requestFocus();
        } else if (TextUtils.isEmpty(pat_PassReg)) {
            patPassReg.setError("Enter Patient's password");
            patPassReg.requestFocus();
        } else if (pat_PassReg.length() > 0 && pat_PassReg.length() < 6) {
            patPassReg.setError("Password too short, enter minimum 6 character long");
            patPassReg.requestFocus();
        } else if (TextUtils.isEmpty(pat_ConfPassReg)) {
            patConfPassReg.setError("Enter Password Confirmation");
            patConfPassReg.requestFocus();
        } else if (!pat_ConfPassReg.equals(pat_PassReg)) {
            patConfPassReg.setError("The Password does not match");
            patConfPassReg.requestFocus();
        } else {
            result = true;
        }
        return result;
    }
}
