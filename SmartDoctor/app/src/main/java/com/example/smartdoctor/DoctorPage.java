package com.example.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DoctorPage extends AppCompatActivity {

    //Access customer database
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    //Retrieve and Display data from Doctors database
    private DatabaseReference doctorDatabaseReference;
    private ValueEventListener doctorEventListener;

    //Retrieve and Display data from Patients database
    private DatabaseReference patientsDatabaseReference;
    private ValueEventListener patientsEventListener;
    private List<Patient> patientsList;

    private int numberPatientsAv;

    private TextView tVWelcomeDoctor, tVShowDoctorDetails, tVShowPatientsAv;

    //Declaring some objects
    private DrawerLayout drawerLayoutDoctor;
    private ActionBarDrawerToggle drawerToggleDoctor;
    private NavigationView navigationViewDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_page);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        patientsList = new ArrayList<>();

        tVWelcomeDoctor = findViewById(R.id.tvWelcomeDoctor);
        tVShowDoctorDetails = findViewById(R.id.tvShowDoctorDetails);
        tVShowPatientsAv = findViewById(R.id.tvDoctorPatientsAv);

        drawerLayoutDoctor = findViewById(R.id.activity_doctor_page);
        navigationViewDoctor = findViewById(R.id.navViewDoctorPage);

        drawerToggleDoctor = new ActionBarDrawerToggle(this, drawerLayoutDoctor, R.string.open_hospPage, R.string.close_hospPage);

        drawerLayoutDoctor.addDrawerListener(drawerToggleDoctor);
        drawerToggleDoctor.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //retrieve data from database into text views
        doctorDatabaseReference = FirebaseDatabase.getInstance().getReference("Doctors");

        doctorEventListener = doctorDatabaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot ds_Doctor : dataSnapshot.getChildren()) {
                    final FirebaseUser doctor_Db = firebaseAuth.getCurrentUser();

                    final Doctor doctor_Data = ds_Doctor.getValue(Doctor.class);

                    assert doctor_Db != null;
                    assert doctor_Data != null;
                    if (doctor_Db.getUid().equalsIgnoreCase(ds_Doctor.getKey())) {
                        tVWelcomeDoctor.setText("Welcome: " + doctor_Data.getDocFirst_Name() + " " + doctor_Data.getDocLast_Name());
                        tVShowDoctorDetails.setText("Doctor Name: \n" + doctor_Data.getDocFirst_Name() + " "
                                + doctor_Data.getDocLast_Name() + "\n\nEmail: \n" + doctor_Data.getDocEmail_Address());

                        //Adding Click Events to our navigation drawer item
                        navigationViewDoctor.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                            @SuppressLint("NonConstantResourceId")
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                int id = item.getItemId();
                                switch (id) {
                                    //Add Patients
                                    case R.id.doctor_addPatients:
                                        Intent add_Patients = new Intent(DoctorPage.this, PatientRegistration.class);
                                        add_Patients.putExtra("DOCName", doctor_Data.getDocFirst_Name() + " " + doctor_Data.getDocLast_Name());
                                        add_Patients.putExtra("DOCKey", doctor_Db.getUid());
                                        startActivity(add_Patients);
                                        break;

                                    //Show Patients List
                                    case R.id.doctorShow_patientsList:
                                        Intent pat_List = new Intent(DoctorPage.this, DoctorsList.class);
                                        pat_List.putExtra("DOCName", doctor_Data.getDocFirst_Name() + " " + doctor_Data.getDocLast_Name());
                                        pat_List.putExtra("DOCKey", doctor_Db.getUid());
                                        startActivity(pat_List);
                                        break;

                                    //Add Medical Records
                                    case R.id.doctor_addMedicalRecords:
//                                        Intent pat_List = new Intent(HospitalPage.this, PatientsListHospital.class);
//                                        pat_List.putExtra("HOSPName", hosp_Data.getHosp_Name());
//                                        pat_List.putExtra("HOSPId", hosp_Db.getUid());
//                                        startActivity(pat_List);
                                        break;

                                    //Show Medical Records
                                    case R.id.doctorShow_recordsList:
//                                        Intent pat_List = new Intent(DoctorPage.this, DoctorsList.class);
//                                        pat_List.putExtra("DOCName", doctor_Data.getDocFirst_Name() + " " + doctor_Data.getDocLast_Name());
//                                        pat_List.putExtra("DOCKey", doctor_Db.getUid());
//                                        startActivity(pat_List);
                                        break;

                                    default:
                                        return true;
                                }
                                return true;
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

    public void doctorLogOut(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(DoctorPage.this, MainActivity.class));
    }

    public void doctorEditProfile(){
        finish();
        startActivity(new Intent(DoctorPage.this, DoctorEditProfile.class));
    }

    public void doctorChangePassword(){
        finish();
        startActivity(new Intent(DoctorPage.this, DoctorChangePassword.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doctor_page, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (drawerToggleDoctor.onOptionsItemSelected(item)) {
            return true;
        }


        if (item.getItemId() == R.id.doctorLogOut){
            alertDialogDoctorLogout();
        }

        if (item.getItemId() == R.id.doctorEditProfile){
            doctorEditProfile();
        }

        if (item.getItemId() == R.id.doctorChangePassword){
            doctorChangePassword();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        //loadPatientsAv();
    }

//    private void loadPatientsAv() {
//        //initialize the bike storage database
//        firebaseStBikesAvRentCustom = FirebaseStorage.getInstance();
//        databaseRefBikesAvRentCustom = FirebaseDatabase.getInstance().getReference("Bikes");
//
//        bikesAvRentCustomEventListener = databaseRefBikesAvRentCustom.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                bikesRentListAvRentCustom.clear();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    BikesRent bikesRent = postSnapshot.getValue(BikesRent.class);
//                    assert bikesRent != null;
//                    bikesRent.setBike_Key(postSnapshot.getKey());
//                    bikesRentListAvRentCustom.add(bikesRent);
//                    numberBikesAvRentCustom = bikesRentListAvRentCustom.size();
//                    tVCustomBikesRentAv.setText(String.valueOf(numberBikesAvRentCustom));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(CustomerPageRentBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void alertDialogDoctorLogout(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DoctorPage.this);
        alertDialogBuilder
                .setMessage("Are sure to Log Out?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                doctorLogOut();
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