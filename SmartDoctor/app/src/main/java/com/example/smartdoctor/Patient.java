package com.example.smartdoctor;

import com.google.firebase.database.Exclude;

public class Patient {

    public String patCard_Code;
    public String patUnique_Code;
    public String patFirst_Name;
    public String patLast_Name;
    public String patEmail_Address;
    public String patHosp_Key;
    public String patDoc_Key;
    public String patient_Key;

    public Patient() {

    }

    public Patient(String patCard_Code, String patUnique_Code, String patFirst_Name, String patLast_Name, String patEmail_Address, String patHosp_Key, String patDoc_Key) {
        this.patCard_Code = patCard_Code;
        this.patUnique_Code = patUnique_Code;
        this.patFirst_Name = patFirst_Name;
        this.patLast_Name = patLast_Name;
        this.patEmail_Address = patEmail_Address;
        this.patHosp_Key = patHosp_Key;
        this.patDoc_Key = patDoc_Key;
    }

    public String getPatCard_Code() {
        return patCard_Code;
    }

    public void setPatCard_Code(String patCard_Code) {
        this.patCard_Code = patCard_Code;
    }

    public String getPatUnique_Code() {
        return patUnique_Code;
    }

    public void setPatUnique_Code(String patUnique_Code) {
        this.patUnique_Code = patUnique_Code;
    }

    public String getPatFirst_Name() {
        return patFirst_Name;
    }

    public void setPatFirst_Name(String patFirst_Name) {
        this.patFirst_Name = patFirst_Name;
    }

    public String getPatLast_Name() {
        return patLast_Name;
    }

    public void setPatLast_Name(String patLast_Name) {
        this.patLast_Name = patLast_Name;
    }

    public String getPatEmail_Address() {
        return patEmail_Address;
    }

    public void setPatEmail_Address(String patEmail_Address) {
        this.patEmail_Address = patEmail_Address;
    }

    public String getPatHosp_Key() {
        return patHosp_Key;
    }

    public void setPatHosp_Key(String patHosp_Key) {
        this.patHosp_Key = patHosp_Key;
    }

    public String getPatDoc_Key() {
        return patDoc_Key;
    }

    public void setPatDoc_Key(String patDoc_Key) {
        this.patDoc_Key = patDoc_Key;
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
