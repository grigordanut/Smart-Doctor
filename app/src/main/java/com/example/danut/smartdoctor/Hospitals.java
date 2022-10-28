package com.example.danut.smartdoctor;

import com.google.firebase.database.Exclude;

public class Hospitals {

    public String hosp_UniqueCode;
    public String hosp_Name;
    public String hosp_EmailAddress;
    public String hosp_Key;

    public Hospitals(){

    }

    public Hospitals(String hosp_UniqueCode, String hosp_Name, String hosp_EmailAddress) {
        this.hosp_UniqueCode = hosp_UniqueCode;
        this.hosp_Name = hosp_Name;
        this.hosp_EmailAddress = hosp_EmailAddress;
    }

    public String getHosp_UniqueCode() {
        return hosp_UniqueCode;
    }

    public void setHosp_UniqueCode(String hosp_UniqueCode) {
        this.hosp_UniqueCode = hosp_UniqueCode;
    }

    public String getHosp_Name() {
        return hosp_Name;
    }

    public void setHosp_Name(String hosp_Name) {
        this.hosp_Name = hosp_Name;
    }

    public String getHosp_EmailAddress() {
        return hosp_EmailAddress;
    }

    public void setHosp_EmailAddress(String hosp_EmailAddress) {
        this.hosp_EmailAddress = hosp_EmailAddress;
    }

    @Exclude
    public String getHosp_Key() {
        return hosp_Key;
    }

    @Exclude
    public void setHosp_Key(String hosp_Key) {
        this.hosp_Key = hosp_Key;
    }
}
