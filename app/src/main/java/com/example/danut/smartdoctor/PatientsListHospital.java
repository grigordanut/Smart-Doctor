package com.example.danut.smartdoctor;

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

public class PatientsListHospital extends AppCompatActivity {

    //Declare variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener patientDBEventListener;

    private ArrayList<String> patientListHosp;
    private ArrayAdapter<String> arrayAdapter;
    private ListView patientListViewHosp;

    private TextView textViewPatListHospital;

    String hospitalID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list_hospital);

        getIntent().hasExtra("HOSPKey");
        hospitalID = getIntent().getExtras().getString("HOSPID");

        textViewPatListHospital = (TextView)findViewById(R.id.tvPatListHosp);
        textViewPatListHospital.setText("Patient's list "+hospitalID);

        patientListViewHosp = (ListView) findViewById(R.id.listViewPatientsHosp);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Patients");
        patientListHosp = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.image_patient,R.id.tvPatientInfo,patientListHosp);

        patientDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsPatHosp : dataSnapshot.getChildren()){
                    Patient patHosp = dsPatHosp.getValue(Patient.class);
                    if(patHosp!=null){
                        if(patHosp.getPatHosp_ID().equals(hospitalID)) {
                            patientListHosp.add("First Name: "+patHosp.patFirst_Name+"\nLast Name: "+patHosp.patLast_Name+"\nUnique Code: "+patHosp.patUnique_Code+"\nDoctor: "+patHosp.patDoc_ID);
                        }
                    }
                    else{
                        patientListHosp.add("No patients");
                    }
                }
                patientListViewHosp.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
