package com.example.danut.smartdoctor

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings.ACTION_NFC_SETTINGS
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.danut.smartdoctor.parser.NdefMessageParser
import com.example.danut.smartdoctor.utils.Utils
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_nfc.*

class NFCActivity : Activity() {

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    //Check if Patients table exists
    lateinit var dbRefCheckPatTable: DatabaseReference

    //Check if the patient has NFC id recorded
    lateinit var dbRefNFCId: DatabaseReference
    lateinit var eventListener: ValueEventListener

    //display the data read
    private var tVShowNFC: TextView? = null
    private var tVSaveCode: TextView? = null

    lateinit var btn_Save: Button
    lateinit var btn_Clear: Button
    lateinit var btn_Check: Button

    private var save_Code: String? = null

    private val progressDialog: ProgressDialog? = null

    @SuppressLint("UnspecifiedImmutableFlag", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        tVShowNFC = findViewById<View>(R.id.tvShowNFC) as? TextView
        tVSaveCode = findViewById<View>(R.id.tvSaveCode) as? TextView

        tVShowNFC!!.text = "Hold the card in the back of your device"
        tVSaveCode!!.text = "No patient NFC Id"

        btn_Save = findViewById(R.id.btnSave)
        btn_Clear = findViewById(R.id.btnClear)
        btn_Check = findViewById(R.id.btnCheck)

        tVSaveCode?.setOnClickListener {
            alertNoNFCId()
        }

        btn_Save.setOnClickListener {
            alertNoNFCId()
        }

        btn_Clear.setOnClickListener {
            alertNothingToClear()
        }

        btn_Check.setOnClickListener {
            save_Code = tvSaveCode.text.toString()
            if (save_Code == "No patient NFC Id" || save_Code == "Click SAVE to get the Patient Id") {
                alertNoNFCFound()
            } else {
                checkPatientDatabase()
            }
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, this.javaClass)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )
    }

    private fun alertNoNFCId() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder
            .setMessage("Hold the card in the back of your device to get a Patient NFC Id, and then press the button SAVE!")
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialog, which -> dialog.dismiss() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun alertNothingToClear() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder
            .setMessage("There is nothing to clear!!")
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialog, which -> dialog.dismiss() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    //for Button Chek
    private fun alertNoNFCFound() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder
            .setMessage("No Patient NFC id found.\nHold the card in the back of your device.\nPress the button SAVE and then press Check Id")
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialog, which -> dialog.dismiss() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()

        val nfcAdapterRefCopy = nfcAdapter
        if (nfcAdapterRefCopy != null) {
            if (!nfcAdapterRefCopy.isEnabled)
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
    @SuppressLint("SetTextI18n")
    private fun dumpTagData(tag: Tag): String {
        val sb = StringBuilder()
        val id = tag.id
        sb.append("ID (hex): ").append(Utils.toHex(id)).append('\n')
        sb.append("ID (reversed hex): ").append(Utils.toReversedHex(id)).append('\n')
        sb.append("ID (dec): ").append(Utils.toDec(id)).append('\n')
        sb.append("ID (reversed dec): ").append(Utils.toReversedDec(id)).append('\n')

        tVSaveCode!!.isClickable = false
        tVSaveCode!!.text = "Click SAVE to get the Patient Id"

        btn_Save.setOnClickListener {
            tVSaveCode!!.setText(Utils.toReversedDec(id).toString())
            tVSaveCode!!.isClickable = false
            btn_Save.isClickable = false
        }

        btn_Clear.setOnClickListener {
            save_Code = tVSaveCode!!.text.toString().trim { it <= ' ' }
            if (save_Code == "Click SAVE to get the Patient Id") {
                alertNothingToClear()
            } else {
                tVSaveCode!!.text = "Click SAVE to get the Patient Id"
                btn_Save.isClickable = true
                Toast.makeText(this@NFCActivity, "The Button SAVE is clickable", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val prefix = "android.nfc.tech."
        sb.append("Technologies: ")
        for (tech in tag.techList) {
            sb.append(tech.substring(prefix.length))
            sb.append(", ")
        }

        sb.delete(sb.length - 2, sb.length)

        for (tech in tag.techList) {
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
            || NfcAdapter.ACTION_NDEF_DISCOVERED == action
        ) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

            if (rawMsgs != null) {
                Log.i("NFC", "Size:" + rawMsgs.size)
                val ndefMessages: Array<NdefMessage> =
                    Array(rawMsgs.size, { i -> rawMsgs[i] as NdefMessage })
                displayNfcMessages(ndefMessages)
            } else {
                val empty = ByteArray(0)
                val id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
                val tag = intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag
                val payload = dumpTagData(tag).toByteArray()
                val record = NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload)
                val emptyMsg = NdefMessage(arrayOf(record))
                val emptyNdefMessages: Array<NdefMessage> = arrayOf(emptyMsg)
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

        this.tVShowNFC?.text = builder.toString()
    }

    private fun checkPatientDatabase() {

        progressDialog?.setMessage("The Patient is identified!!")
        progressDialog?.show()

        dbRefCheckPatTable = FirebaseDatabase.getInstance().reference.child("Patients")

        dbRefCheckPatTable.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    checkPatientNFCId()
                } else {
                    alertNoPatientRegisteredFound()
                }
                progressDialog?.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@NFCActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkPatientNFCId() {

        dbRefNFCId = FirebaseDatabase.getInstance().reference.child("Patients")

        eventListener = dbRefNFCId.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val pat_data = postSnapshot.getValue(Patients::class.java)
                    val save_Code = tVSaveCode!!.text.toString().trim { it <= ' ' }
                    if (pat_data != null) {
                        if (save_Code == pat_data.patient_CardCode) {
                            pat_data.patient_Key = postSnapshot.key
                            val intent = Intent(this@NFCActivity, PatientNFC::class.java)
                            intent.putExtra("FIRSTNAME", pat_data.patient_FirstName)
                            intent.putExtra("LASTNAME", pat_data.patient_LastName)
                            intent.putExtra("DOCTORNAME", pat_data.patient_DocName)
                            intent.putExtra("HOSPNAME", pat_data.patient_HospName)
                            startActivity(intent)
                        } else {
                            alertNoPatientFond()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@NFCActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun alertNoPatientRegisteredFound() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder
            .setMessage("No Patients registered in the database were found.\nPlease register Patients and then check the NFC id!!")
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialog, which -> dialog.dismiss() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun alertNoPatientFond() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder
            .setMessage("No Patient found to be registered with the NCF Id: $save_Code")
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialog, which -> dialog.dismiss() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
