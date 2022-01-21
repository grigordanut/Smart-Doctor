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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class DoctorRegistration extends AppCompatActivity {

    //Declare variables
    private TextInputEditText docUniqueCode, docFirstName, docLastName, docPhone, docEmailReg, docPassReg, docConfPassReg;
    private TextView tVHospNameDoctorReg;

    private String doc_UniqueCode, doc_FirstName, doc_LastName, doc_Phone, doc_EmailReg, doc_PassReg, doc_ConfPassReg;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private String docHospitalName = "";
    private String docHospitalKey = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_registration);

        progressDialog = new ProgressDialog(this);

        tVHospNameDoctorReg = findViewById(R.id.tvHospNameDoctorReg);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            docHospitalName = bundle.getString("HOSPName");
            docHospitalKey = bundle.getString("HOSPKey");
        }

        tVHospNameDoctorReg.setText("Add doctor to: " + docHospitalName  + " Hospital");

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

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Doctors");

        Button buttonDoctorReg = findViewById(R.id.btnDoctorReg);
        buttonDoctorReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (validateDoctorData()) {

                    progressDialog.setMessage("Registering Doctor Details");
                    progressDialog.show();

                    //create new user into Firebase Database
                    firebaseAuth.createUserWithEmailAndPassword(doc_EmailReg, doc_PassReg).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                sendEmailVerification();

                                //clear input fields
                                docUniqueCode.setText("");
                                docFirstName.setText("");
                                docLastName.setText("");
                                docPhone.setText("");
                                docEmailReg.setText("");
                                docPassReg.setText("");
                                docConfPassReg.setText("");
                            }

                            else{
                                progressDialog.dismiss();
                                Toast.makeText(DoctorRegistration.this, "Registration Failed, this email address was already used to other account",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
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
            docUniqueCode.setError("Enter Doctors Unique Code");
            docUniqueCode.requestFocus();
        }

        else if (TextUtils.isEmpty(doc_FirstName)) {
            docFirstName.setError("Enter Doctors's First Name");
            docFirstName.requestFocus();
        }

        else if (TextUtils.isEmpty(doc_LastName)) {
            docLastName.setError("Enter Doctor's Last Name");
            docLastName.requestFocus();
        }

        else if (TextUtils.isEmpty(doc_Phone)) {
            docPhone.setError("Enter Doctor's Phone Number");
            docPhone.requestFocus();
        }

        else if (TextUtils.isEmpty(doc_EmailReg)) {
            docEmailReg.setError("Enter Doctor's Email Address");
            docEmailReg.requestFocus();
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(doc_EmailReg).matches()) {
            Toast.makeText(DoctorRegistration.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            docEmailReg.setError("Enter a valid Email Address");
            docEmailReg.requestFocus();
        }

        else if (TextUtils.isEmpty(doc_PassReg)) {
            docPassReg.setError("Enter Password");
            docPassReg.requestFocus();
        }

        else if (doc_PassReg.length() > 0 && doc_PassReg.length() < 6) {
            docPassReg.setError("The password is too short, enter minimum 6 character long");
        }

        else if (TextUtils.isEmpty(doc_ConfPassReg)) {
            docConfPassReg.setError("Enter Password Confirmation");
            docConfPassReg.requestFocus();
        }

        else if (!doc_PassReg.equals(doc_ConfPassReg)) {
            Toast.makeText(DoctorRegistration.this, "Confirm Password does not match Password", Toast.LENGTH_SHORT).show();
            docConfPassReg.setError("The Password does not match");
            docConfPassReg.requestFocus();
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
                        sendDoctorRegData();
                        progressDialog.dismiss();
                        Toast.makeText(DoctorRegistration.this, "Successful Registered, Email verification was sent", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(DoctorRegistration.this, Login.class));
                    }

                    else{
                        progressDialog.dismiss();
                        Toast.makeText(DoctorRegistration.this, "Verification email has not been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendDoctorRegData(){

        //Add doctors data to Firebase Database
        FirebaseUser doc = firebaseAuth.getCurrentUser();
        assert doc != null;
        String doc_Id = doc.getUid();
        Doctors doctors = new Doctors(doc_UniqueCode, doc_FirstName, doc_LastName, doc_Phone, doc_EmailReg, docHospitalName, docHospitalKey);
        databaseReference.child(doc_Id).setValue(doctors);
    }
}