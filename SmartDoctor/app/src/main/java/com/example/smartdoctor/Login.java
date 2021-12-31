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
import android.widget.EditText;
import android.widget.TextView;
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

public class Login extends AppCompatActivity {

    //declare variables
    private TextInputEditText etEmailLogUser, etPasswordLogUser;
    private TextView tVInfoLogUser;

    private int counter = 5;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String email_LogUser, password_LogUser;

    private DatabaseReference databaseReference;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize variables
        etEmailLogUser = findViewById(R.id.etEmailLogUser);
        etPasswordLogUser = findViewById(R.id.etPassLogUser);

        tVInfoLogUser = findViewById(R.id.tvInfoLogUser);

        tVInfoLogUser.setText("Number of attempts remaining: " + counter);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);


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

                    firebaseAuth.signInWithEmailAndPassword(email_LogUser, password_LogUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                checkEmailVerification();
                                //clear data
                                etEmailLogUser.setText("");
                                etPasswordLogUser.setText("");
                            } else {
                                Toast.makeText(Login.this, "Log In failed, this email is not Registered", Toast.LENGTH_SHORT).show();
                                counter--;
                                progressDialog.dismiss();
                                tVInfoLogUser.setText("No of attempts remaining: " + counter);

                                if (counter == 0) {
                                    tVInfoLogUser.setText("No more attempts remaining, please press Forgotten Password");
                                    buttonLogIn.setEnabled(false);
                                    buttonLogIn.setBackgroundColor(Color.parseColor("#cc3333"));
                                    buttonLogIn.setText("Stop");
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    //validate input data into the editText
    public Boolean validateLogInData() {
        boolean result = false;

        email_LogUser = Objects.requireNonNull(etEmailLogUser.getText()).toString().trim();
        password_LogUser = Objects.requireNonNull(etPasswordLogUser.getText()).toString().trim();

        if (email_LogUser.isEmpty()) {
            etEmailLogUser.setError("Enter your Email Address");
            etEmailLogUser.requestFocus();
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email_LogUser).matches()){
            etEmailLogUser.setError("Enter a valid Email Address");
            etEmailLogUser.requestFocus();
        }

        else if (password_LogUser.isEmpty()){
            etPasswordLogUser.setError("Enter your Password");
            etPasswordLogUser.requestFocus();
        }

        else{
            result = true;
        }

        return result;
    }

    //check if the email has been verified
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        boolean emailFlag = firebaseUser.isEmailVerified();

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

        //check if the user Hospital try to log in
        databaseReference = FirebaseDatabase.getInstance().getReference("Hospitals");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot users: dataSnapshot.getChildren()){
                    Hospital hos = users.getValue(Hospital.class);
                    assert hos != null;
                    if(email_LogUser.equals(hos.getHospEmail_Address())){
                        //hos.setHosp_Key(users.getKey());
                        Toast.makeText(Login.this, "Log In successful Hospital", Toast.LENGTH_SHORT).show();
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
                    assert doc != null;
                    if(email_LogUser.equals(doc.getDocEmail_Address())){
                        //doc.;
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
//
//        //check if the user patient try to log in
//        databaseReference = FirebaseDatabase.getInstance().getReference("Patients");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot users: dataSnapshot.getChildren()){
//                    Patient pat = users.getValue(Patient.class);
//                    if(emailLog_User.equals(pat.getPatEmail_Address())){
//                        pat.setPatientKey(users.getKey());
//                        Toast.makeText(LogIn.this, "Log In successful Patient", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(LogIn.this, PatientPage.class));
//                        progressDialog.dismiss();
//                        finish();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(LogIn.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}