package com.example.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class HospitalChangeEmail extends AppCompatActivity {

//    private TextInputEditText oldEmailHospital, newEmailHospital;
//
//    private String oldEmail_Hospital, newEmail_Hospital;
//
//    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_change_email);

//        progressDialog = new ProgressDialog(this);
//
//        oldEmailHospital = findViewById(R.id.etOldEmailHospital);
//        newEmailHospital = findViewById(R.id.etNewEmailHospital);
//
//        Button buttonUpdatePassword = findViewById(R.id.btnUpdateHospEmail);
//        buttonUpdatePassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                progressDialog.show();
//                oldEmail_Hospital = Objects.requireNonNull(oldEmailHospital.getText()).toString().trim();
//                newEmail_Hospital = Objects.requireNonNull(newEmailHospital.getText()).toString().trim();
//
//                if (oldEmail_Hospital.isEmpty()) {
//                    oldEmailHospital.setError("Enter your old Email");
//                    oldEmailHospital.requestFocus();
//                } else if (newEmail_Hospital.isEmpty()) {
//                    newEmailHospital.setError("Enter your new Email");
//                    newEmailHospital.requestFocus();
//                } else {
//                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    //String email="";
//                    String password = " ";
//
//                    assert user != null;
//                    AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail(), password));
//                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                user.updateEmail(newEmail_Hospital).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//
//                                    }
//                                });
//                            }
//                                else {
//                                Toast.makeText(HospitalChangeEmail.this, "Authentication failed", Toast.LENGTH_SHORT).show();
//                            }
//                            progressDialog.dismiss();
//                        }
//                    });
//                }
//            }
//        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_change_password_custom, menu);
//        return true;
//    }
//
//    private void goBackPassCustom(){
//        finish();
//        startActivity(new Intent(ChangePassword.this, CustomerPageMain.class));
//    }
//
//    @SuppressLint("NonConstantResourceId")
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.changePassCustomGoBack) {
//            goBackPassCustom();
//        }
//
//        return super.onOptionsItemSelected(item);
//
//    }

}