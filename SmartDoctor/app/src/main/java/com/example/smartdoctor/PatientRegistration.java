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
    private TextView tVPatDocNameReg, tVPatHospNameReg;

    private Button buttonPatReg, buttonPatCancelReg;
    private String pat_CardCode, pat_UniqueCode, pat_FirstName, pat_LastName, pat_EmailReg, pat_PassReg, pat_ConfPassReg;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ValueEventListener valueEventListener;

    private String patDoctor_Name = "";
    private String patDoctor_key = "";
    private String patHosp_Name= "";
    private String patHospital_Key ="";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_registration);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Patients");

        tVPatDocNameReg = findViewById(R.id.tvPatDocNameReg);
        tVPatHospNameReg = (TextView)findViewById(R.id.tvPatHospNameReg);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            patDoctor_Name = bundle.getString("DOCName");
            patDoctor_key = bundle.getString("DOCKey");
            patHosp_Name = bundle.getString("HOSPName");
            patHospital_Key = bundle.getString("HOSPKey");
        }

        tVPatDocNameReg.setText("Add patient to Dr. " + patDoctor_Name);
        tVPatHospNameReg.setText("Hospital" + patHosp_Name);


        patCardCode = findViewById(R.id.etPatCardCode);
        patUniqueCode = findViewById(R.id.etPatUniqueCode);
        patFirstName = findViewById(R.id.etPatFirstName);
        patLastName = findViewById(R.id.etPatLastName);
        patEmailReg = findViewById(R.id.etPatEmailReg);
        patPassReg = findViewById(R.id.etPatPassReg);
        patConfPassReg = findViewById(R.id.etPatConfPassReg);

        buttonPatReg = (Button)findViewById(R.id.btnAddPatient);
        buttonPatReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePatientData()) {

                    progressDialog.setMessage("Register Doctor Details");
                    progressDialog.show();

                    //create new patient into FirebaseDatabase
                    firebaseAuth.createUserWithEmailAndPassword(pat_EmailReg, pat_PassReg).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                sendEmailVerification();

                                //clear input fields
                                patUniqueCode.setText("");
                                patFirstName.setText("");
                                patLastName.setText("");
                                patEmailReg.setText("");
                                patPassReg.setText("");
                                patConfPassReg.setText("");
                            }

                            else{
                                progressDialog.dismiss();
                                Toast.makeText(PatientRegistration.this, "Registration Failed, this email address was already used to other account",Toast.LENGTH_SHORT).show();
                            }
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
            patUniqueCode.setError("Enter Patient Unique code");
            patUniqueCode.requestFocus();
        }

        else if (TextUtils.isEmpty(pat_FirstName)) {
            patFirstName.setError("Enter Patient's First Name");
            patFirstName.requestFocus();
        }

        else if (TextUtils.isEmpty(pat_LastName)) {
            patLastName.setError("Enter Patient's Last Name");
            patLastName.requestFocus();
        }

        else if (TextUtils.isEmpty(pat_EmailReg)) {
            patEmailReg.setError("Enter Patient's Email Address");
            patEmailReg.requestFocus();
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(pat_EmailReg).matches()) {
            Toast.makeText(PatientRegistration.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            patEmailReg.setError("Enter a valid Email Address");
            patEmailReg.requestFocus();
        }

        else if (TextUtils.isEmpty(pat_PassReg)) {
            patPassReg.setError("Enter Patient's password");
            patPassReg.requestFocus();
        }

        else if (pat_PassReg.length() > 0 && pat_PassReg.length() < 6) {
            patPassReg.setError("The password is too short, enter minimum 6 character long");
        }

        else if (TextUtils.isEmpty(pat_ConfPassReg)) {
            patConfPassReg.setError("Enter Password Confirmation");
            patConfPassReg.requestFocus();
        }

        else if (!pat_PassReg.equals(pat_ConfPassReg)) {
            Toast.makeText(PatientRegistration.this, "Confirm Password does not match Password", Toast.LENGTH_SHORT).show();
            patConfPassReg.setError("The Password does not match");
            patConfPassReg.requestFocus();
        }

        else {
            result = true;
        }
        return result;
    }

//    //validate data in the input fields
//    private Boolean validate() {
//        boolean result = false;
//
//        pat_CardCode = patCardCode.getText().toString();
//        pat_UniqueCode = patUniqueCode.getText().toString();
//        pat_FirstName = patFirstName.getText().toString();
//        pat_LastName = patLastName.getText().toString();
//        pat_EmailReg = patEmailReg.getText().toString();
//        pat_PassReg = patPassReg.getText().toString();
//        pat_ConfPassReg = patConfPassReg.getText().toString();
//
//        if (pat_UniqueCode.isEmpty()) {
//            patUniqueCode.setError("Enter Patient Unique code");
//            patUniqueCode.requestFocus();
//        }
//
//        else if (pat_FirstName.isEmpty()) {
//            patFirstName.setError("Enter Patient's First Name");
//            patFirstName.requestFocus();
//        }
//
//        else if (pat_LastName.isEmpty()) {
//            patLastName.setError("Enter Patient's Last Name");
//            patLastName.requestFocus();
//        }
//
//        else if (pat_EmailReg.isEmpty()) {
//            patEmailReg.setError("Enter Patient's Email Address");
//            patEmailReg.requestFocus();
//        }
//
//        else if(!Patterns.EMAIL_ADDRESS.matcher(pat_EmailReg).matches()){
//            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
//            patEmailReg.setError("Enter a valid Email Address");
//            patEmailReg.requestFocus();
//        }
//
//        else if (pat_PassReg.isEmpty()) {
//            patPassReg.setError("Enter Patient's password");
//            patPassReg.requestFocus();
//        }
//
//
//        else if (pat_PassReg.length() > 0 && pat_PassReg.length()<6) {
//            patPassReg.setError("The password is too short, enter minimum 6 character long");
//            Toast.makeText(this, "The password is too short, enter minimum 6 character long", Toast.LENGTH_SHORT).show();
//        }
//
//        else if (!pat_PassReg.equals(pat_ConfPassReg)) {
//            Toast.makeText(this, "Confirm Password does not match Password", Toast.LENGTH_SHORT).show();
//            patConfPassReg.setError("Enter same Password");
//            patConfPassReg.requestFocus();
//        }
//
//        else {
//            result = true;
//        }
//        return result;
//    }

    //send email to user to verify if the email is real
    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        sendPatientData();
                        progressDialog.dismiss();
                        Toast.makeText(PatientRegistration.this, "Successful Registered, Email verification was sent", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        //Patient patient = new Patient(pat_CardCode, pat_UniqueCode, pat_FirstName, pat_LastName, pat_EmailReg, hospitalID, doctorID);
                        Intent patMedRec = new Intent (PatientRegistration.this, Login.class);
                        //patMedRec.putExtra("PATID", patient.getPatFirst_Name()+" "+patient.getPatLast_Name()+" "+patient.patUnique_Code);
                        startActivity(patMedRec);
                    }

                    else{
                        progressDialog.dismiss();
                        Toast.makeText(PatientRegistration.this, "Verification email has not been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendPatientData(){
        FirebaseUser pat = firebaseAuth.getCurrentUser();
        assert pat != null;
        String patID = pat.getUid();
        Patient patient = new Patient(pat_CardCode, pat_UniqueCode, pat_FirstName, pat_LastName, pat_EmailReg, patDoctor_key, patHospital_Key);
        databaseReference.child(patID).setValue(patient);
    }
}