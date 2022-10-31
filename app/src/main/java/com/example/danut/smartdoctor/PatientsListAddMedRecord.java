package com.example.danut.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatientsListAddMedRecord extends AppCompatActivity implements PatientsAdapter.OnItemClickListener {

    //Declare variables
    private DatabaseReference dbReferenceLoadPat;
    private ValueEventListener eventListenerPat;

    private List<Patients> patListAddMedRec;
    private PatientsAdapter patientsAdapter;
    private RecyclerView recyclerViewPatAddRec;

    private TextView tVDocListAddRec, tVPatListAddRec;

    private String patDoctor_Name;
    private String patDoctor_Key;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list_add_med_record);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Patients' list add Med Record");

        tVDocListAddRec = findViewById(R.id.tvDocListAddRec);
        tVPatListAddRec = findViewById(R.id.tvPatListAddRec);

        getIntent().hasExtra("DOCName");
        patDoctor_Name = getIntent().getExtras().getString("DOCName");

        getIntent().hasExtra("DOCKey");
        patDoctor_Key = getIntent().getExtras().getString("DOCKey");

        tVDocListAddRec.setText("Doctor: " + patDoctor_Name);

        dbReferenceLoadPat = FirebaseDatabase.getInstance().getReference("Patients");

        patListAddMedRec = new ArrayList<>();

        recyclerViewPatAddRec = findViewById(R.id.recViewPatAddRec);
        recyclerViewPatAddRec.setHasFixedSize(true);
        recyclerViewPatAddRec.setLayoutManager(new LinearLayoutManager(this));

        patientsAdapter = new PatientsAdapter(PatientsListAddMedRecord.this, patListAddMedRec);
        recyclerViewPatAddRec.setAdapter(patientsAdapter);
        patientsAdapter.setOnItmClickListener(PatientsListAddMedRecord.this);
    }

    public void onStart() {
        super.onStart();
        loadPatientData();
    }

    public void loadPatientData() {

        eventListenerPat = dbReferenceLoadPat.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    patListAddMedRec.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Patients pat_Data = postSnapshot.getValue(Patients.class);
                        assert pat_Data != null;
                        if (pat_Data.getPatient_DocKey().equals(patDoctor_Key)) {
                            pat_Data.setPatient_Key(postSnapshot.getKey());
                            patListAddMedRec.add(pat_Data);
                            tVPatListAddRec.setText("Select your Patient");
                        }
                    }
                    patientsAdapter.notifyDataSetChanged();
                } else {
                    tVPatListAddRec.setText("No Patients have been added!!:db not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientsListAddMedRecord.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position){

        Patients selected_Pat = patListAddMedRec.get(position);

        Intent intent_AddRec = new Intent(PatientsListAddMedRecord.this,AddMedicalRecord.class);
        intent_AddRec.putExtra("PATName", selected_Pat.getPatient_FirstName()+ " " + selected_Pat.getPatient_LastName());
        intent_AddRec.putExtra("PATKey", selected_Pat.getPatient_Key());
        startActivity(intent_AddRec);
    }
}
