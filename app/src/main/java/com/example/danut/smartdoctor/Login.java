package com.example.danut.smartdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    //declare variables
    private EditText emailLogIn, passwordLogIn;
    private TextView textViewInfoLog, textViewForgotPassUser, textViewNewUser;
    private Button buttonSignUp, buttonLogIn, buttonCancelLogIn;
    private int counter = 5;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String uniqueCode_LogIn, emailLog_User,passwordLog_User;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //Initialize variables
        emailLogIn= (EditText) findViewById(R.id.etEmailLogDoctor);
        passwordLogIn = (EditText) findViewById(R.id.etPassLogDoctor);
        textViewInfoLog = (TextView) findViewById(R.id.tvInfoUser);
        textViewNewUser = (TextView)findViewById(R.id.tvNewUser);
        buttonLogIn = (Button) findViewById(R.id.btnLogInUser);
        buttonCancelLogIn = (Button)findViewById(R.id.btnCancelLogUser);

        textViewInfoLog.setText("No of attempts remaining: " + counter);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        /*
        if (user != null) {
            finish();
            startActivity(new Intent(Login.this, DoctorPage.class));
        }*/

        //Action TextView Forgotten Password
        textViewForgotPassUser = (TextView)findViewById(R.id.tvForgotPasswordUser);
        textViewForgotPassUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fPassword = new Intent(Login.this, ResetPassword.class);
                startActivity(fPassword);
            }
        });

        //Action button log in user
        buttonCancelLogIn = (Button) findViewById(R.id.btnCancelLogUser);
        buttonCancelLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLogIn.setText("");
                passwordLogIn.setText("");
            }
        });

        //Action TextView new user
        textViewNewUser = (TextView) findViewById(R.id.tvNewUser);
        textViewNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign = new Intent(Login.this, CheckUniqueCode.class);
                startActivity(sign);
            }
        });

        //Action button Login
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }

    //validate input data into the editText
    public void validate() {
        emailLog_User = emailLogIn.getText().toString();
        passwordLog_User = passwordLogIn.getText().toString();

        if (emailLog_User.isEmpty()) {
            emailLogIn.setError("Enter your Email Address");
            Toast.makeText(this, "Please enter your Email Address", Toast.LENGTH_SHORT).show();
            emailLogIn.requestFocus();
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(emailLog_User).matches()){
            emailLogIn.setError("Enter a valid Email Address");
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            emailLogIn.requestFocus();
        }

        else if (passwordLog_User.isEmpty()){
            passwordLogIn.setError("Enter your Password");
            Toast.makeText(this, "Please enter your Password",Toast.LENGTH_SHORT).show();
            passwordLogIn.requestFocus();
        }

        //log in a new user
        else {
            progressDialog.setMessage("Welcome to Smart Doctor");
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(emailLog_User, passwordLog_User).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        checkEmailVerification();
                        //clear data
                        emailLogIn.setText("");
                        passwordLogIn.setText("");
                    }

                    else {
                        Toast.makeText(Login.this, "Log In failed, this email is not Registered", Toast.LENGTH_SHORT).show();
                        counter--;
                        progressDialog.dismiss();
                        textViewInfoLog.setText("No of attempts remaining: " + counter);

                        if (counter == 0) {
                            textViewInfoLog.setText("No more attempts remaining, please press Forgoten Password");
                            buttonLogIn.setEnabled(false);
                            buttonLogIn.setBackgroundColor(Color.parseColor("#cc3333"));
                            buttonLogIn.setText("Stop");
                        }
                    }
                }
            });
        }
    }

    //check if the email has been verified
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailFlag = firebaseUser.isEmailVerified();

        if(emailFlag){
            //progressDialog.dismiss();
            checkUserAccount();
        }

        else{
            progressDialog.dismiss();
            Toast.makeText(this, "Please verify your Email first", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

    private void checkUserAccount() {

        //check if the user Hospitals try to log in
        databaseReference = FirebaseDatabase.getInstance().getReference("Hospitals");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot users: dataSnapshot.getChildren()){
                    Hospitals hos = users.getValue(Hospitals.class);
                    if(emailLog_User.equals(hos.getHospEmail_Address())){
                        //hos.setHospitalKey(users.getKey());
                        Toast.makeText(Login.this, "Log In successful Hospitals", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, HospitalPage.class));
                        progressDialog.dismiss();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Login.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        //check if the user Doctor try to log in
        databaseReference = FirebaseDatabase.getInstance().getReference("Doctors");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot users: dataSnapshot.getChildren()){
                    Doctor doc = users.getValue(Doctor.class);
                    if(emailLog_User.equals(doc.getDocEmail_Address())){
                        doc.setDoctorKey(users.getKey());
                        Toast.makeText(Login.this, "Log In successful Doctor", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, DoctorPage.class));
                        progressDialog.dismiss();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Login.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        //check if the user patient try to log in
        databaseReference = FirebaseDatabase.getInstance().getReference("Patients");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot users: dataSnapshot.getChildren()){
                    Patient pat = users.getValue(Patient.class);
                    if(emailLog_User.equals(pat.getPatEmail_Address())){
                        pat.setPatientKey(users.getKey());
                        Toast.makeText(Login.this, "Log In successful Patient", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, PatientPage.class));
                        progressDialog.dismiss();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Login.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
