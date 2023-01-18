package com.example.danut.smartdoctor.parser

import android.nfc.NdefMessage
import android.nfc.NdefRecord

import com.example.danut.smartdoctor.record.ParsedNdefRecord
import com.example.danut.smartdoctor.record.SmartPoster
import com.example.danut.smartdoctor.record.TextRecord
import com.example.danut.smartdoctor.record.UriRecord

import java.util.ArrayList

object NdefMessageParser {

    fun parse(message: NdefMessage): List<ParsedNdefRecord> {
        return getRecords(message.records)
    }

    fun getRecords(records: Array<NdefRecord>): List<ParsedNdefRecord> {
        val elements = ArrayList<ParsedNdefRecord>()

        for (record in records) {
            if (UriRecord.isUri(record)) {
                elements.add(UriRecord.parse(record))
            } else if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record))
            } else if (SmartPoster.isPoster(record)) {
                elements.add(SmartPoster.parse(record))
            } else {
                elements.add(object : ParsedNdefRecord {
                    override fun str(): String {
                        return String(record.payload)
                    }
                })
            }
        }

        return elements
    }
}
