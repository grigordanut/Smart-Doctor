package com.example.smartdoctor;

import com.google.firebase.database.Exclude;

public class Doctor {

    public String docUnique_Code;
    public String docFirst_Name;
    public String docLast_Name;
    public String docPhone_Number;
    public String docEmail_Address;
    public String docHosp_Key;
    public String doc_Key;

    public Doctor() {

    }

    public Doctor(String docUnique_Code, String docFirst_Name, String docLast_Name, String docPhone_Number, String docEmail_Address, String docHosp_Key) {
        this.docUnique_Code = docUnique_Code;
        this.docFirst_Name = docFirst_Name;
        this.docLast_Name = docLast_Name;
        this.docPhone_Number = docPhone_Number;
        this.docEmail_Address = docEmail_Address;
        this.docHosp_Key = docHosp_Key;
    }

    public String getDocUnique_Code() {
        return docUnique_Code;
    }

    public void setDocUnique_Code(String docUnique_Code) {
        this.docUnique_Code = docUnique_Code;
    }

    public String getDocFirst_Name() {
        return docFirst_Name;
    }

    public void setDocFirst_Name(String docFirst_Name) {
        this.docFirst_Name = docFirst_Name;
    }

    public String getDocLast_Name() {
        return docLast_Name;
    }

    public void setDocLast_Name(String docLast_Name) {
        this.docLast_Name = docLast_Name;
    }

    public String getDocPhone_Number() {
        return docPhone_Number;
    }

    public void setDocPhone_Number(String docPhone_Number) {
        this.docPhone_Number = docPhone_Number;
    }

    public String getDocEmail_Address() {
        return docEmail_Address;
    }

    public void setDocEmail_Address(String docEmail_Address) {
        this.docEmail_Address = docEmail_Address;
    }

    public String getDocHosp_Key() {
        return docHosp_Key;
    }

    public void setDocHosp_Key(String docHosp_Key) {
        this.docHosp_Key = docHosp_Key;
    }

    @Exclude
    public String getDoc_Key() {
        return doc_Key;
    }

    @Exclude
    public void setDoc_Key(String doc_Key) {
        this.doc_Key = doc_Key;
    }
}
