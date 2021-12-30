package com.example.smartdoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class CheckUniqueCode extends AppCompatActivity {

    private TextInputEditText editTextCheckCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_unique_code);

        editTextCheckCode = findViewById(R.id.etCheckUniqueCode);

        Button buttonCheckCode = (Button)findViewById(R.id.btnCheckCode);

        buttonCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editText_CheckCode = Objects.requireNonNull(editTextCheckCode.getText()).toString();
                if (editText_CheckCode.isEmpty()) {
                    editTextCheckCode.setError("Please enter your Unique code");
                    editTextCheckCode.requestFocus();
                }

                else{
                    if(editText_CheckCode.charAt(0) =='h'||editText_CheckCode.charAt(0)=='H'){
                        Toast.makeText(CheckUniqueCode.this, "This is a correct code for Hospital",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CheckUniqueCode.this, HospitalRegistration.class));
                    }

                    else if(editText_CheckCode.charAt(0) =='d'||editText_CheckCode.charAt(0)=='D'){
                        Toast.makeText(CheckUniqueCode.this, "This is a correct code for Doctor",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CheckUniqueCode.this, HospitalImageAddDoctor.class));
                    }

                    else{
                        Toast.makeText(CheckUniqueCode.this, "Please enter a correct Unique Code",Toast.LENGTH_SHORT).show();
                        editTextCheckCode.setError("Enter a correct Unique Code");
                        editTextCheckCode.requestFocus();
                    }
                }
            }
        });
    }
}