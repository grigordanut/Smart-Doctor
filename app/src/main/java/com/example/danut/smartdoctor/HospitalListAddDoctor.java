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

public class HospitalListAddDoctor extends AppCompatActivity {

    //Declare variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<String> hospListAddDoc;
    private ArrayAdapter<String> arrayAdapter;
    private ListView hospListViewAddDoc;

    private TextView textViewHospListAddDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_list_add_doctor);

        textViewHospListAddDoc = (TextView)findViewById(R.id.tvHospListAddDoc);

        hospListViewAddDoc = (ListView)findViewById(R.id.listViewHosListAddDoc);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Hospitals");
        hospListAddDoc = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.image_hospital,R.id.tvHospitalInfo,hospListAddDoc);
        databaseReference .addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hospListAddDoc.clear();
                for (DataSnapshot dsHosp: dataSnapshot.getChildren()){
                    Hospitals hospitals = dsHosp.getValue(Hospitals.class);
                    assert hospitals != null;
                    hospListAddDoc.add(hospitals.hosp_Name+" Hospital");
                    //textViewHospListAddDoc.setText("Select your Hospital");
                }
                hospListViewAddDoc.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalListAddDoctor.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        hospListViewAddDoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String hosp_Name = hospListAddDoc.get(position);
                Intent intent = new Intent(HospitalListAddDoctor.this, DoctorRegister.class);
                intent.putExtra("HOSPID", hosp_Name);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void onStart(){
        super.onStart();
        if(hospListViewAddDoc==null || arrayAdapter.getCount()== 0){
            textViewHospListAddDoc.setText("No Hospitals; Click to add Hospital");
            textViewHospListAddDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HospitalListAddDoctor.this, HospitalRegistration.class));
                }
            });
        }

        else{
            textViewHospListAddDoc.setText("Select your Hospital");
        }
    }
}

