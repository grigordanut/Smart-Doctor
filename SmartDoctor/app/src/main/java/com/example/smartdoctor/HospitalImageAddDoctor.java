package com.example.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HospitalImageAddDoctor extends AppCompatActivity {

    private DatabaseReference hospitalDatabaseReference;
    private ValueEventListener hospitalEventListener;

    private RecyclerView hospitalRecyclerView;
    private HospitalAdapterAddDoctor hospitalAdapterAddDoctor;

    private List<Hospital> hospitalList;

    private TextView tVHospListAddDoctor;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_image_add_doctor);

        hospitalRecyclerView = findViewById(R.id.hospRecyclerView);
        hospitalRecyclerView.setHasFixedSize(true);
        hospitalRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        hospitalList = new ArrayList<>();

        tVHospListAddDoctor = findViewById(R.id.tvHospListAddDoctor);
        tVHospListAddDoctor.setText("No Hospitals; Click to add Hospital");
        tVHospListAddDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HospitalImageAddDoctor.this, HospitalRegistration.class));
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.show();
    }

    public void onStart(){
        super.onStart();
        loadHospitalData();
    }

    private void loadHospitalData(){

        //initialize the Hospital database
        hospitalDatabaseReference = FirebaseDatabase.getInstance().getReference("Hospitals");

        hospitalEventListener = hospitalDatabaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hospitalList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Hospital hospital = postSnapshot.getValue(Hospital.class);
                    assert hospital != null;
                    hospital.setHosp_Key(postSnapshot.getKey());
                    hospitalList.add(hospital);
                    tVHospListAddDoctor.setText("Select your Hospital");
                    tVHospListAddDoctor.setEnabled(false);
                }
                hospitalAdapterAddDoctor = new HospitalAdapterAddDoctor(HospitalImageAddDoctor.this, hospitalList);
                hospitalRecyclerView.setAdapter(hospitalAdapterAddDoctor);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalImageAddDoctor.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}