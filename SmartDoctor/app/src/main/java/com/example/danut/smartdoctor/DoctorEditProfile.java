package com.example.danut.smartdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorEditProfile extends AppCompatActivity {

    //declare variables
    private EditText newDocUniqueCode,newDocFirstName, newDocLastName, newDocPhone, newDocEmail, newDocHospID;
    private Button buttonNewDocSave;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_edit_profile);

        newDocUniqueCode = (EditText)findViewById(R.id.etNewDocUCode);
        newDocFirstName = (EditText)findViewById(R.id.etNewDocFName);
        newDocLastName = (EditText)findViewById(R.id.etNewDocLName);
        newDocPhone = (EditText)findViewById(R.id.etNewDocFNumber);
        newDocEmail = (EditText)findViewById(R.id.etNewDocEmail);
        newDocHospID = (EditText)findViewById(R.id.etNewDocHospID);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("Doctors");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //retrieve data from firebase database
                for(DataSnapshot childDoc : dataSnapshot.getChildren()){
                    FirebaseUser userDoc = firebaseAuth.getCurrentUser();

                    Doctor docProfile = childDoc.getValue(Doctor.class);
                    progressDialog.dismiss();
                    if (userDoc.getEmail().equalsIgnoreCase(docProfile.docEmail_Address)){
                        newDocUniqueCode.setText(docProfile.docUnique_Code);
                        newDocFirstName.setText(docProfile.docFirst_Name);
                        newDocLastName.setText(docProfile.docLast_Name);
                        newDocPhone.setText(docProfile.docPhone_Number);
                        newDocEmail.setText(docProfile.docEmail_Address);
                        newDocHospID.setText(docProfile.docHosp_ID);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorEditProfile.this,databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        buttonNewDocSave = (Button)findViewById(R.id.btnNewDocSave);
        buttonNewDocSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String newDoc_UniqueCode = newDocUniqueCode.getText().toString();
                String newDoc_FirstName = newDocFirstName.getText().toString();
                String newDoc_LastName = newDocLastName.getText().toString();
                String newDoc_Phone = newDocPhone.getText().toString();
                String newDoc_Email = newDocEmail.getText().toString();
                String newDoc_HospID = newDocHospID.getText().toString();

                String docUser_Id = firebaseAuth.getCurrentUser().getUid();
                DatabaseReference docUser = databaseReference.child(docUser_Id);
                Doctor docProf = new Doctor(newDoc_UniqueCode,newDoc_FirstName,newDoc_LastName,newDoc_Phone,newDoc_Email,newDoc_HospID);
                docUser.setValue(docProf);

                //clear input text fields
                newDocUniqueCode.setText("");
                newDocFirstName.setText("");
                newDocLastName.setText("");
                newDocPhone.setText("");
                newDocEmail.setText("");
                newDocHospID.setText("");

                progressDialog.dismiss();
                firebaseAuth.signOut();
                finish();
                Toast.makeText(DoctorEditProfile.this, "The Doctor's details has been changed successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DoctorEditProfile.this,LogIn.class));
            }
        });
    }
}
