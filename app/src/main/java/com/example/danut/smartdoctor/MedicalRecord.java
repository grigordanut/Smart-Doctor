package com.example.danut.smartdoctor;

import com.google.firebase.database.Exclude;

public class MedicalRecord {

    public String medRecord_Gender;
    public String medRecord_DateBirth;
    public String medRecord_PPS;
    public String medRecord_Address;
    public String recordPat_ID;
    public String recordKey;


    public MedicalRecord(){

    }

    public MedicalRecord(String medRecord_Gender, String medRecord_DateBirth, String medRecord_PPS, String medRecord_Address, String recordPat_ID) {
        this.medRecord_Gender = medRecord_Gender;
        this.medRecord_DateBirth = medRecord_DateBirth;
        this.medRecord_PPS = medRecord_PPS;
        this.medRecord_Address = medRecord_Address;
        this.recordPat_ID = recordPat_ID;
    }

    public String getMedRecord_Gender() {
        return medRecord_Gender;
    }

    public void setMedRecord_Gender(String medRecord_Gender) {
        this.medRecord_Gender = medRecord_Gender;
    }

    public String getMedRecord_DateBirth() {
        return medRecord_DateBirth;
    }

    public void setMedRecord_DateBirth(String medRecord_DateBirth) {
        this.medRecord_DateBirth = medRecord_DateBirth;
    }

    public String getMedRecord_PPS() {
        return medRecord_PPS;
    }

    public void setMedRecord_PPS(String medRecord_PPS) {
        this.medRecord_PPS = medRecord_PPS;
    }

    public String getMedRecord_Address() {
        return medRecord_Address;
    }

    public void setMedRecord_Address(String medRecord_Address) {
        this.medRecord_Address = medRecord_Address;
    }

    public String getRecordPat_ID() {
        return recordPat_ID;
    }

    public void setRecordPat_ID(String recordPat_ID) {
        this.recordPat_ID = recordPat_ID;
    }

    @Exclude
    public String getRecordKey(){
        return recordKey;
    }

    @Exclude
    public void setRecordKey(String recordKey){
        this.recordKey = recordKey;
    }

}
