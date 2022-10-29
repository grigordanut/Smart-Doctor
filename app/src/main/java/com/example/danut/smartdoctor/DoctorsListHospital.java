package com.example.danut.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

public class DoctorsListHospital extends AppCompatActivity {

    //Declare variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener doctorDBEventListener;

    private ArrayList<String> doctorList;
    private ArrayAdapter<String> arrayAdapter;
    private ListView doctorListView;

    private TextView tVHospDocList;

    private String hospital_Name = "";
    private String hospital_Key = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_list_hospital);

        Objects.requireNonNull(getSupportActionBar()).setTitle("HOSPITALS: main page");

        tVHospDocList = findViewById(R.id.tvHospDocList);

        doctorListView = findViewById(R.id.listViewDoctors);

        getIntent().hasExtra("HOSPName");
        hospital_Name = getIntent().getExtras().getString("HOSPName");

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
                        Doctors doc = dsDoc.getValue(Doctors.class);
                        assert doc != null;
                        if (doc.getDoctor_HospKey().equals(hospital_Key)) {
                            doctorList.add(doc.getDoctor_FirstName() + " " + doc.getDoctor_LastName());
                            tVHospDocList.setText("Doctors' list: " + hospital_Name + " Hospital.");
                        } else {
                            tVHospDocList.setText("No doctors: " + hospital_Name + " hospital.");
                        }
                    }
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    tVHospDocList.setText("No doctors: " + hospital_Name + " hospital.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorsListHospital.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
