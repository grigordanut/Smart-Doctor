package com.example.danut.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

public class HospitalDoctorsList extends AppCompatActivity {

    //Declare variables
    private DatabaseReference databaseReference;
    private ValueEventListener doctorDBEventListener;

    private ArrayList<String> doctorList;
    private ArrayAdapter<String> arrayAdapter;
    private ListView doctorListView;

    private TextView tVHospDocList;

    private String hospital_Key = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_doctors_list);

        Objects.requireNonNull(getSupportActionBar()).setTitle("HOSPITAL: Doctors' list");

        tVHospDocList = findViewById(R.id.tvHospDocList);

        doctorListView = findViewById(R.id.listViewDoctors);
        tVHospDocList.setText("No Doctors have been added!!");

        getIntent().hasExtra("HOSPKey");
        hospital_Key = getIntent().getExtras().getString("HOSPKey");

        //retrieve data from Doctors database
        databaseReference = FirebaseDatabase.getInstance().getReference("Doctors");

        doctorList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.image_doctor, R.id.tvDoctorName, doctorList);
        doctorListView.setAdapter(arrayAdapter);
    }

    public void onStart() {
        super.onStart();
        loadDoctorData();
    }

    public void loadDoctorData() {

        doctorDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    doctorList.clear();
                    for (DataSnapshot dsDoc : dataSnapshot.getChildren()) {
                        Doctors doc_Data = dsDoc.getValue(Doctors.class);
                        assert doc_Data != null;
                        if (doc_Data.getDoctor_HospKey().equals(hospital_Key)) {
                            doctorList.add(doc_Data.getDoctor_FirstName() + " " + doc_Data.getDoctor_LastName());
                            tVHospDocList.setText("Doctors' list: " + doc_Data.getDoctor_HospName() + " Hospital.");
                        }
                    }
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    tVHospDocList.setText("No Doctors have been added!!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalDoctorsList.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
