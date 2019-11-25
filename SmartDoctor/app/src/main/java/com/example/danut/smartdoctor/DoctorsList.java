package com.example.danut.smartdoctor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorsList extends AppCompatActivity {

    //Declare variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener doctorDBEventListener;

    private ArrayList<String> doctorList;
    private ArrayAdapter<String> arrayAdapter;
    private ListView doctorListView;

    private TextView textViewHospDocList;
    Doctor doc;

    String hospitalID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_list);

        getIntent().hasExtra("HOSPID");
        hospitalID = getIntent().getExtras().getString("HOSPID");

        doc = new Doctor();

        textViewHospDocList = (TextView)findViewById(R.id.tvHospDocList);
        doctorListView = (ListView)findViewById(R.id.listViewDoctors);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Doctors");
        doctorList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.doctor_info,R.id.tvDoctorInfo,doctorList);

        doctorDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorList.clear();
                for (DataSnapshot dsDoc: dataSnapshot.getChildren()){
                    Doctor doc = dsDoc.getValue(Doctor.class);
                    if (doc != null){
                        if(doc.getDocHosp_ID().equals(hospitalID)){
                            doc.setDoctorKey(dsDoc.getKey());
                            textViewHospDocList.setText("Doctor's List "+hospitalID);
                            doctorList.add(doc.docFirst_Name+" "+doc.docLast_Name);
                        }
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
