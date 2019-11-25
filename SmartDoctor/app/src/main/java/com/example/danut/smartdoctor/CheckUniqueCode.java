package com.example.danut.smartdoctor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CheckUniqueCode extends AppCompatActivity {

    private EditText editTextCheckCode;
    private Button buttonCheckCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_unique_code);

        editTextCheckCode = (EditText)findViewById(R.id.etCheckUniqueCode);
        buttonCheckCode = (Button)findViewById(R.id.btnCheckCode);

        buttonCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editText_CheckCode = editTextCheckCode.getText().toString();
                if (editText_CheckCode.isEmpty()) {
                    editTextCheckCode.setError("Please enter your Unique code");
                    editTextCheckCode.requestFocus();
                }

                else{
                    if(editText_CheckCode.charAt(0) =='h'||editText_CheckCode.charAt(0)=='H'){
                        Toast.makeText(CheckUniqueCode.this, "This is a correct code for Hospital",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CheckUniqueCode.this, HospitalRegister.class));
                    }

                    else if(editText_CheckCode.charAt(0) =='d'||editText_CheckCode.charAt(0)=='D'){
                        Toast.makeText(CheckUniqueCode.this, "This is a correct code for Doctor",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CheckUniqueCode.this, HospitalListAddDoctor.class));
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
