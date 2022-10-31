package com.example.danut.smartdoctor;

import android.annotation.SuppressLint;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorMedicalRecordList extends AppCompatActivity implements MedicalRecordAdapter.OnItemClickListener {

    //Declare variables
    private DatabaseReference databaseRefMedRecord;
    private ValueEventListener medRecDBEventListener;

    private String doctor_Name = "";
    private String patient_Name = "";
    private String patient_Key = "";

    private RecyclerView recyclerView;
    private MedicalRecordAdapter medRecAdapter;

    public List<MedicalRecord> medRecList;

    private TextView textViewDocMedRecList, textViewPatMedRecList;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_medical_record_list);

        textViewDocMedRecList = (TextView) findViewById(R.id.tvDocMedRecordList);
        textViewPatMedRecList = (TextView) findViewById(R.id.tvPatMedRecordList);

        getIntent().hasExtra("DOCName");
        doctor_Name = getIntent().getExtras().getString("DOCName");

        getIntent().hasExtra("PATName");
        patient_Name = getIntent().getExtras().getString("PATName");

        getIntent().hasExtra("PATKey");
        patient_Key = getIntent().getExtras().getString("PATKey");

        //Set textViews
        textViewDocMedRecList.setText("Doctor: " + doctor_Name);
        textViewPatMedRecList.setText("Med Record Patient: " + patient_Name);

        medRecList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseRefMedRecord = FirebaseDatabase.getInstance().getReference().child("Medical Record");

        medRecDBEventListener = databaseRefMedRecord.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medRecList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    MedicalRecord medRecord = postSnapshot.getValue(MedicalRecord.class);
                    assert medRecord != null;
                    if (medRecord.getMedRecord_PatKey().equals(patient_Key)) {
                        medRecord.setMedRecord_Key(postSnapshot.getKey());
                        medRecList.add(medRecord);
                    }
                }

                medRecAdapter = new MedicalRecordAdapter(DoctorMedicalRecordList.this, medRecList);
                recyclerView.setAdapter(medRecAdapter);
                medRecAdapter.setOnItmClickListener(DoctorMedicalRecordList.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorMedicalRecordList.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Press long click to show more action: ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateClick(int position) {
        Toast.makeText(this, "Update click at position: ", Toast.LENGTH_SHORT).show();
    }

    //Action of the menu Delete and alert dialog
    @Override
    public void onDeleteClick(final int position) {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(DoctorMedicalRecordList.this);
        builderAlert.setMessage("Are sure to delete this item?");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MedicalRecord selectedMedRec = medRecList.get(position);
                        final String selectedKey = selectedMedRec.getMedRecord_Key();

                        databaseRefMedRecord.child(selectedKey).removeValue();
                        Toast.makeText(DoctorMedicalRecordList.this, "The item has been deleted successfully ", Toast.LENGTH_SHORT).show();
                    }
                });

        builderAlert.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert1 = builderAlert.create();
        alert1.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseRefMedRecord.removeEventListener(medRecDBEventListener);
    }
}
