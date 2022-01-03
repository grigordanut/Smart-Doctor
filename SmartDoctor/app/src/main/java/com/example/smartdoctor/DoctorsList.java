package com.example.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorsList extends AppCompatActivity {

    //Declare variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener doctorDBEventListener;

    private ArrayList<String> doctorList;
    private ArrayAdapter<String> arrayAdapter;
    private ListView doctorListView;

    private TextView tVHospNameDoctorList;

    private String hospital_Name = "";
    private String hospital_Key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_list);

        getIntent().hasExtra("HOSPName");
        hospital_Name = getIntent().getExtras().getString("HOSPName");

        tVHospNameDoctorList = findViewById(R.id.tvHospNameDoctorList);
        tVHospNameDoctorList.setText("Doctor's list for: " + hospital_Name);

        getIntent().hasExtra("HOSPKey");
        hospital_Key = getIntent().getExtras().getString("HOSPKey");


        doctorListView = (ListView)findViewById(R.id.listViewDoctors);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Doctors");
        doctorList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.image_doctor,R.id.tvShowDoctorName,doctorList);

        doctorDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Doctor doctor_data = postSnapshot.getValue(Doctor.class);

                    assert doctor_data != null;
                    if(doctor_data.getDocHosp_Key().equals(hospital_Key)){
                        doctor_data.setDoc_Key(postSnapshot.getKey());
                        doctorList.add(doctor_data.getDocFirst_Name()+" "+ doctor_data.docLast_Name);
                    }
                }
                doctorListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}