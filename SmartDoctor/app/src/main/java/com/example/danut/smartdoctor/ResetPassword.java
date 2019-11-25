package com.example.danut.smartdoctor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    //declare variables
    private EditText emailResetPass;
    private Button buttonResetPass;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //initialize variables
        emailResetPass = (EditText) findViewById(R.id.etForgotPassword);
        buttonResetPass = (Button) findViewById(R.id.btnResetPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        //Action of the button Reset password
        buttonResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_ResetPass = emailResetPass.getText().toString().trim();

                //check the input fields
                if (email_ResetPass.isEmpty()){
                    Toast.makeText(ResetPassword.this, "Please enter your registered email ID",Toast.LENGTH_SHORT).show();
                    emailResetPass.setError("Enter your Email Address");
                }

                else if(!Patterns.EMAIL_ADDRESS.matcher(email_ResetPass).matches()){
                    Toast.makeText(ResetPassword.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    emailResetPass.setError("Enter a valid Email Address");
                }

                //change the old password to a new password
                else{
                    firebaseAuth.sendPasswordResetEmail(email_ResetPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ResetPassword.this, "The password reset email was sent",Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ResetPassword.this, LogIn.class));
                            }
                            else{
                                Toast.makeText(ResetPassword.this, "Error in sending password reset email",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
