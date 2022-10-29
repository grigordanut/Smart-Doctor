package com.example.danut.smartdoctor;

import android.content.Intent;
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

public class DoctorPage extends AppCompatActivity {

    private String TAG = "DoctorPage", MSG = "";

    //Declare variables
    private TextView textViewWelcomeDoctor;

    private Button buttonDocAddPat, buttonDocPatList, buttonDocAddMedRec, buttonDocMedRecList;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_page);

//        //initialise the variables
//        textViewWelcomeDoctor = (TextView) findViewById(R.id.tvWelcomeDoctor);
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseDatabase = FirebaseDatabase.getInstance();
//
//        //retrieve data from database into text views
//        databaseReference = FirebaseDatabase.getInstance().getReference("Doctors");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                //retrieve data from database
//                for (DataSnapshot dsDoc : dataSnapshot.getChildren()) {
//                    final FirebaseUser user_Doc = firebaseAuth.getCurrentUser();
//
//                    final Doctors doc = dsDoc.getValue(Doctors.class);
//
//                    if (user_Doc.getEmail().equalsIgnoreCase(doc.docEmail_Address)){
//                        textViewWelcomeDoctor.setText("Doctors "+doc.getDocFirst_Name()+" "+doc.getDocLast_Name());
//
//                        buttonDocPatList = (Button)findViewById(R.id.btnDocPatList);
//                        buttonDocPatList.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent patList = new Intent (DoctorPage.this, PatientsList.class);
//                                patList.putExtra("DOCID", doc.getDocFirst_Name()+" "+doc.getDocLast_Name());
//                                patList.putExtra("HOSPID",doc.getDocHosp_ID());
//                                startActivity(patList);
//                            }
//                        });
//
//                        buttonDocAddPat = (Button)findViewById(R.id.btnDocAddPat);
//                        buttonDocAddPat.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent addPat = new Intent(DoctorPage.this, AddPatient.class);
//                                addPat.putExtra("DOCID", doc.getDocFirst_Name()+" "+doc.getDocLast_Name());
//                                addPat.putExtra("HOSPID",doc.getDocHosp_ID());
//                                startActivity(addPat);
//                            }
//                        });
//
//                        buttonDocAddMedRec = (Button)findViewById(R.id.btnDocAddMedRec);
//                        buttonDocAddMedRec.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                            Intent addRec = new Intent(DoctorPage.this, PatientListAddMedRecord.class);
//                            addRec.putExtra("DOCID", doc.getDocFirst_Name()+" "+doc.getDocLast_Name());
//                            startActivity(addRec);
//                            }
//                        });
//
//                        buttonDocMedRecList = (Button)findViewById(R.id.btnDocMedRecList);
//                        buttonDocMedRecList.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent pat_List = new Intent(DoctorPage.this, PatientListShowMedRecord.class);
//                                pat_List.putExtra("DOCID", doc.getDocFirst_Name()+" "+doc.getDocLast_Name());
//                                startActivity(pat_List);
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(DoctorPage.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    //user log out
    private void logOutDoctor(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(DoctorPage.this, MainActivity.class));
    }

    private void EditProfile(){
        startActivity(new Intent(DoctorPage.this,DoctorEditProfile.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doctor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOutDoctor:{
                logOutDoctor();
            }
            case R.id.doctorDetails:{
                EditProfile();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
