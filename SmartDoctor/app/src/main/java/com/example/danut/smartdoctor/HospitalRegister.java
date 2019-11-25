package com.example.danut.smartdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HospitalRegister extends AppCompatActivity {

    private static final String TAG = "HospitalRegister";

    //Declare variables
    private EditText hospUniqueCode, hospName, hospEmailReg, hospPassReg, hospConfPassReg;
    private Button buttonHospReg, buttonHospCancelReg;
    private String hosp_UniqueCode, hosp_Name, hosp_EmailReg, hosp_PassReg, hosp_ConfPassReg;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_register);

        buttonHospCancelReg = (Button)findViewById(R.id.btnHospCancelReg);
        buttonHospCancelReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear input fields
                hospUniqueCode.setText("");
                hospName.setText("");
                hospEmailReg.setText("");
                hospPassReg.setText("");
                hospConfPassReg.setText("");
                finish();
                startActivity(new Intent(HospitalRegister.this, MainActivity.class));

            }
        });

        hospUniqueCode = (EditText)findViewById(R.id.etHospUniqueCodeReg);
        hospName = (EditText)findViewById(R.id.etHospNameReg);
        hospEmailReg = (EditText)findViewById(R.id.etHospEmailReg);
        hospPassReg = (EditText)findViewById(R.id.etHospPassReg);
        hospConfPassReg = (EditText)findViewById(R.id.etHospConfPassReg);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Hospitals");

        buttonHospReg = (Button)findViewById(R.id.btnHospReg);
        buttonHospReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    //upload data to the database
                    String hospital_Email = hospEmailReg.getText().toString().trim();
                    String hospital_Password = hospPassReg.getText().toString().trim();

                    progressDialog.setMessage("Register Hospital details");
                    progressDialog.show();
                    //create new user into FirebaseDatabase
                    firebaseAuth.createUserWithEmailAndPassword(hospital_Email, hospital_Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                sendEmailVerification();

                                //clear input fields
                                hospUniqueCode.setText("");
                                hospName.setText("");
                                hospEmailReg.setText("");
                                hospPassReg.setText("");
                                hospConfPassReg.setText("");
                            }

                            else{
                                progressDialog.dismiss();
                                Toast.makeText(HospitalRegister.this, "Registration Failed, this email address was already used to other account",Toast.LENGTH_SHORT).show();
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

        hosp_UniqueCode = hospUniqueCode.getText().toString();
        hosp_Name = hospName.getText().toString();
        hosp_EmailReg = hospEmailReg.getText().toString();
        hosp_PassReg = hospPassReg.getText().toString();
        hosp_ConfPassReg = hospConfPassReg.getText().toString();

        if (hosp_UniqueCode.isEmpty()) {
            hospUniqueCode.setError("Enter Hospital Unique code");
            hospUniqueCode.requestFocus();
        }

        else if (hosp_Name.isEmpty()) {
            hospName.setError("Enter Hospital Name");
            hospName.requestFocus();
        }

        else if (hosp_EmailReg.isEmpty()) {
            hospEmailReg.setError("Enter Hospital Email Address");
            hospEmailReg.requestFocus();
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(hosp_EmailReg).matches()){
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            hospEmailReg.setError("Enter a valid Email Address");
            hospEmailReg.requestFocus();
        }

        else if (hosp_PassReg.isEmpty()) {
            hospPassReg.setError("Enter Hospital password");
            hospPassReg.requestFocus();
        }
        else if (hosp_PassReg.length()>0 && hosp_PassReg.length()<6) {
            hospPassReg.setError("The password is too short, enter minimum 6 character long");
            Toast.makeText(this, "The password is too short, enter minimum 6 character long", Toast.LENGTH_SHORT).show();
        }

        else if (!hosp_PassReg.equals(hosp_ConfPassReg)) {
            Toast.makeText(this, "Confirm Password does not match Password", Toast.LENGTH_SHORT).show();
            hospConfPassReg.setError("Enter same Password");
            hospConfPassReg.requestFocus();
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
                        sendHospitalData();
                        progressDialog.dismiss();
                        Toast.makeText(HospitalRegister.this, "Successful Registered, Email verification was sent", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(HospitalRegister.this, LogIn.class));
                    }

                    else{
                        progressDialog.dismiss();
                        Toast.makeText(HospitalRegister.this, "Verification email has not been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //send user date to the FirebaseDatabase
    private void sendHospitalData(){
        //add Hospital data to Firebase Database
        FirebaseUser hosp = firebaseAuth.getCurrentUser();
        String hospID = hosp.getUid();
        Hospital hospital = new Hospital(hosp_UniqueCode, hosp_Name, hosp_EmailReg);
        databaseReference.child(hospID).setValue(hospital);
    }
}
