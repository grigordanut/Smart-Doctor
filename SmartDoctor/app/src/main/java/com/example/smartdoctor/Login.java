package com.example.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {

    //declare variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference databaseReference;

    private TextInputEditText emailLogUser, passwordLogUser;
    private String emailLog_User, passwordLog_User;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Log in User");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Initialize variables
        emailLogUser = findViewById(R.id.etEmailLogUser);
        passwordLogUser = findViewById(R.id.etPassLogUser);

        //Action button log in user
        Button buttonRegNewUser = (Button) findViewById(R.id.btnRegNewUser);
        buttonRegNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, CheckUniqueCode.class));
            }
        });

        //Action TextView Forgotten Password
        TextView tVForgotPassUser = findViewById(R.id.tvForgotPasswordUser);
        tVForgotPassUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fPassword = new Intent(Login.this, ResetPassword.class);
                startActivity(fPassword);
            }
        });

        //Action button LogIn
        Button buttonLogIn = findViewById(R.id.btnLogInUser);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateLogInData()) {

                    progressDialog.setMessage("Log in user");
                    progressDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(emailLog_User, passwordLog_User).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                checkEmailVerification();

                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthInvalidUserException e){
                                    emailLogUser.setError("This email is not registered.");
                                    emailLogUser.requestFocus();
                                } catch (FirebaseAuthInvalidCredentialsException e){
                                    passwordLogUser.setError("Invalid Password");
                                    passwordLogUser.requestFocus();
                                } catch (Exception e) {
                                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    //validate input data into the editText
    public Boolean validateLogInData() {

        boolean result = false;

        emailLog_User = Objects.requireNonNull(emailLogUser.getText()).toString().trim();
        passwordLog_User = Objects.requireNonNull(passwordLogUser.getText()).toString().trim();

        if (emailLog_User.isEmpty()) {
            emailLogUser.setError("Enter your Email Address");
            emailLogUser.requestFocus();
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(emailLog_User).matches()){
            emailLogUser.setError("Enter a valid Email Address");
            emailLogUser.requestFocus();
        }

        else if (passwordLog_User.isEmpty()){
            passwordLogUser.setError("Enter your Password");
            passwordLogUser.requestFocus();
        }

        else{
            result = true;
        }

        return result;
    }

    //check if the email has been verified
    private void checkEmailVerification(){

        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            boolean emailFlag = firebaseUser.isEmailVerified();

            progressDialog.dismiss();
            if(emailFlag){
                checkUserAccount();
            }

            else{
                Toast.makeText(this, "Please verify your Email first", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
            }
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

                    if (hos != null){
                        if(emailLog_User.equals(hos.getHosp_Email())){
                            hos.setHosp_Key(users.getKey());

                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Log in successful Hospitals", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, HospitalPage.class));
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Login.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        //check if the user Doctors try to log in
        databaseReference = FirebaseDatabase.getInstance().getReference("Doctors");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot users: dataSnapshot.getChildren()){
                    Doctors doc = users.getValue(Doctors.class);

                    if (doc != null) {
                        if(emailLog_User.equals(doc.getDocEmail_Address())){
                            doc.setDoc_Key(users.getKey());

                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Log in successful Doctors", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, DoctorPage.class));
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Login.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        //check if the user Patients try to log in
        databaseReference = FirebaseDatabase.getInstance().getReference("Patients");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot users: dataSnapshot.getChildren()){
                    Patients pat = users.getValue(Patients.class);

                    if (pat != null) {
                        if(emailLog_User.equals(pat.getPatEmail_Address())){
                            pat.setPatient_Key(users.getKey());

                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Log In successful Patients", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, PatientPage.class));
                            finish();
                        }
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