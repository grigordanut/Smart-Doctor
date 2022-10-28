package com.example.danut.smartdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoctorRegister extends AppCompatActivity {

    //Declare variables
    private EditText docUniqueCode, docFirstName, docLastName, docPhone, docEmailReg, docPassReg, docConfPassReg;
    private TextView textViewDoctorRegister;

    private Button buttonDocReg, buttonDocCancelReg;
    private String doc_UniqueCode, doc_FirstName, doc_LastName, doc_Phone, doc_EmailReg, doc_PassReg, doc_ConfPassReg;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    String hospitalID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);

        getIntent().hasExtra("HOSPID");
        hospitalID = getIntent().getExtras().getString("HOSPID");

        textViewDoctorRegister = (TextView)findViewById(R.id.tvDoctorReg);
        textViewDoctorRegister.setText("Add Doctor to "+hospitalID);

        buttonDocCancelReg = (Button)findViewById(R.id.btnDoctorCancelReg);
        buttonDocCancelReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear input fields
                docUniqueCode.setText("");
                docFirstName.setText("");
                docLastName.setText("");
                docPhone.setText("");
                docEmailReg.setText("");
                docPassReg.setText("");
                docConfPassReg.setText("");
                finish();
                startActivity(new Intent(DoctorRegister.this, MainActivity.class));
            }
        });


        docUniqueCode = (EditText)findViewById(R.id.etDocUniqueCode);
        docFirstName = (EditText)findViewById(R.id.etDocFirstName);
        docLastName = (EditText)findViewById(R.id.etDocLastName);
        docPhone = (EditText)findViewById(R.id.etDocPhoneNumber);
        docEmailReg = (EditText)findViewById(R.id.etDocEmailReg);
        docPassReg = (EditText)findViewById(R.id.etDocPassReg);
        docConfPassReg = (EditText)findViewById(R.id.etDocConfPassReg);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Doctors");

        buttonDocReg = (Button)findViewById(R.id.btnDocReg);
        buttonDocReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    //upload data to the database
                    String newDoctor_Email = docEmailReg.getText().toString().trim();
                    String newDoctor_Password = docPassReg.getText().toString().trim();

                    progressDialog.setMessage("Register Doctor Details");
                    progressDialog.show();

                    //create new user into Firebase Database
                    firebaseAuth.createUserWithEmailAndPassword(newDoctor_Email, newDoctor_Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                Toast.makeText(DoctorRegister.this, "Registration Failed, this email address was already used to other account",Toast.LENGTH_SHORT).show();
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

        doc_UniqueCode = docUniqueCode.getText().toString();
        doc_FirstName = docFirstName.getText().toString();
        doc_LastName = docLastName.getText().toString();
        doc_Phone = docPhone.getText().toString();
        doc_EmailReg = docEmailReg.getText().toString();
        doc_PassReg = docPassReg.getText().toString();
        doc_ConfPassReg = docConfPassReg.getText().toString();

        if (doc_UniqueCode.isEmpty()) {
            docUniqueCode.setError("Enter Doctor Unique code");
            docUniqueCode.requestFocus();
        }

        else if (doc_FirstName.isEmpty()) {
            docFirstName.setError("Enter Doctor's First Name");
            docFirstName.requestFocus();
        }

        else if (doc_LastName.isEmpty()) {
            docLastName.setError("Enter Doctor's Last Name");
            docLastName.requestFocus();
        }

        else if (doc_EmailReg.isEmpty()) {
            docEmailReg.setError("Enter Doctor's Email Address");
            docEmailReg.requestFocus();
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(doc_EmailReg).matches()){
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            docEmailReg.setError("Enter a valid Email Address");
            docEmailReg.requestFocus();
        }

        else if (doc_PassReg.isEmpty()) {
            docPassReg.setError("Enter Hospitals password");
            docPassReg.requestFocus();
        }
        else if (doc_PassReg.length()>0 && doc_PassReg.length()<6) {
            docPassReg.setError("The password is too short, enter minimum 6 character long");
            Toast.makeText(this, "The password is too short, enter minimum 6 character long", Toast.LENGTH_SHORT).show();
        }

        else if (!doc_PassReg.equals(doc_ConfPassReg)) {
            Toast.makeText(this, "Confirm Password does not match Password", Toast.LENGTH_SHORT).show();
            docConfPassReg.setError("Enter same Password");
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
                        sendDoctorData();
                        progressDialog.dismiss();
                        Toast.makeText(DoctorRegister.this, "Successful Registered, Email verification was sent", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(DoctorRegister.this, Login.class));
                    }

                    else{
                        progressDialog.dismiss();
                        Toast.makeText(DoctorRegister.this, "Verification email has not been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendDoctorData(){
        //Add doctor data to Firebase Database
        FirebaseUser doc = firebaseAuth.getCurrentUser();
        String docID = doc.getUid();
        Doctor doctor = new Doctor(doc_UniqueCode, doc_FirstName, doc_LastName, doc_Phone, doc_EmailReg, hospitalID);
        databaseReference.child(docID).setValue(doctor);
    }
}
