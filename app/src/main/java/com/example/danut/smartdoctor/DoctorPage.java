package com.example.danut.smartdoctor;

import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class DoctorPage extends AppCompatActivity {

    //Access Doctors database
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    //Declare variables
    private TextView tVWelcomeDoctor;

    private Button buttonDocPatList, buttonDocAddMedRec, buttonDocMedRecList;

//    Doctors user_Doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_page);

        Objects.requireNonNull(getSupportActionBar()).setTitle("DOCTORS: main page");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //retrieve data from Doctors database
        databaseReference = FirebaseDatabase.getInstance().getReference("Doctors");

        //initialise the variables
        tVWelcomeDoctor = findViewById(R.id.tvWelcomeDoctor);

        Button btn_DocAddPat = findViewById(R.id.btnDocAddPat);

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Doctors user_Doctor = postSnapshot.getValue(Doctors.class);

                    assert user_Doctor != null;
                    assert firebaseUser != null;

                    if (firebaseUser.getUid().equalsIgnoreCase(postSnapshot.getKey())) {

                        tVWelcomeDoctor.setText("Welcome Doctor: " + user_Doctor.getDoctor_FirstName() + " " + user_Doctor.getDoctor_LastName());

                        btn_DocAddPat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                assert firebaseUser != null;

                                Intent intent_AddPat = new Intent(DoctorPage.this, AddPatient.class);

                                intent_AddPat.putExtra("HOSPName", user_Doctor.getDoctor_HospName());
                                intent_AddPat.putExtra("HOSPKey", user_Doctor.getDoctor_HospKey());
                                intent_AddPat.putExtra("DOCName", user_Doctor.getDoctor_FirstName() + " " + user_Doctor.getDoctor_LastName());
                                intent_AddPat.putExtra("DOCKey", firebaseUser.getUid());

                                startActivity(intent_AddPat);
                            }
                        });

                        buttonDocPatList = (Button) findViewById(R.id.btnDocPatList);
                        buttonDocPatList.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent patList = new Intent(DoctorPage.this, PatientsList.class);
                                patList.putExtra("HOSPName", user_Doctor.getDoctor_HospName());
                                patList.putExtra("HOSPKey", user_Doctor.getDoctor_HospKey());
                                patList.putExtra("DOCName", user_Doctor.getDoctor_FirstName() + " " + user_Doctor.getDoctor_LastName());
                                patList.putExtra("DOCKey", firebaseUser.getUid());

                                startActivity(patList);
                            }
                        });

//                        buttonDocAddMedRec = (Button) findViewById(R.id.btnDocAddMedRec);
//                        buttonDocAddMedRec.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent addRec = new Intent(DoctorPage.this, PatientListAddMedRecord.class);
//                                addRec.putExtra("DOCID", doc.getDocFirst_Name() + " " + doc.getDocLast_Name());
//                                startActivity(addRec);
//                            }
//                        });
//
//                        buttonDocMedRecList = (Button) findViewById(R.id.btnDocMedRecList);
//                        buttonDocMedRecList.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent pat_List = new Intent(DoctorPage.this, PatientListShowMedRecord.class);
//                                pat_List.putExtra("DOCID", doc.getDocFirst_Name() + " " + doc.getDocLast_Name());
//                                startActivity(pat_List);
//                            }
//                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorPage.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    //user log out
    private void logOutDoctor() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(DoctorPage.this, MainActivity.class));
    }

    private void EditProfile() {
        startActivity(new Intent(DoctorPage.this, DoctorEditProfile.class));
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doctor, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOutDoctor: {
                logOutDoctor();
            }
            case R.id.doctorDetails: {
                EditProfile();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
