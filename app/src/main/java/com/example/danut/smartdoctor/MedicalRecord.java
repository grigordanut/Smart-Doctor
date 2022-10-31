package com.example.danut.smartdoctor;

import com.google.firebase.database.Exclude;

public class MedicalRecord {

    private String medRecord_Gender;
    private String medRecord_DateBirth;
    private String medRecord_PPS;
    private String medRecord_Address;
    private String medRecord_PatName;
    private String medRecord_PatKey;
    private String medRecord_Key;

    public MedicalRecord(){

    }

    public MedicalRecord(String medRecord_Gender, String medRecord_DateBirth, String medRecord_PPS, String medRecord_Address, String medRecord_PatName, String medRecord_PatKey) {
        this.medRecord_Gender = medRecord_Gender;
        this.medRecord_DateBirth = medRecord_DateBirth;
        this.medRecord_PPS = medRecord_PPS;
        this.medRecord_Address = medRecord_Address;
        this.medRecord_PatName = medRecord_PatName;
        this.medRecord_PatKey = medRecord_PatKey;
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

    public String getMedRecord_PatName() {
        return medRecord_PatName;
    }

    public void setMedRecord_PatName(String medRecord_PatName) {
        this.medRecord_PatName = medRecord_PatName;
    }

    public String getMedRecord_PatKey() {
        return medRecord_PatKey;
    }

    public void setMedRecord_PatKey(String medRecord_PatKey) {
        this.medRecord_PatKey = medRecord_PatKey;
    }

    @Exclude
    public String getMedRecord_Key() {
        return medRecord_Key;
    }

    @Exclude
    public void setMedRecord_Key(String medRecord_Key) {
        this.medRecord_Key = medRecord_Key;
    }
}
