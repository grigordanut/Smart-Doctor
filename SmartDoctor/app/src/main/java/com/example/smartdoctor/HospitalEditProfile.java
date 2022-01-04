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
import android.widget.EditText;
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

        //initialise the variables
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

        tVHospitalEditProfile = findViewById(R.id.tvHospitalEditProfile);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        //load the user details in the edit texts
        hospDatabaseReferenceUp = FirebaseDatabase.getInstance().getReference("Hospitals");
        hospEventListenerUp = hospDatabaseReferenceUp.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot ds_User : dataSnapshot.getChildren()) {
                    FirebaseUser user_Db = firebaseAuth.getCurrentUser();

                    Hospital hospital_Data = ds_User.getValue(Hospital.class);

                    if (user_Db != null){
                        if (user_Db.getUid().equals(ds_User.getKey())){
                            hospUniqueCodeUp.setText(Objects.requireNonNull(hospital_Data).getHospUnique_Code());
                            hospNameUp.setText(hospital_Data.getHosp_Name());
                            hospEmailUp.setText(hospital_Data.getHospEmail_Address());
                            tVHospitalEditProfile.setText("Edit profile of: " + hospital_Data.getHosp_Name());
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

            progressDialog.setMessage("Update the Hospital Profile");
            progressDialog.show();

            hosp_UniqueCodeUp = Objects.requireNonNull(hospUniqueCodeUp.getText()).toString().trim();
            hosp_NameUp = Objects.requireNonNull(hospNameUp.getText()).toString().trim();
            hosp_EmailUp = Objects.requireNonNull(hospEmailUp.getText()).toString().trim();

            String user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
            DatabaseReference currentUser = hospDatabaseReferenceUp.child(user_id);
            Hospital hospital_Up = new Hospital(hosp_UniqueCodeUp, hosp_NameUp, hosp_EmailUp);
            currentUser.setValue(hospital_Up);

            //clear data input fields
            hospUniqueCodeUp.getText().clear();
            hospNameUp.getText().clear();
            hospEmailUp.getText().clear();

            progressDialog.dismiss();
            Toast.makeText(HospitalEditProfile.this, "Your details has been changed successfully", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(HospitalEditProfile.this, LoginBy.class));
        }
    }

    private Boolean validateHospitalEditData() {

        boolean result = false;

        hosp_UniqueCodeUp = Objects.requireNonNull(hospUniqueCodeUp.getText()).toString().trim();
        hosp_NameUp = Objects.requireNonNull(hospNameUp.getText()).toString().trim();

        if (TextUtils.isEmpty(hosp_UniqueCodeUp)) {
            hospUniqueCodeUp.setError("Enter Hospital Unique Code");
            hospUniqueCodeUp.requestFocus();
        } else if (TextUtils.isEmpty(hosp_NameUp)) {
            hospNameUp.setError("Enter Hospital Name");
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

    private void goBackEditCustom(){
        finish();
        startActivity(new Intent(HospitalEditProfile.this, HospitalPage.class));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.hospitalEditProfileGoBack) {
            goBackEditCustom();
        }

        return super.onOptionsItemSelected(item);
    }
}