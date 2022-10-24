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

public class PatientListShowMedRecord extends AppCompatActivity {

    //Declare variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener patientDBEventListener;

    private ArrayList<String> patListShowRec;
    private ArrayAdapter<String> arrayAdapter;
    private ListView patListViewShowRec;

    private TextView textViewDocShowRec;
    Patient pat;

    String doctorID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list_show_med_record);

        getIntent().hasExtra("DOCID");
        doctorID = getIntent().getExtras().getString("DOCID");

        textViewDocShowRec = (TextView)findViewById(R.id.tvDocShowMedRec);
        textViewDocShowRec.setText("Doctor "+doctorID);

        pat = new Patient();
        patListViewShowRec = (ListView) findViewById(R.id.listViewPatShowRec);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Patients");
        patListShowRec = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.patient_info,R.id.tvPatientInfo,patListShowRec);

        patientDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsPatAddRec : dataSnapshot.getChildren()){
                    final Patient patAddRec = dsPatAddRec.getValue(Patient.class);
                    if(patAddRec!=null){
                        if(patAddRec.getPatDoc_ID().equals(doctorID)) {
                            patListShowRec.add(patAddRec.patFirst_Name+" "+patAddRec.patLast_Name+" "+patAddRec.patUnique_Code);

                            patListViewShowRec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String pat_Name = patListShowRec.get(position);
                                    Intent intentPat = new Intent(PatientListShowMedRecord.this, MedicalRecordList.class);
                                    intentPat.putExtra("PATID", pat_Name);
                                    intentPat.putExtra("DOCID", patAddRec.getPatDoc_ID());
                                    startActivity(intentPat);
                                }
                            });
                        }
                    }
                    else{
                        patListShowRec.add("No patients");
                    }
                }
                patListViewShowRec.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
