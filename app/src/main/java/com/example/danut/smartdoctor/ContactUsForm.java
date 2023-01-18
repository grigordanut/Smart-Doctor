package com.example.danut.smartdoctor;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class ContactUsForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us_form);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Contact Us Form");

        //Declare variable
        final EditText usrName = findViewById(R.id.usrName);
        final EditText usrEmail = findViewById(R.id.usrEmail);
        final EditText usrObject = findViewById(R.id.usrObject);
        final EditText usrMessage = findViewById(R.id.usrMessage);

        Button post_Message = (Button) findViewById(R.id.post_message);
        post_Message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = usrName.getText().toString();
                String email = usrEmail.getText().toString();
                String subject = usrObject.getText().toString();
                String message = usrMessage.getText().toString();

                if (TextUtils.isEmpty(name)){
                    usrName.setError("Please Enter Your Name");
                    usrName.requestFocus();
                    return;
                }

                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ContactUsForm.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    usrEmail.setError("Enter a valid Email Address");
                    usrEmail.requestFocus();
                }

                else if (TextUtils.isEmpty(subject)){
                    usrObject.setError("Enter Your Subject");
                    usrObject.requestFocus();
                    return;
                }

                else if (TextUtils.isEmpty(message)){
                    usrMessage.setError("Enter Your Message");
                    usrMessage.requestFocus();
                    return;
                }
                else {
                    Intent sendEmail = new Intent(android.content.Intent.ACTION_SEND);

                    /* insert Data in email*/
                    sendEmail.setType("plain/text");
                    sendEmail.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"smart.doctor67@gmail.com"});
                    sendEmail.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
                    sendEmail.putExtra(android.content.Intent.EXTRA_TEXT,
                            "name:" + name + '\n' + "Email ID:" + email + '\n' + "Message:" + '\n' + message);

                    // Send message to the Activity
                    startActivity(Intent.createChooser(sendEmail, "Send mail..."));
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}

