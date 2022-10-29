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

    private DatabaseReference databaseRefHosp;
    private DatabaseReference databaseRefDoc;
    private DatabaseReference databaseRefPat;

    private EditText emailLogUser, passLogUser;
    private String email_LogUser, pass_LogUser;

    private TextView tVCounterUser;

    private int counter = 5;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Log in User");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseRefHosp = FirebaseDatabase.getInstance().getReference("Hospitals");

        databaseRefDoc = FirebaseDatabase.getInstance().getReference("Doctors");

        databaseRefPat = FirebaseDatabase.getInstance().getReference("Patients");

        //Initialize variables
        emailLogUser = findViewById(R.id.etEmailLogUser);
        passLogUser = findViewById(R.id.etPassLogUser);

        tVCounterUser = findViewById(R.id.tvCounterUser);

        //Action button log in user
        Button btn_RegNewUser = findViewById(R.id.btnRegNewUser);
        btn_RegNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, CheckUniqueCode.class));
            }
        });

        //Action TextView Forgotten Password
        TextView tVForgotPassUser = findViewById(R.id.tvForgotPassUser);
        tVForgotPassUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, ResetPassword.class);
                startActivity(intent);
            }
        });

        //Action button LogIn
        Button btn_LogUser = findViewById(R.id.btnLogUser);
        btn_LogUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateLogInData()) {

                    progressDialog.setMessage("Log in user");
                    progressDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(email_LogUser, pass_LogUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                checkEmailVerification();

                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthInvalidUserException e) {
                                    emailLogUser.setError("This email is not registered.");
                                    emailLogUser.requestFocus();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    passLogUser.setError("Invalid Password");
                                    passLogUser.requestFocus();
                                } catch (Exception e) {
                                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                counter --;
                                tVCounterUser.setText("Attempts remaining: " + counter);

                                if (counter == 0) {
                                    tVCounterUser.setText("No more attempts remaining, please press Forgot Password");
                                    btn_LogUser.setEnabled(false);
                                    btn_LogUser.setBackgroundColor(Color.parseColor("#cc3333"));
                                    btn_LogUser.setText("Stop");
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
    private Boolean validateLogInData() {

        boolean result = false;

        email_LogUser = Objects.requireNonNull(emailLogUser.getText()).toString().trim();
        pass_LogUser = Objects.requireNonNull(passLogUser.getText()).toString().trim();

        if (email_LogUser.isEmpty()) {
            emailLogUser.setError("Enter your Email Address");
            emailLogUser.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email_LogUser).matches()) {
            emailLogUser.setError("Enter a valid Email Address");
            emailLogUser.requestFocus();
        } else if (pass_LogUser.isEmpty()) {
            passLogUser.setError("Enter your Password");
            passLogUser.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    //check if the email has been verified
    private void checkEmailVerification() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            if (firebaseUser.isEmailVerified()) {
                checkUserAccount();

            } else {
                Toast.makeText(this, "Please verify your Email first", Toast.LENGTH_SHORT).show();
            }

            progressDialog.dismiss();
        }
    }

    private void checkUserAccount() {

        //Check if the user Hospital try to log in
        final String hosp_emailCheck = Objects.requireNonNull(emailLogUser.getText()).toString().trim();

        databaseRefHosp.orderByChild("hosp_Email").equalTo(hosp_emailCheck)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Hospital successfully Log in!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, HospitalPage.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Login.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //Check if the user Doctors try to log in
        final String doc_emailCheck = Objects.requireNonNull(emailLogUser.getText()).toString().trim();

        databaseRefDoc.orderByChild("doctor_Email").equalTo(doc_emailCheck)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Doctors successfully Log in!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, DoctorPage.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Login.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //Check if the user Patients try to log in
        final String pat_emailCheck = Objects.requireNonNull(emailLogUser.getText()).toString().trim();

        databaseRefPat.orderByChild("patEmail_Address").equalTo(pat_emailCheck)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Patients successfully Log in!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, PatientPage.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Login.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
