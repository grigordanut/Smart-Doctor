package com.example.danut.smartdoctor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckUniqueCode extends AppCompatActivity {

    //Retrieve data from Hospitals database
    private DatabaseReference dbReferenceLoadHosp;
    private ValueEventListener eventListenerHosp;

    private EditText checkUniqueCode;
    private Button btn_CheckCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_unique_code);

        dbReferenceLoadHosp = FirebaseDatabase.getInstance().getReference("Hospitals");

        checkUniqueCode = findViewById(R.id.etCheckUniqueCode);
        btn_CheckCode = findViewById(R.id.btnCheckCode);

        btn_CheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String check_UniqueCode = checkUniqueCode.getText().toString();

                if (check_UniqueCode.isEmpty()) {
                    checkUniqueCode.setError("Please enter your Unique code");
                    checkUniqueCode.requestFocus();
                }

                else{
                    if(check_UniqueCode.charAt(0) =='h'||check_UniqueCode.charAt(0)=='H'){
                        Toast.makeText(CheckUniqueCode.this, "This is a correct code for Hospitals",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CheckUniqueCode.this, HospitalRegistration.class));
                    }

                    else if(check_UniqueCode.charAt(0) =='d'|| check_UniqueCode.charAt(0)=='D'){

                        eventListenerHosp = dbReferenceLoadHosp.addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){
                                    Toast.makeText(CheckUniqueCode.this, "This is a correct code for Doctors",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(CheckUniqueCode.this, HospitalsListAddDoctor.class));
                                }

                                else{
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CheckUniqueCode.this);

                                    alertDialogBuilder
                                            .setTitle("There are not Hospitals existing in database.")
                                            .setMessage("Please add Hospitals first and then add Doctors.")
                                            .setCancelable(false)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    checkUniqueCode.setText("");
                                                }
                                            });

                                    // create alert dialog
                                    AlertDialog alertDialog = alertDialogBuilder.create();

                                    // show it
                                    alertDialog.show();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(CheckUniqueCode.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    else{
                        Toast.makeText(CheckUniqueCode.this, "Please enter a correct Unique Code",Toast.LENGTH_SHORT).show();
                        checkUniqueCode.setError("Enter a correct Unique Code");
                        checkUniqueCode.requestFocus();
                    }
                }
            }
        });
    }

    public void onStart() {
        super.onStart();
        checkUniqueCode.setText("");
    }
}
