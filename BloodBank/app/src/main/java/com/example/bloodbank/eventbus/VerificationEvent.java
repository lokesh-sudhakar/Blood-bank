package com.example.bloodbank.eventbus;

import android.net.wifi.hotspot2.pps.Credential;

import com.google.firebase.auth.PhoneAuthCredential;

/**
 * @author Lokesh chennamchetty
 * @date 08/09/2019
 */
public class VerificationEvent {

    public PhoneAuthCredential credential;


    public VerificationEvent(PhoneAuthCredential credential) {
        this.credential = credential;
    }

}
