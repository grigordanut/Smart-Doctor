package com.example.danut.smartdoctor;

import com.google.firebase.database.Exclude;

public class Patient {

    public String patCard_Code;
    public String patUnique_Code;
    public String patFirst_Name;
    public String patLast_Name;
    public String patEmail_Address;
    public String patHosp_ID;
    public String patDoc_ID;
    public String patientKey;

    public Patient() {

    }

    public Patient(String patCard_Code, String patUnique_Code, String patFirst_Name, String patLast_Name, String patEmail_Address, String patHosp_ID, String patDoc_ID) {
        this.patCard_Code = patCard_Code;
        this.patUnique_Code = patUnique_Code;
        this.patFirst_Name = patFirst_Name;
        this.patLast_Name = patLast_Name;
        this.patEmail_Address = patEmail_Address;
        this.patHosp_ID = patHosp_ID;
        this.patDoc_ID = patDoc_ID;
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

    public String getPatHosp_ID() {
        return patHosp_ID;
    }

    public void setPatHosp_ID(String patHosp_ID) {
        this.patHosp_ID = patHosp_ID;
    }

    public String getPatDoc_ID() {
        return patDoc_ID;
    }

    public void setPatDoc_ID(String patDoc_ID) {
        this.patDoc_ID = patDoc_ID;
    }

    @Exclude
    public String getPatientKey() {
        return patientKey;
    }

    public void setPatientKey(String patientKey) {
        this.patientKey = patientKey;
    }

    public class NONE {
    }
}
