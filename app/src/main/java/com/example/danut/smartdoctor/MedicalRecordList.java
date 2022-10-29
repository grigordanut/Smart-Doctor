package com.example.danut.smartdoctor;

import android.content.DialogInterface;
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

public class MedicalRecordList extends AppCompatActivity implements MedicalRecordAdapter.OnItemClickListener{

    //Declare variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseRefMedRecord;
    private ValueEventListener medRecDBEventListener;

    String patientID = "";
    String doctorID ="";

    private RecyclerView recyclerView;
    private MedicalRecordAdapter medRecAdapter;

    public List<MedicalRecord> medRecList;

    private TextView textViewDocMedRecList, textViewPatMedRecList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_record_list);

        //initialize variables
        firebaseDatabase = FirebaseDatabase.getInstance();

        getIntent().hasExtra("PATID");
        patientID = getIntent().getExtras().getString("PATID");

        //Set textViews
        textViewPatMedRecList = (TextView) findViewById(R.id.tvPatMedRecordList);
        textViewPatMedRecList.setText("Patients "+patientID);

        getIntent().hasExtra("DOCID");
        doctorID = getIntent().getExtras().getString("DOCID");

        textViewDocMedRecList = (TextView)findViewById(R.id.tvDocMedRecordList);
        textViewDocMedRecList.setText("Doctors "+doctorID);

        medRecList = new ArrayList<MedicalRecord>();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //check if the menu list is empty and add a new menu
        if(databaseRefMedRecord == null){
            databaseRefMedRecord = FirebaseDatabase.getInstance().getReference().child("Record");
        }

        medRecDBEventListener = databaseRefMedRecord.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                medRecList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    MedicalRecord medRecord = postSnapshot.getValue(MedicalRecord.class);
                    if(medRecord != null) {
                        if( medRecord.getRecordPat_ID().equals(patientID)) {
                            medRecord.setRecordKey(postSnapshot.getKey());
                            medRecList.add(medRecord);

                        }
                    }
                }

                medRecAdapter = new MedicalRecordAdapter(MedicalRecordList.this,medRecList);
                recyclerView.setAdapter(medRecAdapter);
                medRecAdapter.setOnItmClickListener(MedicalRecordList .this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MedicalRecordList.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Press long click to show more action: ",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateClick(int position) {
        Toast.makeText(this, "Update click at position: ",Toast.LENGTH_SHORT).show();
    }

    //Action of the menu Delete and alert dialog
    @Override
    public void onDeleteClick(final int position) {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(MedicalRecordList.this);
        builderAlert.setMessage("Are sure to delete this item?");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MedicalRecord selectedMedRec = medRecList.get(position);
                        final String selectedKey = selectedMedRec.getRecordKey();

                        databaseRefMedRecord.child(selectedKey).removeValue();
                        Toast.makeText(MedicalRecordList.this, "The item has been deleted successfully ", Toast.LENGTH_SHORT).show();
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
    protected void onDestroy(){
        super.onDestroy();
        databaseRefMedRecord.removeEventListener(medRecDBEventListener);
    }
}
