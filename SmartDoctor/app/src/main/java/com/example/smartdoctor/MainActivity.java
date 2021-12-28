package com.example.smartdoctor;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.text.DecimalFormat;

import static java.lang.Double.parseDouble;

public class MainActivity extends AppCompatActivity {

    private EditText eTLungime, eTLatime, eTGrosime, eTPret;
    private TextView tVTotalVolum, tVTotalPret;
    private String lung_ime, gros_ime, lat_ime, pr_et;
    private Double total_Volum, total_Pret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eTLungime = findViewById(R.id.etLungime);
        eTGrosime = findViewById(R.id.etGrosime);
        eTLatime = findViewById(R.id.etLatime);
        eTPret= findViewById(R.id.etPret);

        tVTotalVolum = findViewById(R.id.tvVolum);
        tVTotalPret = findViewById(R.id.tvPret);

        eTLungime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tVTotalVolum.setText("");
                tVTotalPret.setText("");
            }
        });

        eTGrosime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tVTotalVolum.setText("");
                tVTotalPret.setText("");
            }
        });

        eTLatime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tVTotalVolum.setText("");
                tVTotalPret.setText("");
            }
        });

        eTPret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tVTotalPret.setText("");
            }
        });


        Button buttonVolumTotal = findViewById(R.id.btnVolum);
        buttonVolumTotal.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onClick(View v) {

                lung_ime = eTLungime.getText().toString();
                gros_ime = eTGrosime.getText().toString();
                lat_ime = eTLatime.getText().toString();

                if (TextUtils.isEmpty(lung_ime)){
                    alertDialogLungime();
                    eTLungime.requestFocus();
                }

                else if(TextUtils.isEmpty(gros_ime)){
                    alertDialogGrosime();
                    eTGrosime.requestFocus();
                }

                else if(TextUtils.isEmpty(lat_ime)){
                    alertDialogLatime();
                    eTLatime.requestFocus();
                }

                else {
                    double lung_ime = parseDouble(eTLungime.getText().toString());
                    double gros_ime = parseDouble(eTGrosime.getText().toString());
                    double lat_ime = parseDouble(eTLatime.getText().toString());

                    total_Volum = (lung_ime*gros_ime*lat_ime)/10000;

                    total_Volum = roundTwoDecimalsVolum(total_Volum);
                    tVTotalVolum.setText(total_Volum+" m3");
                    //eTLatime.setBackgroundResource(R.drawable.et_round_corners);
                }
            }
        });

        Button buttonPretTotal = findViewById(R.id.btnPret);
        buttonPretTotal.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onClick(View v) {

                pr_et = eTPret.getText().toString().trim();
                final String total_VolumVal = tVTotalVolum.getText().toString().trim();

                if ((TextUtils.isEmpty(total_VolumVal) && ((TextUtils.isEmpty(pr_et))))){
                    alertDialogVolumPretLipsa();
                    eTLungime.requestFocus();
                }

                else if (TextUtils.isEmpty(total_VolumVal)){
                    alertDialogVolumLipsa();
                }

                else if (TextUtils.isEmpty(String.valueOf(pr_et))){
                    alertDialogPretLipsa();
                    eTPret.requestFocus();
                }

                else {
                    double pr_et = parseDouble(eTPret.getText().toString().trim());

                    total_Pret = total_Volum *pr_et;
                    tVTotalPret.setText(String.format("%,.2f", total_Pret)+" Lei" );
                }
            }
        });

        Button buttonClearLungime = findViewById(R.id.btnClearLungime);
        buttonClearLungime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eTLungime.setText("");
                tVTotalVolum.setText("");
                tVTotalPret.setText("");
                eTLungime.requestFocus();
            }
        });

        Button buttonClearGrosime = findViewById(R.id.btnClearGrosime);
        buttonClearGrosime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eTGrosime.setText("");
                tVTotalVolum.setText("");
                tVTotalPret.setText("");
                eTGrosime.requestFocus();
            }
        });

        Button buttonClearLatime = findViewById(R.id.btnClearLatime);
        buttonClearLatime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eTLatime.setText("");
                tVTotalVolum.setText("");
                tVTotalPret.setText("");
                eTLatime.requestFocus();
            }
        });

        Button buttonClearPret = findViewById(R.id.btnClearPret);
        buttonClearPret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eTPret.setText("");
                tVTotalPret.setText("");
                eTPret.requestFocus();
            }
        });

        Button buttonClearAll = findViewById(R.id.btnClearAll);
        buttonClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eTLungime.setText("");
                eTGrosime.setText("");
                eTLatime.setText("");
                tVTotalVolum.setText("");
                eTPret.setText("");
                tVTotalPret.setText("");
                eTLungime.requestFocus();
            }
        });

        Button buttonReg = findViewById(R.id.btnRegister);
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterCustomer.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

        return super.onOptionsItemSelected(item);
    }

    public double roundTwoDecimalsVolum(double total_Volum){
        DecimalFormat twoDFormV = new DecimalFormat("#.##");
        return  parseDouble(twoDFormV.format(total_Volum));
    }

    public double roundTwoDecimalsPret(double total_pret){
        DecimalFormat twoDFormV = new DecimalFormat("#.##");
        return  parseDouble(twoDFormV.format(total_pret));
    }

    public void alertDialogLungime(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Adaugă valoare Lungime");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void alertDialogLatime(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Adaugă valoare Lăţime");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void alertDialogGrosime(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Adaugă valoare Grosime");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void alertDialogVolumPretLipsa(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Calculaţi Volumul şi Introduceţi Preţul");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void alertDialogVolumLipsa(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Calculaţi Volumul");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void alertDialogPretLipsa(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Introduceţi Preţul");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
