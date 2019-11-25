package com.example.danut.smartdoctor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HospitalsList extends AppCompatActivity {

    //Declare variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<String> hospitalList;
    private ArrayAdapter<String> arrayAdapter;
    private ListView hospListView;

    Hospital hosp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitals_list);

        hosp = new Hospital();

        hospListView = (ListView)findViewById(R.id.listViewHospitals);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Hospitals");
        hospitalList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.hospital_info,R.id.tvHospitalInfo,hospitalList);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hospitalList.clear();
                for (DataSnapshot dsHosp: dataSnapshot.getChildren()){
                    Hospital hosp = dsHosp.getValue(Hospital.class);
                    hospitalList.add(hosp.hosp_Name+" Hospital");
                }
                hospListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        hospListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String hospital_Name = hospitalList.get(position);
                Intent intent = new Intent(HospitalsList.this, DoctorsList.class);
                intent.putExtra("HOSPID", hospital_Name);
                startActivity(intent);
            }
        });
    }
}
