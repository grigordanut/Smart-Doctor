package com.example.danut.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MedicalRecordPatient extends AppCompatActivity {

    //Declare variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseRefMedRecord;

    String patientID = "";
    String doctorID = "";

    private TextView welcomePatMedRec, medRecGender, medRecDateBirth, medRecPPsNo, medRecAddress, medRecPatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_record_patient);

        getIntent().hasExtra("PATID");
        patientID = getIntent().getExtras().getString("PATID");

        getIntent().hasExtra("DOCID");
        doctorID = getIntent().getExtras().getString("DOCID");

        welcomePatMedRec = findViewById(R.id.tvWelcomePatMedRec);
        welcomePatMedRec.setText("Doctors "+doctorID);

        medRecGender = findViewById(R.id.tvMedRecGender);
        medRecDateBirth = findViewById(R.id.tvMedRecDateBirth);
        medRecPPsNo = findViewById(R.id.tvMedRecPPsNo);
        medRecAddress = findViewById(R.id.tvMedRecAddress);
        medRecPatName = findViewById(R.id.tvMedRecPatName);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRefMedRecord = FirebaseDatabase.getInstance().getReference("Record");

        databaseRefMedRecord.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    MedicalRecord med_Rec = postSnapshot.getValue(MedicalRecord.class);
                    if(med_Rec != null) {
                        if(med_Rec.getRecordPat_ID().equals(patientID)) {
                            med_Rec.setRecordKey(postSnapshot.getKey());
                            medRecGender.setText(med_Rec.medRecord_Gender);
                            medRecDateBirth.setText(med_Rec.medRecord_DateBirth);
                            medRecPPsNo.setText(med_Rec.medRecord_PPS);
                            medRecAddress.setText(med_Rec.medRecord_Address);
                            medRecPatName.setText(med_Rec.recordPat_ID);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MedicalRecordPatient.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
