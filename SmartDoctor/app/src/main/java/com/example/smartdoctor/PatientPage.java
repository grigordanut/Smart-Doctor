package com.example.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PatientPage extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private DatabaseReference databaseReference;

    private TextView textViewWelcomePatient;

    private Button buttonSeeMedRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_page);

        //initialise the variables
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        textViewWelcomePatient = findViewById(R.id.tvWelcomePatient);
    }

    @Override
    public void onStart(){
        super.onStart();
        loadPatientData();
    }

    private void loadPatientData(){

        //retrieve data from database into text views
        databaseReference = FirebaseDatabase.getInstance().getReference("Patients");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    final FirebaseUser user_Pat = firebaseAuth.getCurrentUser();
                    final Patients pat_Data = postSnapshot.getValue(Patients.class);

                    if (pat_Data != null) {
                        if (user_Pat != null) {
                            if (user_Pat.getUid().equals(postSnapshot.getKey())){
                                textViewWelcomePatient.setText("Welcome: " + pat_Data.getPatFirst_Name()+ " " + pat_Data.getPatLast_Name());

                                buttonSeeMedRecord = (Button)findViewById(R.id.btnSeeMedRecord);
                                buttonSeeMedRecord.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent rec_Pat = new Intent(PatientPage.this, MedicalRecordPatient.class);
                                        rec_Pat.putExtra("DOCName", pat_Data.getPatDoc_Name());
                                        rec_Pat.putExtra("PATName", pat_Data.getPatFirst_Name()+" "+pat_Data.getPatLast_Name());
                                        rec_Pat.putExtra("PATKey", user_Pat.getUid());
                                        startActivity(rec_Pat);
                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientPage.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Patients log out
    private void patientLogOut(){
        firebaseAuth.signOut();
        startActivity(new Intent(PatientPage.this, MainActivity.class));
        finish();
    }

    private void patientEditProfile(){
        startActivity(new Intent(PatientPage.this,PatientEditProfile.class));
        finish();
    }

    private void patientChangeEmail(){
        startActivity(new Intent(PatientPage.this,PatientChangeEmail.class));
        finish();
    }

    private void patientChangePassword(){
        startActivity(new Intent(PatientPage.this,PatientChangePassword.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.patientLogOut){
            alertDialogPatientLogout();
        }

        if (item.getItemId() == R.id.patientEditProfile){
            patientEditProfile();
        }

        if (item.getItemId() == R.id.patientChangeEmail){
            patientChangeEmail();
        }

        if (item.getItemId() == R.id.patientChangePassword){
            patientChangePassword();
        }

        return super.onOptionsItemSelected(item);
    }

    private void alertDialogPatientLogout(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PatientPage.this);
        alertDialogBuilder
                .setMessage("Are sure to Log Out?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                patientLogOut();
                            }
                        })

                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert1 = alertDialogBuilder.create();
        alert1.show();
    }
}