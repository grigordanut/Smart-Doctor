package com.example.danut.smartdoctor;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;



public interface FingerPrintAuthCallback {


    void onNoFingerPrintHardwareFound();


    void onNoFingerPrintRegistered();


    void onBelowMarshmallow();


    void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject);


    void onAuthFailed(int errorCode, String errorMessage);
}
