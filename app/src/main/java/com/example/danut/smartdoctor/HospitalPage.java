package com.example.danut.smartdoctor;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HospitalPage extends AppCompatActivity {

    //Declare variables
    private TextView textViewWelcomeHospital;

    private Button buttonHospPatList, buttonHospDocList, buttonHospAddDoc;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_page);

        //initialise the variables
        textViewWelcomeHospital = (TextView) findViewById(R.id.tvWelcomeHospital);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //retrieve data from database into text views
        databaseReference = FirebaseDatabase.getInstance().getReference("Hospitals");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot dsHosp : dataSnapshot.getChildren()) {
                    FirebaseUser user_Hosp = firebaseAuth.getCurrentUser();

                    final Hospital userHosp = dsHosp.getValue(Hospital.class);

                    if (user_Hosp.getEmail().equalsIgnoreCase(userHosp.hospEmail_Address)) {
                        textViewWelcomeHospital.setText("Welcome to "+userHosp.getHosp_Name()+" Hospital");

                        buttonHospAddDoc = (Button)findViewById(R.id.btnHospAddDoc);
                        buttonHospAddDoc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HospitalPage.this, DoctorRegister.class);
                                intent.putExtra("HOSPID",userHosp.getHosp_Name()+" Hospital");
                                startActivity(intent);
                            }
                        });

                        buttonHospDocList = (Button)findViewById(R.id.btnHospDocList);
                        buttonHospDocList.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HospitalPage.this, DoctorsList.class);
                                intent.putExtra("HOSPID",userHosp.getHosp_Name()+" Hospital");
                                startActivity(intent);
                            }
                        });

                        //Action button patients list
                        buttonHospPatList = (Button)findViewById(R.id.btnHospPatList);
                        buttonHospPatList.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HospitalPage.this,PatientsListHospital.class);
                                intent.putExtra("HOSPKey",userHosp.getHosp_Name()+" Hospital");
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HospitalPage.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
