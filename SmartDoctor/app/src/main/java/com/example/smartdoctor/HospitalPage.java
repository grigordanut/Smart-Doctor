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
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HospitalPage extends AppCompatActivity {

    //Access customer database
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    //Retrieve and Display data from Hospitals database
    private DatabaseReference hospitalDatabaseReference;
    private ValueEventListener hospitalEventListener;

    //Retrieve and Display data from Doctors database
    private DatabaseReference doctorsDatabaseReference;
    private ValueEventListener doctorsEventListener;
    private List<Doctor> doctorsList;

    private int numberDoctorsAv;

    private String hospital_Key;


    private TextView tVWelcomeHospital, tVShowHospitalDetails, tVShowDoctorsAv;

    //Declaring some objects
    private DrawerLayout drawerLayoutHospital;
    private ActionBarDrawerToggle drawerToggleHospital;
    private NavigationView navigationViewUserRent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_page);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        doctorsList = new ArrayList<>();

        tVWelcomeHospital = findViewById(R.id.tvWelcomeHospital);
        tVShowHospitalDetails = findViewById(R.id.tvShowHospitalDetails);
        tVShowDoctorsAv = findViewById(R.id.tvShowDoctorsAv);

        drawerLayoutHospital = findViewById(R.id.activity_hospital_page);
        navigationViewUserRent = findViewById(R.id.navViewHospPage);

        drawerToggleHospital = new ActionBarDrawerToggle(this, drawerLayoutHospital, R.string.open_doctorPage, R.string.close_doctorPage);

        drawerLayoutHospital.addDrawerListener(drawerToggleHospital);
        drawerToggleHospital.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //retrieve data from database into text views
        hospitalDatabaseReference = FirebaseDatabase.getInstance().getReference("Hospitals");

        hospitalEventListener = hospitalDatabaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    final FirebaseUser hosp_Db = firebaseAuth.getCurrentUser();
                    final Hospital hosp_Data = postSnapshot.getValue(Hospital.class);

                    assert hosp_Db != null;
                    assert hosp_Data != null;

                    hospital_Key = hosp_Db.getUid();

                    if (hosp_Db.getUid().equalsIgnoreCase(postSnapshot.getKey())) {
                        tVWelcomeHospital.setText("Welcome: " + hosp_Data.getHosp_Name());
                        tVShowHospitalDetails.setText("Hospital Name: \n" + hosp_Data.getHosp_Name()+"\n\nEmail: \n"+hosp_Data.hospEmail_Address);

                        //Adding Click Events to our navigation drawer item
                        navigationViewUserRent.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                            @SuppressLint("NonConstantResourceId")
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                int id = item.getItemId();
                                switch (id) {
                                    //Add Doctors
                                    case R.id.hospital_addDoctors:
                                        Intent add_Doctors = new Intent(HospitalPage.this, DoctorRegistration.class);
                                        add_Doctors.putExtra("HOSPName", hosp_Data.getHosp_Name());
                                        add_Doctors.putExtra("HOSPKey", hosp_Db.getUid());
                                        startActivity(add_Doctors);
                                        break;

                                    //Show Doctors List
                                    case R.id.hospitalShow_doctorsList:
                                        Intent doc_List = new Intent(HospitalPage.this, DoctorsList.class);
                                        doc_List.putExtra("HOSPName", hosp_Data.getHosp_Name());
                                        doc_List.putExtra("HOSPKey", hosp_Db.getUid());
                                        startActivity(doc_List);
                                        break;

                                    //Show Patients List
                                    case R.id.hospShow_patientList:
                                        Intent pat_List = new Intent(HospitalPage.this, PatientsListHospital.class);
                                        pat_List.putExtra("HOSPName", hosp_Data.getHosp_Name());
                                        pat_List.putExtra("HOSPId", hosp_Db.getUid());
                                        startActivity(pat_List);
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
                Toast.makeText(HospitalPage.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void hospitalLogOut(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(HospitalPage.this, MainActivity.class));
    }

    public void hospitalEditProfile(){
        finish();
        startActivity(new Intent(HospitalPage.this, HospitalEditProfile.class));
    }

    public void hospitalChangeEmail(){
        finish();
        startActivity(new Intent(HospitalPage.this, HospitalChangeEmail.class));
    }

    public void hospitalChangePassword(){
        finish();
        startActivity(new Intent(HospitalPage.this, HospitalChangePassword.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hospital_page, menu);
        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (drawerToggleHospital.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.hospitalLogOut){
            alertDialogHospLogout();
        }

        if (item.getItemId() == R.id.hospitalEditProfile){
            hospitalEditProfile();
        }

        if (item.getItemId() == R.id.hospitalChangeEmail){
            hospitalChangeEmail();
        }

        if (item.getItemId() == R.id.hospitalChangePassword){
            hospitalChangePassword();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadDoctorsAv();
        //loadPatientsAv();
    }

    private void loadDoctorsAv() {

        //initialize the Doctors database
        doctorsDatabaseReference = FirebaseDatabase.getInstance().getReference("Doctors");

        doctorsEventListener = doctorsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Doctor doctor_Data = postSnapshot.getValue(Doctor.class);
                    assert doctor_Data != null;
                    if(doctor_Data.getDocHosp_Key().equals(hospital_Key)){
                        doctor_Data.setDoc_Key(postSnapshot.getKey());
                        doctorsList.add(doctor_Data);
                        numberDoctorsAv = doctorsList.size();
                        tVShowDoctorsAv.setText(String.valueOf(numberDoctorsAv));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void alertDialogHospLogout(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HospitalPage.this);
        alertDialogBuilder
                .setMessage("Are sure to Log Out?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                hospitalLogOut();
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