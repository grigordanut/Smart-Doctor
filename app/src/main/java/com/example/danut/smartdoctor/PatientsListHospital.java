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

public class PatientsListHospital extends AppCompatActivity {

    //Declare variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener patientDBEventListener;

    private ArrayList<String> patientListHosp;
    private ArrayAdapter<String> arrayAdapter;
    private ListView patientListViewHosp;

    private TextView tVPatListHosp;

    String hospital_Name;
    String hospital_Key;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list_hospital);

        tVPatListHosp = (TextView) findViewById(R.id.tvPatListHosp);

        getIntent().hasExtra("HOSPName");
        hospital_Name = getIntent().getExtras().getString("HOSPName");

        getIntent().hasExtra("HOSPKey");
        hospital_Key = getIntent().getExtras().getString("HOSPKey");

        patientListViewHosp = findViewById(R.id.listViewPatientsHosp);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Patients");

        patientListHosp = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.image_patient, R.id.tvPatientList, patientListHosp);
        patientListViewHosp.setAdapter(arrayAdapter);
    }

    public void onStart() {
        super.onStart();
        loadPatientData();
    }

    public void loadPatientData() {

        patientDBEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    patientListHosp.clear();
                    for (DataSnapshot dsPatHosp : dataSnapshot.getChildren()) {
                        Patients pat_Data = dsPatHosp.getValue(Patients.class);
                        assert pat_Data != null;
                        if (pat_Data.getPatient_HospKey().equals(hospital_Key)) {
                            tVPatListHosp.setText("Patients' list: " + hospital_Name + " hospital");
                            patientListHosp.add("First Name: " + pat_Data.getPatient_FirstName() + "\nLast Name: " + pat_Data.getPatient_LastName() + "\nUnique Code: " + pat_Data.getPatient_UniqueCode() + "\nDoctor: " + pat_Data.getPatient_DocName());
                        } else {
                            tVPatListHosp.setText("No Patients: " + hospital_Name + " hospital");
                        }
                    }
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    tVPatListHosp.setText("No Patients: " + hospital_Name + " hospital");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientsListHospital.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
