package com.example.danut.smartdoctor;

import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class DoctorPatientsList extends AppCompatActivity {

    //Declare variables
    private DatabaseReference databaseReference;
    private ValueEventListener patientDBEventListener;

    private ArrayList<String> patientList;
    private ArrayAdapter<String> arrayAdapter;
    private ListView patientListView;

    private TextView tVHospPatList, tVDocPatList;

    private String patDoctor_KeyNew;

    private Button btn_NewPatient, btn_BackDoctor;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_patients_list);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Patients' list");

        tVHospPatList = findViewById(R.id.tvHospPatList);
        tVDocPatList = findViewById(R.id.tvDocPatListNew);

        btn_NewPatient = (Button) findViewById(R.id.btnNewPatient);
        btn_BackDoctor = findViewById(R.id.btnBackDoctor);

        getIntent().hasExtra("DOCKey");
        patDoctor_KeyNew = getIntent().getExtras().getString("DOCKey");

        patientListView = findViewById(R.id.listViewPatients);

        databaseReference = FirebaseDatabase.getInstance().getReference("Patients");

        patientList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.image_patient, R.id.tvPatientList, patientList);
        patientListView.setAdapter(arrayAdapter);

        patientDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    patientList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Patients pat_Data = postSnapshot.getValue(Patients.class);
                        assert pat_Data != null;
                        if (pat_Data.getPatient_DocKey().equals(patDoctor_KeyNew)) {

                            patientList.add(pat_Data.getPatient_FirstName() + " " + pat_Data.getPatient_LastName() + "  " + pat_Data.getPatient_UniqueCode());

                            tVHospPatList.setText(pat_Data.getPatient_HospName() + " Hospital");
                            tVDocPatList.setText("Patients of Doctor: " + pat_Data.getPatient_DocName());

                            //Action button new Patients
                            btn_NewPatient.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent new_Pat = new Intent(DoctorPatientsList.this, AddPatient.class);
                                    new_Pat.putExtra("HOSPName", pat_Data.getPatient_HospName());
                                    new_Pat.putExtra("HOSPKey", pat_Data.getPatient_HospKey());
                                    new_Pat.putExtra("DOCName", pat_Data.getPatient_DocName());
                                    new_Pat.putExtra("DOCKey", pat_Data.getPatient_DocKey());
                                    startActivity(new_Pat);
                                }
                            });

                        }
//                        else {
//                            tVDocPatList.setText("No patients have been added!!:Db exist");
//                        }
                    }
                    patientListView.setAdapter(arrayAdapter);
                } else {
                    tVDocPatList.setText("No patients have been added!!:Db exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorPatientsList.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        btn_BackDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(DoctorPatientsList.this, DoctorPage.class));
            }
        });
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        loadDoctorPatientData();
//    }
//
//    public void loadDoctorPatientData() {
//        patientDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    patientList.clear();
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        Patients pat_Data = postSnapshot.getValue(Patients.class);
//                        assert pat_Data != null;
//                        if (pat_Data.getPatient_DocKey().equals(patDoctor_KeyNew)) {
//
//                            patientList.add(pat_Data.getPatient_FirstName() + " " + pat_Data.getPatient_LastName() + "  " + pat_Data.getPatient_UniqueCode());
//
//                            tVHospPatList.setText(pat_Data.getPatient_HospName() + " Hospital");
//                            tVDocPatList.setText("Patients of Doctor: "+ pat_Data.getPatient_DocName());
//
//                            //Action button new Patients
//                            btn_NewPatient.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent new_Pat = new Intent(DoctorPatientsList.this, AddPatient.class);
//                                    new_Pat.putExtra("HOSPName", pat_Data.getPatient_HospName());
//                                    new_Pat.putExtra("HOSPKey", pat_Data.getPatient_HospKey());
//                                    new_Pat.putExtra("DOCName", pat_Data.getPatient_DocName());
//                                    new_Pat.putExtra("DOCKey", pat_Data.getPatient_DocKey());
//                                    startActivity(new_Pat);
//                                }
//                            });
//
//                        }
////                        else {
////                            //patientList.add("No patients have been added");
////                            tVDocPatList.setText("No patients have been added!!:Db exist");
////                        }
//                    }
//                    arrayAdapter.notifyDataSetChanged();
//
//                } else {
//                    tVDocPatList.setText("No patients have been added:db Not exist");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(DoctorPatientsList.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
