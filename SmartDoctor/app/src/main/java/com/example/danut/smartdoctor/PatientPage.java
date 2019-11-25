package com.example.danut.smartdoctor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

public class PatientPage extends AppCompatActivity {


    //Declare variables
    private TextView textViewWelcomePatient;

    private Button buttonSeeMedRecord;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_page);

        //initialise the variables
        textViewWelcomePatient = (TextView) findViewById(R.id.tvWelcomePatient);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //retrieve data from database into text views
        databaseReference = FirebaseDatabase.getInstance().getReference("Patients");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot dsPat : dataSnapshot.getChildren()) {
                    FirebaseUser user_Pat = firebaseAuth.getCurrentUser();

                    final Patient pat = dsPat.getValue(Patient.class);

                    if (user_Pat.getEmail().equalsIgnoreCase(pat.patEmail_Address)){
                        textViewWelcomePatient.setText("Welcome "+pat.getPatFirst_Name()+" "+pat.getPatLast_Name());

                        buttonSeeMedRecord = (Button)findViewById(R.id.btnSeeMedRecord);
                        buttonSeeMedRecord.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent rec_Pat = new Intent(PatientPage.this, MedicalRecordPatient.class);
                                rec_Pat.putExtra("PATID", pat.getPatFirst_Name()+" "+pat.getPatLast_Name()+" "+pat.getPatUnique_Code());
                                rec_Pat.putExtra("DOCID", pat.getPatDoc_ID());
                                startActivity(rec_Pat);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PatientPage.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Patient log out
    private void logOutPatient(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(PatientPage.this, MainActivity.class));
    }

    private void changePassword(){
        finish();
        startActivity(new Intent(PatientPage.this,PatientChangePassword.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOutPatient:{
                logOutPatient();
                return true;
            }
            case R.id.changePassword:{
                changePassword();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
