package com.example.danut.smartdoctor;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PatientChangePassword extends AppCompatActivity {

    private EditText editTextNewPassPat;
    private Button buttonChangePassPat;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_change_password);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        editTextNewPassPat = (EditText)findViewById(R.id.etNewPassPat);

        buttonChangePassPat = (Button)findViewById(R.id.btnChangePassPat);
        buttonChangePassPat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_PassPat = editTextNewPassPat.getText().toString().trim();

                if( new_PassPat.isEmpty()){
                    Toast.makeText(PatientChangePassword.this, "Please enter your New Password", Toast.LENGTH_SHORT).show();
                    editTextNewPassPat.setError("Enter a new password");
                }
                else{
                    firebaseUser.updatePassword(new_PassPat).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PatientChangePassword.this, "The password has been changed", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(PatientChangePassword.this, Login.class));
                            }

                            else{
                                Toast.makeText(PatientChangePassword.this, "Password change failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
