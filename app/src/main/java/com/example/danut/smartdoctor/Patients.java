package com.example.danut.smartdoctor;

import com.google.firebase.database.Exclude;

public class Patients {

    private String patient_CardCode;
    private String patient_UniqueCode;
    private String patient_FirstName;
    private String patient_LastName;
    private String patient_Email;
    private String patient_HospName;
    private String patient_HospKey;
    private String patient_DocName;
    private String patient_DocKey;

    private String patient_Key;

    public Patients() {

    }

    public Patients(String patient_CardCode, String patient_UniqueCode, String patient_FirstName, String patient_LastName, String patient_Email, String patient_HospName, String patient_HospKey, String patient_DocName, String patient_DocKey) {
        this.patient_CardCode = patient_CardCode;
        this.patient_UniqueCode = patient_UniqueCode;
        this.patient_FirstName = patient_FirstName;
        this.patient_LastName = patient_LastName;
        this.patient_Email = patient_Email;
        this.patient_HospName = patient_HospName;
        this.patient_HospKey = patient_HospKey;
        this.patient_DocName = patient_DocName;
        this.patient_DocKey = patient_DocKey;
    }

    public String getPatient_CardCode() {
        return patient_CardCode;
    }

    public void setPatient_CardCode(String patient_CardCode) {
        this.patient_CardCode = patient_CardCode;
    }

    public String getPatient_UniqueCode() {
        return patient_UniqueCode;
    }

    public void setPatient_UniqueCode(String patient_UniqueCode) {
        this.patient_UniqueCode = patient_UniqueCode;
    }

    public String getPatient_FirstName() {
        return patient_FirstName;
    }

    public void setPatient_FirstName(String patient_FirstName) {
        this.patient_FirstName = patient_FirstName;
    }

    public String getPatient_LastName() {
        return patient_LastName;
    }

    public void setPatient_LastName(String patient_LastName) {
        this.patient_LastName = patient_LastName;
    }

    public String getPatient_Email() {
        return patient_Email;
    }

    public void setPatient_Email(String patient_Email) {
        this.patient_Email = patient_Email;
    }

    public String getPatient_HospName() {
        return patient_HospName;
    }

    public void setPatient_HospName(String patient_HospName) {
        this.patient_HospName = patient_HospName;
    }

    public String getPatient_HospKey() {
        return patient_HospKey;
    }

    public void setPatient_HospKey(String patient_HospKey) {
        this.patient_HospKey = patient_HospKey;
    }

    public String getPatient_DocName() {
        return patient_DocName;
    }

    public void setPatient_DocName(String patient_DocName) {
        this.patient_DocName = patient_DocName;
    }

    public String getPatient_DocKey() {
        return patient_DocKey;
    }

    public void setPatient_DocKey(String patient_DocKey) {
        this.patient_DocKey = patient_DocKey;
    }

    @Exclude
    public String getPatient_Key() {
        return patient_Key;
    }

    @Exclude
    public void setPatient_Key(String patient_Key) {
        this.patient_Key = patient_Key;
    }
}
