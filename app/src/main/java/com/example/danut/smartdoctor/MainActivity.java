package com.example.danut.smartdoctor;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button buttonLogIn,buttonSignUp, buttonNFC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Objects.requireNonNull(getSupportActionBar()).setTitle("DOCTOR: main page");

        buttonLogIn = (Button)findViewById(R.id.btnLogInMain);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginBy.class));
            }
        });

        //Action button SignUp
        buttonSignUp = (Button) findViewById(R.id.btnRegisterMain);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign = new Intent(MainActivity.this, CheckUniqueCode.class);
                startActivity(sign);
            }
        });

        //Action button NFC TEST
        buttonNFC = (Button) findViewById(R.id.btnNfc);
        buttonNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nfc = new Intent(MainActivity.this, NFCActivity.class);
                startActivity(nfc);
            }
        });
    }

    //user log out
    private void HospitalsList(){
        //finish();
        startActivity(new Intent(MainActivity.this, HospitalsList.class));
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu_restaurant; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (item.getItemId() == R.id.contactUs) {
            HospitalsList();
        }
        return super.onOptionsItemSelected(item);
    }
}
