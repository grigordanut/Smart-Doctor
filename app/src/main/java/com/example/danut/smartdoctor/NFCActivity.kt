package com.example.danut.smartdoctor

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.provider.Settings.ACTION_NFC_SETTINGS
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.danut.smartdoctor.utils.Utils
import com.example.danut.smartdoctor.parser.NdefMessageParser
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.os.Parcelable
import android.util.Log;
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_nfc.*


class NFCActivity : Activity() {

    private var nfcAdapter: NfcAdapter? = null
    // launch application when a new Tag or Card will be scanned
    private var pendingIntent: PendingIntent? = null
    // display the data read
    private var text: TextView? = null

    lateinit var ref: DatabaseReference

    lateinit var btn_Save: Button

    lateinit var  btn_Check: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        btn_Check = findViewById(R.id.check)
        btn_Check.setOnClickListener {
            checkCodePatient()
        }

        text = findViewById<View>(R.id.text) as? TextView
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
            Intent(this, this.javaClass)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
    }

    override fun onResume() {
        super.onResume()

        val nfcAdapterRefCopy = nfcAdapter
        if (nfcAdapterRefCopy != null) {
            if (!nfcAdapterRefCopy.isEnabled())
                showNFCSettings()

            nfcAdapterRefCopy.enableForegroundDispatch(this, pendingIntent, null, null)
        }
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        resolveIntent(intent)
    }

    private fun showNFCSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show()
        val intent = Intent(ACTION_NFC_SETTINGS)
        startActivity(intent)
    }

    //Tag data is converted to string to display

    //return the data from this tag in String format

    private fun dumpTagData(tag: Tag): String {
        val sb = StringBuilder()
        val id = tag.getId()
        sb.append("ID (hex): ").append(Utils.toHex(id)).append('\n')
        sb.append("ID (reversed hex): ").append(Utils.toReversedHex(id)).append('\n')
        sb.append("ID (dec): ").append(Utils.toDec(id)).append('\n')
        sb.append("ID (reversed dec): ").append(Utils.toReversedDec(id)).append('\n')

        btn_Save = findViewById(R.id.save)
        btn_Save.setOnClickListener{
            val saveCode = findViewById<EditText>(R.id.etCheckTest)
            saveCode.append(Utils.toReversedDec(id).toString())
            btn_Save.isClickable=false
        }

        val prefix = "android.nfc.tech."
        sb.append("Technologies: ")
        for (tech in tag.getTechList()) {
            sb.append(tech.substring(prefix.length))
            sb.append(", ")
        }

        sb.delete(sb.length - 2, sb.length)

        for (tech in tag.getTechList()) {
            if (tech == MifareClassic::class.java.name) {
                sb.append('\n')
                var type = "Unknown"

                try {
                    val mifareTag = MifareClassic.get(tag)

                    when (mifareTag.type) {
                        MifareClassic.TYPE_CLASSIC -> type = "Classic"
                        MifareClassic.TYPE_PLUS -> type = "Plus"
                        MifareClassic.TYPE_PRO -> type = "Pro"
                    }
                    sb.append("Mifare Classic type: ")
                    sb.append(type)
                    sb.append('\n')

                    sb.append("Mifare size: ")
                    sb.append(mifareTag.size.toString() + " bytes")
                    sb.append('\n')

                    sb.append("Mifare sectors: ")
                    sb.append(mifareTag.sectorCount)
                    sb.append('\n')

                    sb.append("Mifare blocks: ")
                    sb.append(mifareTag.blockCount)
                } catch (e: Exception) {
                    sb.append("Mifare classic error: " + e.message)
                }
            }

            if (tech == MifareUltralight::class.java.name) {
                sb.append('\n')
                val mifareUlTag = MifareUltralight.get(tag)
                var type = "Unknown"
                when (mifareUlTag.type) {
                    MifareUltralight.TYPE_ULTRALIGHT -> type = "Ultralight"
                    MifareUltralight.TYPE_ULTRALIGHT_C -> type = "Ultralight C"
                }
                sb.append("Mifare Ultralight type: ")
                sb.append(type)
            }
        }

        return sb.toString()
    }

    private fun resolveIntent(intent: Intent) {
        val action = intent.action

        if (NfcAdapter.ACTION_TAG_DISCOVERED == action
                || NfcAdapter.ACTION_TECH_DISCOVERED == action
                || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

            if (rawMsgs != null) {
                Log.i("NFC", "Size:" + rawMsgs.size);
                val ndefMessages: Array<NdefMessage> = Array(rawMsgs.size, {i -> rawMsgs[i] as NdefMessage});
                displayNfcMessages(ndefMessages)
            } else {
                val empty = ByteArray(0)
                val id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
                val tag = intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag
                val payload = dumpTagData(tag).toByteArray()
                //val payload1 = dumpTagData(tag).toFloat()
                val record = NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload)
                val emptyMsg = NdefMessage(arrayOf(record))
                val emptyNdefMessages: Array<NdefMessage> = arrayOf(emptyMsg);
                displayNfcMessages(emptyNdefMessages)
            }
        }
    }

    private fun displayNfcMessages(msgs: Array<NdefMessage>?) {
        if (msgs == null || msgs.isEmpty())
            return

        val builder = StringBuilder()
        val records = NdefMessageParser.parse(msgs[0])
        val size = records.size

        for (i in 0 until size) {
            val record = records[i]
            val str = record.str()
            builder.append(str).append("\n")
        }

        text?.setText(builder.toString())
    }

    private fun checkCodePatient(){

        ref = FirebaseDatabase.getInstance().getReference("Patients")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(h in p0.children){

                        val saveCode = etCheckTest.text.toString()
                        val pat_newCode = h.getValue(Patients::class.java)
//                        if (pat_newCode != null) {
//                            if (saveCode.equals(pat_newCode.getPatCard_Code())){
//
//                                pat_newCode.setPatientKey(h.key)
//                                val intent = Intent(this@NFCActivity, PatientNFC::class.java)
//                                intent.putExtra("FIRSTNAME", pat_newCode.getPatFirst_Name())
//                                intent.putExtra("LASTNAME", pat_newCode.getPatLast_Name())
//                                intent.putExtra("DOCTORNAME", pat_newCode.getPatDoc_ID())
//                                intent.putExtra("HOSPNAME", pat_newCode.getPatHosp_ID())
//                                startActivity(intent)
//
//                            }
//                            else {
//                                Toast.makeText(this@NFCActivity, "User Not Found", Toast.LENGTH_SHORT).show()
//                            }
//
//                        }
                    }
                }
            }
        })
    }
}
