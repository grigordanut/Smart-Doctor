package com.example.danut.smartdoctor;

import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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

public class HospitalPage extends AppCompatActivity {

    //Declare variables
    private TextView tVWelcomeHospital, tVHospitalKey;

    private Button buttonHospPatList, buttonHospDocList, buttonHospAddDoc;

    //Access customer database
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    Hospitals userHosp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_page);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //retrieve data from Hospitals database
        databaseReference = FirebaseDatabase.getInstance().getReference("Hospitals");

        //initialise the variables
        tVWelcomeHospital = findViewById(R.id.tvWelcomeHospital);
        tVHospitalKey = findViewById(R.id.tvHospitalKey);

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    userHosp = postSnapshot.getValue(Hospitals.class);

                    assert userHosp != null;
                    assert firebaseUser != null;
                    if (firebaseUser.getUid().equals(postSnapshot.getKey())) {
                        tVWelcomeHospital.setText("Welcome to "+userHosp.getHosp_Name() +" Hospital");
                        tVHospitalKey.setText(firebaseUser.getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalPage.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        buttonHospAddDoc = findViewById(R.id.btnHospAddDoc);
        buttonHospAddDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                assert firebaseUser != null;
                Intent addEvent = new Intent(HospitalPage.this, DoctorRegistration.class);
                addEvent.putExtra("HOSPName", userHosp.getHosp_Name());
                addEvent.putExtra("HOSPKey", firebaseUser.getUid());
                startActivity(addEvent);
            }
        });

        buttonHospDocList = (Button)findViewById(R.id.btnHospDocList);
        buttonHospDocList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HospitalPage.this, DoctorsList.class);
                intent.putExtra("HOSPID",userHosp.getHosp_Name()+" Hospitals");
                startActivity(intent);
            }
        });

        buttonHospPatList = (Button)findViewById(R.id.btnHospPatList);
        buttonHospPatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HospitalPage.this,PatientsListHospital.class);
                intent.putExtra("HOSPKey",userHosp.getHosp_Name()+" Hospitals");
                startActivity(intent);
            }
        });
    }
}
