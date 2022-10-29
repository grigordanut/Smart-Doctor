package com.example.danut.smartdoctor;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PatientListAddMedRecord extends AppCompatActivity {

    //Declare variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener patientDBEventListener;

    private ArrayList<String> patListAddRec;
    private ArrayAdapter<String> arrayAdapter;
    private ListView patListViewAddRec;

    private TextView textViewDoctor;
    Patient pat;

    String doctorID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list_add_med_record);

        getIntent().hasExtra("DOCID");
        doctorID = getIntent().getExtras().getString("DOCID");

        textViewDoctor = (TextView)findViewById(R.id.tvDoctor);
        textViewDoctor.setText("Doctors "+doctorID);

        pat = new Patient();
        patListViewAddRec = (ListView) findViewById(R.id.listViewPatAddRec);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Patients");
        patListAddRec = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.image_patient,R.id.tvPatientInfo,patListAddRec);

        patientDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsPatAddRec : dataSnapshot.getChildren()){
                    Patient patAddRec = dsPatAddRec.getValue(Patient.class);
                    if(patAddRec!=null){
                        if(patAddRec.getPatDoc_ID().equals(doctorID)) {
                            patListAddRec.add(patAddRec.patFirst_Name+" "+patAddRec.patLast_Name+" "+patAddRec.patUnique_Code);
                        }
                    }
                    else{
                        patListAddRec.add("No patients");
                    }
                }
                patListViewAddRec.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        patListViewAddRec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String patient_Name = patListAddRec.get(position);
                Intent intent = new Intent(PatientListAddMedRecord.this, AddMedicalRecord.class);
                intent.putExtra("PATID", patient_Name);
                startActivity(intent);
            }
        });
    }
}
