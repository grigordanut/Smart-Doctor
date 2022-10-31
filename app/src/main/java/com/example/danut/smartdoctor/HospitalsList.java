package com.example.danut.smartdoctor;

import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class HospitalsList extends AppCompatActivity {

    //Declare variables
    private DatabaseReference databaseReference;
    private ArrayList<String> hospitalList;
    private ArrayAdapter<String> arrayAdapter;
    private ListView hospListView;

    private TextView tVHospList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitals_list);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Hospitals' List");

        tVHospList = findViewById(R.id.tvHospList);

        hospListView = findViewById(R.id.listViewHospitals);

        databaseReference = FirebaseDatabase.getInstance().getReference("Hospitals");

        hospitalList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.image_hospital, R.id.tvHospitalName, hospitalList);
        hospListView.setAdapter(arrayAdapter);
    }

    public void onStart() {
        super.onStart();
        loadHospitalsList();
    }

    public void loadHospitalsList() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    hospitalList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Hospitals hosp_Data = postSnapshot.getValue(Hospitals.class);
                        assert hosp_Data != null;
                        hospitalList.add(hosp_Data.getHosp_Name() + " Hospital");
                    }
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    tVHospList.setText("No Hospital have been added!!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalsList.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
