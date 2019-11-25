package com.example.danut.smartdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddPatient extends AppCompatActivity {

    private static final String TAG = "AddPatient";

    //Declare variables
    private EditText patCardCode, patUniqueCode, patFirstName, patLastName, patEmailReg, patPassReg, patConfPassReg;
    private TextView textViewPatRegDoc, textViewPatRegHosp;

    private Button buttonPatReg, buttonPatCancelReg;
    private String pat_CardCode, pat_UniqueCode, pat_FirstName, pat_LastName, pat_EmailReg, pat_PassReg, pat_ConfPassReg;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ValueEventListener valueEventListener;

    String hospitalID ="";
    String doctorID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        getIntent().hasExtra("DOCID");
        doctorID = getIntent().getExtras().getString("DOCID");

        getIntent().hasExtra("HOSPID");
        hospitalID =getIntent().getExtras().getString("HOSPID");

        textViewPatRegDoc = (TextView)findViewById(R.id.tvPatientReg);
        textViewPatRegDoc.setText("Add patient to Dr. "+doctorID);

        textViewPatRegHosp = (TextView)findViewById(R.id.tvPatientRegHosp);
        textViewPatRegHosp.setText(hospitalID);

        buttonPatCancelReg = (Button)findViewById(R.id.btnBackDoctor);
        buttonPatCancelReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear input fields
                patUniqueCode.setText("");
                patFirstName.setText("");
                patLastName.setText("");
                patEmailReg.setText("");
                patPassReg.setText("");
                patConfPassReg.setText("");
                finish();
                startActivity(new Intent(AddPatient.this,DoctorPage.class));
            }
        });

        patCardCode = (EditText)findViewById(R.id.etPatCardCode);
        patUniqueCode = (EditText)findViewById(R.id.etPatUniqueCode);
        patFirstName = (EditText)findViewById(R.id.etPatFirstName);
        patLastName = (EditText)findViewById(R.id.etPatLastName);
        patEmailReg = (EditText)findViewById(R.id.etPatEmailReg);
        patPassReg = (EditText)findViewById(R.id.etPatPassReg);
        patConfPassReg = (EditText)findViewById(R.id.etPatConfPassReg);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Patients");

        buttonPatReg = (Button)findViewById(R.id.btnAddPatient);
        buttonPatReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    //upload data to the database
                    String newPatient_Email = patEmailReg.getText().toString().trim();
                    String newPatient_Password = patPassReg.getText().toString().trim();

                    progressDialog.setMessage("Register Doctor Details");
                    progressDialog.show();

                    //create new patient into FirebaseDatabase
                    firebaseAuth.createUserWithEmailAndPassword(newPatient_Email, newPatient_Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                Toast.makeText(AddPatient.this, "Registration Failed, this email address was already used to other account",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    //validate data in the input fields
    private Boolean validate() {
        Boolean result = false;

        pat_CardCode = patCardCode.getText().toString();
        pat_UniqueCode = patUniqueCode.getText().toString();
        pat_FirstName = patFirstName.getText().toString();
        pat_LastName = patLastName.getText().toString();
        pat_EmailReg = patEmailReg.getText().toString();
        pat_PassReg = patPassReg.getText().toString();
        pat_ConfPassReg = patConfPassReg.getText().toString();

        if (pat_UniqueCode.isEmpty()) {
            patUniqueCode.setError("Enter Patient Unique code");
            patUniqueCode.requestFocus();
        }

        else if (pat_FirstName.isEmpty()) {
            patFirstName.setError("Enter Patient's First Name");
            patFirstName.requestFocus();
        }

        else if (pat_LastName.isEmpty()) {
            patLastName.setError("Enter Patient's Last Name");
            patLastName.requestFocus();
        }

        else if (pat_EmailReg.isEmpty()) {
            patEmailReg.setError("Enter Patient's Email Address");
            patEmailReg.requestFocus();
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(pat_EmailReg).matches()){
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            patEmailReg.setError("Enter a valid Email Address");
            patEmailReg.requestFocus();
        }

        else if (pat_PassReg.isEmpty()) {
            patPassReg.setError("Enter Patient's password");
            patPassReg.requestFocus();
        }
        else if (pat_PassReg.length()>0 && pat_PassReg.length()<6) {
            patPassReg.setError("The password is too short, enter minimum 6 character long");
            Toast.makeText(this, "The password is too short, enter minimum 6 character long", Toast.LENGTH_SHORT).show();
        }

        else if (!pat_PassReg.equals(pat_ConfPassReg)) {
            Toast.makeText(this, "Confirm Password does not match Password", Toast.LENGTH_SHORT).show();
            patConfPassReg.setError("Enter same Password");
            patConfPassReg.requestFocus();
        }

        else {
            result = true;
        }
        return result;
    }

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
                        Toast.makeText(AddPatient.this, "Successful Registered, Email verification was sent", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        Patient patient = new Patient(pat_CardCode, pat_UniqueCode, pat_FirstName, pat_LastName, pat_EmailReg, hospitalID, doctorID);
                        Intent patMedRec = new Intent (AddPatient.this, LogIn.class);
                        patMedRec.putExtra("PATID", patient.getPatFirst_Name()+" "+patient.getPatLast_Name()+" "+patient.patUnique_Code);
                        startActivity(patMedRec);
                    }

                    else{
                        progressDialog.dismiss();
                        Toast.makeText(AddPatient.this, "Verification email has not been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendPatientData(){
        FirebaseUser pat = firebaseAuth.getCurrentUser();
        String patID = pat.getUid();
        Patient patient = new Patient(pat_CardCode, pat_UniqueCode, pat_FirstName, pat_LastName, pat_EmailReg, hospitalID, doctorID);
        databaseReference.child(patID).setValue(patient);
    }
}
