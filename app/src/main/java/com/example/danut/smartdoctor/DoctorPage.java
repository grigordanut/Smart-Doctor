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

    private Button btn_DocAddPat, btn_DocPatList, btn_DocAddMedRec, btn_DocMedRecList;

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

        btn_DocAddPat = findViewById(R.id.btnDocAddPat);
        btn_DocPatList = findViewById(R.id.btnDocPatList);
        btn_DocAddMedRec = findViewById(R.id.btnDocAddMedRec);
        btn_DocMedRecList = (Button) findViewById(R.id.btnDocMedRecList);

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Doctors user_Doctor = postSnapshot.getValue(Doctors.class);
                    assert user_Doctor != null;

                    user_Doctor.setDoctor_Key(postSnapshot.getKey());

                    assert firebaseUser != null;

                    if (firebaseUser.getUid().equalsIgnoreCase(postSnapshot.getKey())) {

                        tVWelcomeDoctor.setText("Welcome Doctor: " + user_Doctor.getDoctor_FirstName() + " " + user_Doctor.getDoctor_LastName());

                        btn_DocAddPat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                assert firebaseUser != null;

                                Intent add_Pat = new Intent(DoctorPage.this, AddPatient.class);

                                add_Pat.putExtra("HOSPName", user_Doctor.getDoctor_HospName());
                                add_Pat.putExtra("HOSPKey", user_Doctor.getDoctor_HospKey());
                                add_Pat.putExtra("DOCName", user_Doctor.getDoctor_FirstName() + " " + user_Doctor.getDoctor_LastName());
                                add_Pat.putExtra("DOCKey", firebaseUser.getUid());

                                startActivity(add_Pat);
                            }
                        });


                        btn_DocPatList.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent pat_List = new Intent(DoctorPage.this, DoctorPatientsList.class);
                                //pat_List.putExtra("HOSPName", user_Doctor.getDoctor_HospName());
                                //pat_List.putExtra("HOSPKey", user_Doctor.getDoctor_HospKey());
                                //pat_List.putExtra("DOCName", user_Doctor.getDoctor_FirstName() + " " + user_Doctor.getDoctor_LastName());
                                pat_List.putExtra("DOCKey", firebaseUser.getUid());

                                startActivity(pat_List);
                            }
                        });


                        btn_DocAddMedRec.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent add_Rec = new Intent(DoctorPage.this, PatientsListAddMedRecord.class);
                                add_Rec.putExtra("DOCName", user_Doctor.getDoctor_FirstName() + " " + user_Doctor.getDoctor_LastName());
                                add_Rec.putExtra("DOCKey", firebaseUser.getUid());
                                startActivity(add_Rec);
                            }
                        });


                        btn_DocMedRecList.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent rec_List = new Intent(DoctorPage.this, PatientsListShowMedRecord.class);
                                rec_List.putExtra("DOCName", user_Doctor.getDoctor_FirstName() + " " + user_Doctor.getDoctor_LastName());
                                rec_List.putExtra("DOCKey", firebaseUser.getUid());
                                startActivity(rec_List);
                            }
                        });
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
