package com.example.danut.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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
import java.util.List;

public class PatientsListShowMedRecord extends AppCompatActivity implements PatientsAdapter.OnItemClickListener {

    //Declare variables
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    private List<Patients> patListShowRec;
    private PatientsAdapter patientsAdapter;
    private RecyclerView recyclerViewPatShowRec;

    private TextView tVMedRecShowDocName, tVMedRecPatList;

    private String patDoctor_Name = "";
    private String patDoctor_Key = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list_show_med_record);

        patListShowRec = new ArrayList<>();

        tVMedRecShowDocName = findViewById(R.id.tvMedRecShowDocName);
        tVMedRecPatList = findViewById(R.id.tvMedRecPatList);

        getIntent().hasExtra("DOCName");
        patDoctor_Name = getIntent().getExtras().getString("DOCName");

        getIntent().hasExtra("DOCKey");
        patDoctor_Key = getIntent().getExtras().getString("DOCKey");

//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            doctor_Name = bundle.getString("DOCName");
//            doctor_Key = bundle.getString("DOCKey");
//        }

        tVMedRecShowDocName.setText("Doctor: " + patDoctor_Name);

        recyclerViewPatShowRec = findViewById(R.id.recViewPatShowRec);
        recyclerViewPatShowRec.setHasFixedSize(true);
        recyclerViewPatShowRec.setLayoutManager(new LinearLayoutManager(this));

        patientsAdapter = new PatientsAdapter(PatientsListShowMedRecord.this, patListShowRec);
        recyclerViewPatShowRec.setAdapter(patientsAdapter);
        patientsAdapter.setOnItmClickListener(PatientsListShowMedRecord.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPatientDataMedRec();
    }

    private void loadPatientDataMedRec() {

        databaseReference = FirebaseDatabase.getInstance().getReference("Patients");

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    patListShowRec.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Patients pat_Data = postSnapshot.getValue(Patients.class);
                        assert pat_Data != null;
                        if (pat_Data.getPatient_DocKey().equals(patDoctor_Key)) {
                            pat_Data.setPatient_Key(postSnapshot.getKey());
                            patListShowRec.add(pat_Data);
                            tVMedRecPatList.setText("Select your Patient");
                        }
                    }
                    patientsAdapter.notifyDataSetChanged();
                } else {
                    tVMedRecPatList.setText("No Patients have been added!!:db not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientsListShowMedRecord.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Patients selected_Pat = patListShowRec.get(position);

        Intent intent_ShowRec = new Intent(PatientsListShowMedRecord.this, DoctorMedicalRecordList.class);
        intent_ShowRec.putExtra("DOCName", selected_Pat.getPatient_DocName());
        intent_ShowRec.putExtra("PATName", selected_Pat.getPatient_FirstName() + " " + selected_Pat.getPatient_LastName());
        intent_ShowRec.putExtra("PATKey", selected_Pat.getPatient_Key());
        startActivity(intent_ShowRec);
        patListShowRec.clear();
    }
}