package com.example.smartdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class HospitalEditProfile extends AppCompatActivity {

    //declare variables
    private TextInputEditText hospUniqueCodeUp, hospNameUp, hospEmailUp;

    private String hosp_UniqueCodeUp, hosp_NameUp, hosp_EmailUp;

    private TextView tVHospitalEditProfile;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference hospDatabaseReferenceUp;

    ValueEventListener hospEventListenerUp;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_edit_profile);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        //initialise the variables
        tVHospitalEditProfile = findViewById(R.id.tvHospitalEditProfile);

        hospUniqueCodeUp = findViewById(R.id.etHospUniqueCodeUp);
        hospNameUp = findViewById(R.id.etHospNameUp);
        hospEmailUp = findViewById(R.id.etHospEmailUp);

        hospEmailUp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                alertEmailChangePlace();
                return true;
            }
        });

        //load the user details in the edit texts
        hospDatabaseReferenceUp = FirebaseDatabase.getInstance().getReference("Hospitals");
        hospEventListenerUp = hospDatabaseReferenceUp.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot ds_User : dataSnapshot.getChildren()) {
                    FirebaseUser user_Db = firebaseAuth.getCurrentUser();

                    Hospitals hospitals_Data = ds_User.getValue(Hospitals.class);

                    if (user_Db != null){
                        if (user_Db.getUid().equals(ds_User.getKey())){
                            hospUniqueCodeUp.setText(Objects.requireNonNull(hospitals_Data).getHosp_UniqueCode());
                            hospNameUp.setText(hospitals_Data.getHosp_Name());
                            hospEmailUp.setText(hospitals_Data.getHosp_Email());
                            tVHospitalEditProfile.setText("Edit profile of: " + hospitals_Data.getHosp_Name());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalEditProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        //save the user details in the database
        Button buttonHospSaveUp = (Button) findViewById(R.id.btnHospSaveUp);
        buttonHospSaveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBikeStoreDetails();
            }
        });
    }

    public void updateBikeStoreDetails() {

        if(validateHospitalEditData()){

            progressDialog.setMessage("Update the Hospitals Profile");
            progressDialog.show();

            hosp_UniqueCodeUp = Objects.requireNonNull(hospUniqueCodeUp.getText()).toString().trim();
            hosp_NameUp = Objects.requireNonNull(hospNameUp.getText()).toString().trim();
            hosp_EmailUp = Objects.requireNonNull(hospEmailUp.getText()).toString().trim();

            String user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
            DatabaseReference currentUser = hospDatabaseReferenceUp.child(user_id);
            Hospitals hospitals_Up = new Hospitals(hosp_UniqueCodeUp, hosp_NameUp, hosp_EmailUp);
            currentUser.setValue(hospitals_Up);

            //clear data input fields
            hospUniqueCodeUp.getText().clear();
            hospNameUp.getText().clear();
            hospEmailUp.getText().clear();

            progressDialog.dismiss();
            Toast.makeText(HospitalEditProfile.this, "Your details has been changed successfully", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(HospitalEditProfile.this, LoginBy.class));
            finish();
        }
    }

    private Boolean validateHospitalEditData() {

        boolean result = false;

        hosp_UniqueCodeUp = Objects.requireNonNull(hospUniqueCodeUp.getText()).toString().trim();
        hosp_NameUp = Objects.requireNonNull(hospNameUp.getText()).toString().trim();

        if (TextUtils.isEmpty(hosp_UniqueCodeUp)) {
            hospUniqueCodeUp.setError("Enter Hospitals Unique Code");
            hospUniqueCodeUp.requestFocus();
        } else if (TextUtils.isEmpty(hosp_NameUp)) {
            hospNameUp.setError("Enter Hospitals Name");
            hospNameUp.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    private void alertEmailChangePlace(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("The Email Address cannot be change here.\nPlease use Change Email option!")
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alert1 = alertDialogBuilder.create();
        alert1.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hospital_edit_profile, menu);
        return true;
    }

    private void hospEditProfileGoBack(){
        startActivity(new Intent(HospitalEditProfile.this, HospitalPage.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.hospitalEditProfileGoBack) {
            hospEditProfileGoBack();
        }

        return super.onOptionsItemSelected(item);
    }
}