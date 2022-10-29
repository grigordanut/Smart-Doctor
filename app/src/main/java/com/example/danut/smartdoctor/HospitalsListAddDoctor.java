package com.example.danut.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class HospitalsListAddDoctor extends AppCompatActivity {

    //Declare variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    //Retrieve data from Hospitals database
    private DatabaseReference dbReferenceLoadHosp;
    private ValueEventListener eventListenerHosp;

    private DatabaseReference dbReferenceHosp;

    private ArrayList<String> hospListAddDoc;
    private ArrayAdapter<String> arrayAdapter;
    private ListView hospListViewAddDoc;

    private TextView tVHospListAddDoc;

    private Hospitals hosp_Data;

    private String hospital_Name;
    private String hospital_Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitals_list_add_doctor);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Hospitals list add Doctor");


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //retrieve data from database into text views
        dbReferenceHosp = FirebaseDatabase.getInstance().getReference("Hospitals");

        dbReferenceLoadHosp = FirebaseDatabase.getInstance().getReference("Hospitals");

        tVHospListAddDoc = findViewById(R.id.tvHospListAddDoc);

        hospListViewAddDoc = findViewById(R.id.listViewHosListAddDoc);

        hospListAddDoc = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.image_hospital, R.id.tvHospitalName, hospListAddDoc);
        hospListViewAddDoc.setAdapter(arrayAdapter);

        hospListViewAddDoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                hospital_Name = hospListAddDoc.get(position);

                Query query = dbReferenceHosp.orderByChild("hosp_Name").equalTo(hospital_Name);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            postSnapshot.getKey();
                            hospital_Key = postSnapshot.getKey();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(HospitalsListAddDoctor.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HospitalsListAddDoctor.this);

                alertDialogBuilder
                        .setTitle("You selected: " + hospital_Name + " Hospital.")
                        .setMessage("Press OK to select this Hospital.\nPress Cancel to delete selection.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(HospitalsListAddDoctor.this, DoctorRegistration.class);
                                intent.putExtra("HOSPName", hospital_Name);
                                intent.putExtra("HOSPKey", hospital_Key);
                                startActivity(intent);
                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
    }

    public void onStart() {
        super.onStart();
        loadHospitalData();
    }

    public void loadHospitalData() {

        eventListenerHosp = dbReferenceLoadHosp.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hospListAddDoc.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    hosp_Data = postSnapshot.getValue(Hospitals.class);
                    assert hosp_Data != null;
                    tVHospListAddDoc.setText("Select your Hospital");
                    hospListAddDoc.add(hosp_Data.getHosp_Name());
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalsListAddDoctor.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }


//    public void loadHospitalData() {
//
//        progressDialog.show();
//        progressDialog.setTitle("The Hospitals list is displaying");
//
//        eventListenerHosp = dbReferenceLoadHosp.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if (dataSnapshot.exists()){
//                    hospListAddDoc.clear();
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        hosp_Data = postSnapshot.getValue(Hospitals.class);
//                        assert hosp_Data != null;
//                        hospListAddDoc.add(hosp_Data.getHosp_Name());
//                        tVHospListAddDoc.setText("Select your Hospital");
//                        progressDialog.dismiss();
//                    }
//                }
//
//                else{
//                    tVHospListAddDoc.setText("No Hospital; Click to add Hospital");
//                    progressDialog.dismiss();
//                    tVHospListAddDoc.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            startActivity(new Intent(HospitalsListAddDoctor.this, HospitalRegistration.class));
//                            progressDialog.dismiss();
//                        }
//                    });
//                }
//
//                arrayAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(HospitalsListAddDoctor.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}

