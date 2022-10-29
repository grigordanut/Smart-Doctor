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
import java.util.Objects;

public class PatientsList extends AppCompatActivity {

    //Declare variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener patientDBEventListener;

    private ArrayList<String> patientList;
    private ArrayAdapter<String> arrayAdapter;
    private ListView patientListView;

    private TextView textViewDocPatList, textViewHospPatList;

    private String patHosp_Name;
    private String patHosp_Key;

    private String patDoctor_Name;
    private String patDoctor_Key;

    private Button buttonNewPatient, buttonBackDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Patients' list");

        textViewHospPatList = findViewById(R.id.tvHospPatList);
        textViewDocPatList = findViewById(R.id.tvDocPatList);

        getIntent().hasExtra("HOSPName");
        patHosp_Name = getIntent().getExtras().getString("HOSPName");

        getIntent().hasExtra("HOSPKey");
        patHosp_Key = getIntent().getExtras().getString("HOSPKey");
        textViewHospPatList.setText(patHosp_Name + " Hospital");

        getIntent().hasExtra("DOCName");
        patDoctor_Name = getIntent().getExtras().getString("DOCName");

        getIntent().hasExtra("DOCKey");
        patDoctor_Key = getIntent().getExtras().getString("DOCKey");
        textViewDocPatList.setText("Doctor: "+patDoctor_Name);

        patientListView = findViewById(R.id.listViewPatients);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Patients");
        patientList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this , R.layout.image_patient,R.id.tvPatientList,patientList);

        patientDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patientList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Patients pat_Data = postSnapshot.getValue(Patients.class);
                    assert pat_Data != null;
                        if (pat_Data.getPatient_DocKey().equals(patDoctor_Key)){
                            patientList.add(pat_Data.getPatient_FirstName()+" "+pat_Data.getPatient_LastName()+"  "+pat_Data.getPatient_UniqueCode());
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

//        //Action button new Patients
//        buttonNewPatient = (Button) findViewById(R.id.btnNewPatient);
//        buttonNewPatient.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent newPat = new Intent(PatientsList.this, AddPatient.class);
//                newPat.putExtra("DOCID",doctorID);
//                newPat.putExtra("HOSPID",hospitalID);
//                startActivity(newPat);
//            }
//        });

        buttonBackDoctor = findViewById(R.id.btnBackDoctor);
        buttonBackDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(PatientsList.this, DoctorPage.class));
            }
        });
    }
}
