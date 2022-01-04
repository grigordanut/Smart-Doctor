package com.example.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class HospitalRegistration extends AppCompatActivity {

    private TextInputEditText hospUniqueCode, hospNameReg, hospEmailReg, hospPassReg, hospConfPassReg;

    private String hosp_UniqueCodeReg, hosp_NameReg, hosp_EmailReg, hosp_PassReg, hosp_ConfPassReg;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbRefHospUpload;
    private DatabaseReference dbRefHospCheck;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_registration);

        hospUniqueCode = findViewById(R.id.etHospUniqueCodeReg);
        hospNameReg = findViewById(R.id.etHospNameReg);
        hospEmailReg = findViewById(R.id.etHospEmailReg);
        hospPassReg = findViewById(R.id.etHospPassReg);
        hospConfPassReg = findViewById(R.id.etHospConfPassReg);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        //Upload data to Hospital table
        dbRefHospUpload = FirebaseDatabase.getInstance().getReference("Hospitals");

        //Retrieve data from Hospital table
        dbRefHospCheck = FirebaseDatabase.getInstance().getReference("Hospitals");

        Button buttonHospLogReg = findViewById(R.id.btnHospLogReg);
        buttonHospLogReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HospitalRegistration.this, Login.class));
            }
        });

        Button buttonHospReg = (Button) findViewById(R.id.btnHospReg);
        buttonHospReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateHospRegData()) {

                    final String hosp_NameCheck = Objects.requireNonNull(hospNameReg.getText()).toString().trim();

                    dbRefHospCheck.orderByChild("hosp_Name").equalTo(hosp_NameCheck)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        alertHospitalExists();
                                    } else {

                                        progressDialog.setMessage("Registering Hospital Details");
                                        progressDialog.show();

                                        firebaseAuth.createUserWithEmailAndPassword(hosp_EmailReg, hosp_PassReg)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            sendEmailVerification();

                                                            //clear input text fields
                                                            hospUniqueCode.setText("");
                                                            hospNameReg.setText("");
                                                            hospEmailReg.setText("");
                                                            hospPassReg.setText("");
                                                            hospConfPassReg.setText("");

                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(HospitalRegistration.this, "Registration Failed, this email address was already used to other account", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(HospitalRegistration.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private Boolean validateHospRegData() {

        boolean result = false;

        hosp_UniqueCodeReg = Objects.requireNonNull(hospUniqueCode.getText()).toString().trim();
        hosp_NameReg = Objects.requireNonNull(hospNameReg.getText()).toString().trim();
        hosp_EmailReg = Objects.requireNonNull(hospEmailReg.getText()).toString().trim();
        hosp_PassReg = Objects.requireNonNull(hospPassReg.getText()).toString().trim();
        hosp_ConfPassReg = Objects.requireNonNull(hospConfPassReg.getText()).toString().trim();

        if (TextUtils.isEmpty(hosp_UniqueCodeReg)) {
            hospUniqueCode.setError("Enter Hospital Unique Code");
            hospUniqueCode.requestFocus();
        } else if (TextUtils.isEmpty(hosp_NameReg)) {
            hospNameReg.setError("Enter Hospital Name");
            hospNameReg.requestFocus();
        } else if (TextUtils.isEmpty(hosp_EmailReg)) {
            hospEmailReg.setError("Enter Hospital Email Address");
            hospEmailReg.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(hosp_EmailReg).matches()) {
            Toast.makeText(HospitalRegistration.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            hospEmailReg.setError("Enter a valid Email Address");
            hospEmailReg.requestFocus();
        } else if (TextUtils.isEmpty(hosp_PassReg)) {
            hospPassReg.setError("Enter Password");
            hospPassReg.requestFocus();
        } else if (hosp_PassReg.length() > 0 && hosp_PassReg.length() < 6) {
            hospPassReg.setError("The password is too short, enter minimum 6 character long");
        } else if (TextUtils.isEmpty(hosp_ConfPassReg)) {
            hospConfPassReg.setError("Enter Password Confirmation");
            hospConfPassReg.requestFocus();
        } else if (!hosp_PassReg.equals(hosp_ConfPassReg)) {
            Toast.makeText(HospitalRegistration.this, "Confirm Password does not match Password", Toast.LENGTH_SHORT).show();
            hospConfPassReg.setError("The Password does not match");
            hospConfPassReg.requestFocus();
        } else {
            result = true;
        }

        return result;
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        uploadHospRegData();
                        progressDialog.dismiss();
                        Toast.makeText(HospitalRegistration.this, "Successful Registered, Email verification has been sent", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(HospitalRegistration.this, Login.class));
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(HospitalRegistration.this, "Verification email has not been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //Upload Hospital data
    private void uploadHospRegData() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        String hosp_Id = user.getUid();
        Hospital hosp = new Hospital(hosp_UniqueCodeReg, hosp_NameReg, hosp_EmailReg);
        dbRefHospUpload.child(hosp_Id).setValue(hosp);
    }

    //Notify if the Hospital name already exist in database
    public void alertHospitalExists() {
        final String hosp_NameCheckAlert = Objects.requireNonNull(hospNameReg.getText()).toString().trim();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("The Hospital: " + hosp_NameCheckAlert + " already exists!");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert1 = alertDialogBuilder.create();
        alert1.show();
    }
}