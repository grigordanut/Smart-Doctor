package com.example.smartdoctor;

import static android.provider.Settings.ACTION_NFC_SETTINGS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Parcelable;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdoctor.parser.NdefMessageParser;
import com.example.smartdoctor.record.ParsedNdefRecord;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NFCActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    private DatabaseReference databaseReference;

    private TextView showNFC, saveCode;

    private String save_Code;

    private Button btn_Save;

    private List<Tag> mTags;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcactivity);

        mTags = new ArrayList<>();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        showNFC = findViewById(R.id.tvShowNFC);
        saveCode = findViewById(R.id.tvSaveCode);

        saveCode.setText("No patient NFC Id");

        saveCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertNoNFCId();
            }
        });


        btn_Save = findViewById(R.id.btnSave);
        Button btn_Clear = findViewById(R.id.btnClear);
        Button btn_Check = findViewById(R.id.btnCheck);

        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertSaveNFC();
            }
        });

        btn_Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCode.setText("No patient NFC Id");
                btn_Save.setClickable(true);
                Toast.makeText(NFCActivity.this, "The Button SAVE is clickable", Toast.LENGTH_SHORT).show();
            }
        });

        btn_Check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_Code = saveCode.getText().toString().trim();
                if (TextUtils.isEmpty(save_Code)){
                    alertSaveNFC();
                }

                else {
                    checkCodePatient();
                }
            }
        });

        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }

    private void alertNoNFCId(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Hold the card in the back of your device to get a Patient NFC Id, and then press the button SAVE!")
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void alertSaveNFC(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("No Patient NFC found. Hold the card in the back of your device, and then press the button SAVE!")
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled()) {
                showNFCSettings();
            }
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }

    private void showNFCSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ACTION_NFC_SETTINGS);
        startActivity(intent);
    }

    //Tag data is converted to string to display
    //return the data from this tag in String format
    private String dumpTagData(Tag tag){
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        sb.append("ID (hex): ").append(toHex(id)).append('\n');
        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n');
        sb.append("ID (dec): ").append(toDec(id)).append('\n');
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n');

        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCode.append(String.valueOf(toReversedDec(id)));
                btn_Save.setClickable(false);
            }
        });

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                String type = "Unknown";

                try {
                    MifareClassic mifareTag = MifareClassic.get(tag);

                    switch (mifareTag.getType()) {
                        case MifareClassic.TYPE_CLASSIC:
                            type = "Classic";
                        case MifareClassic.TYPE_PLUS:
                            type = "Plus";
                        case MifareClassic.TYPE_PRO:
                            type = "Pro";
                            break;
                    }
                    sb.append("Mifare Classic type: ");
                    sb.append(type);
                    sb.append('\n');

                    sb.append("Mifare size: ");
                    sb.append(mifareTag.getSize()).append(" bytes");
                    sb.append('\n');

                    sb.append("Mifare sectors: ");
                    sb.append(mifareTag.getSectorCount());
                    sb.append('\n');

                    sb.append("Mifare blocks: ");
                    sb.append(mifareTag.getBlockCount());
                } catch (Exception e) {
                    sb.append("Mifare classic error: ").append(e.getMessage());
                }
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }

    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    public static long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (byte aByte : bytes) {
            long value = aByte & 0xffL;
            result += value * factor;
            factor *= 256L;
        }
        return result;
    }

    public static long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffL;
            result += value * factor;
            factor *= 256L;
        }
        return result;
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++){
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
                msgs =  new NdefMessage[] {msg};
                mTags.add(tag);
            }
            displayNfcMessages(msgs);
        }
    }

    private void displayNfcMessages(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);

            final int size = records.size();

            for (int i=0; i <size; i++) {

                ParsedNdefRecord record = records.get(i);

                String str = record.str();


                builder.append(str).append("\n");
            }

        showNFC.setText(builder.toString());
    }

    private void checkCodePatient(){

        databaseReference = FirebaseDatabase.getInstance().getReference("Patients");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    Patients pat_data = postSnapshot.getValue(Patients.class);

                    String save_Code = saveCode.getText().toString().trim();

                    assert pat_data != null;
                    if (save_Code.equals(pat_data.getPatCard_Code())){
                        pat_data.setPatient_Key(postSnapshot.getKey());
                        Intent intent = new Intent(NFCActivity.this, PatientNFC.class);
                        intent.putExtra("FIRSTNAME", pat_data.getPatFirst_Name());
                        intent.putExtra("LASTNAME", pat_data.getPatLast_Name());
                        intent.putExtra("DOCTORNAME", pat_data.getPatDoc_Name());
                        intent.putExtra("HOSPNAME", pat_data.getPatHosp_Name());
                        startActivity(intent);
                    }

                    else{
                        Toast.makeText(NFCActivity.this, "Patient Not Found!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NFCActivity.this, "Patient Not Found!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}