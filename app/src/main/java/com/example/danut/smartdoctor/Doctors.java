package com.example.danut.smartdoctor;

import com.google.firebase.database.Exclude;

public class Doctors {

    private String doctor_UniqueCode;
    private String doctor_FirstName;
    private String doctor_LastName;
    private String doctor_PhoneNumber;
    private String doctor_Email;
    private String doctor_HospName;
    private String doctor_HospKey;

    public String doctor_Key;

    public Doctors() {

    }

    public Doctors(String doctor_UniqueCode, String doctor_FirstName, String doctor_LastName, String doctor_PhoneNumber, String doctor_Email, String doctor_HospName, String doctor_HospKey) {
        this.doctor_UniqueCode = doctor_UniqueCode;
        this.doctor_FirstName = doctor_FirstName;
        this.doctor_LastName = doctor_LastName;
        this.doctor_PhoneNumber = doctor_PhoneNumber;
        this.doctor_Email = doctor_Email;
        this.doctor_HospName = doctor_HospName;
        this.doctor_HospKey = doctor_HospKey;
    }

    public String getDoctor_UniqueCode() {
        return doctor_UniqueCode;
    }

    public void setDoctor_UniqueCode(String doctor_UniqueCode) {
        this.doctor_UniqueCode = doctor_UniqueCode;
    }

    public String getDoctor_FirstName() {
        return doctor_FirstName;
    }

    public void setDoctor_FirstName(String doctor_FirstName) {
        this.doctor_FirstName = doctor_FirstName;
    }

    public String getDoctor_LastName() {
        return doctor_LastName;
    }

    public void setDoctor_LastName(String doctor_LastName) {
        this.doctor_LastName = doctor_LastName;
    }

    public String getDoctor_PhoneNumber() {
        return doctor_PhoneNumber;
    }

    public void setDoctor_PhoneNumber(String doctor_PhoneNumber) {
        this.doctor_PhoneNumber = doctor_PhoneNumber;
    }

    public String getDoctor_Email() {
        return doctor_Email;
    }

    public void setDoctor_Email(String doctor_Email) {
        this.doctor_Email = doctor_Email;
    }

    public String getDoctor_HospName() {
        return doctor_HospName;
    }

    public void setDoctor_HospName(String doctor_HospName) {
        this.doctor_HospName = doctor_HospName;
    }

    public String getDoctor_HospKey() {
        return doctor_HospKey;
    }

    public void setDoctor_HospKey(String doctor_HospKey) {
        this.doctor_HospKey = doctor_HospKey;
    }

    @Exclude
    public String getDoctor_Key() {
        return doctor_Key;
    }

    @Exclude
    public void setDoctor_Key(String doctor_Key) {
        this.doctor_Key = doctor_Key;
    }
}
