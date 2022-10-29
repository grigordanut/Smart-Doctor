package com.example.danut.smartdoctor;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PatientsList extends AppCompatActivity {

    //Declare variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener patientDBEventListener;

    private ArrayList<String> patientList;
    private ArrayAdapter<String> arrayAdapter;
    private ListView patientListView;

    private TextView textViewDocPatList, textViewHospPatList;
    Patient pat;

    String doctorID = "";
    String hospitalID ="";

    private Button buttonNewPatient, buttonBackDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list);

        getIntent().hasExtra("DOCID");
        doctorID = getIntent().getExtras().getString("DOCID");

        textViewDocPatList = (TextView) findViewById(R.id.tvDocPatList);
        textViewDocPatList.setText("Doctors "+doctorID);

        getIntent().hasExtra("HOSPID");
        hospitalID = getIntent().getExtras().getString("HOSPID");

        textViewHospPatList = (TextView)findViewById(R.id.tvHospPatList);
        textViewHospPatList.setText(hospitalID);

        pat = new Patient();
        patientListView = (ListView) findViewById(R.id.listViewPatients);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Patients");
        patientList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this , R.layout.image_patient,R.id.tvPatientInfo,patientList);

        patientDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patientList.clear();
                for (DataSnapshot dsPat : dataSnapshot.getChildren()){
                    Patient pat = dsPat.getValue(Patient.class);
                    if (pat!=null){
                        if (pat.getPatDoc_ID().equals(doctorID)){
                            patientList.add(pat.patFirst_Name+" "+pat.patLast_Name+"  "+pat.patUnique_Code);
                        }
                    }

                    else {
                        patientList.add(" No patients");
                    }
                }
                patientListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Action button new Patient
        buttonNewPatient = (Button) findViewById(R.id.btnNewPatient);
        buttonNewPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPat = new Intent(PatientsList.this, AddPatient.class);
                newPat.putExtra("DOCID",doctorID);
                newPat.putExtra("HOSPID",hospitalID);
                startActivity(newPat);
            }
        });

        buttonBackDoctor = (Button)findViewById(R.id.btnBackDoctor);
        buttonBackDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(PatientsList.this, DoctorPage.class));
            }
        });
    }
}
