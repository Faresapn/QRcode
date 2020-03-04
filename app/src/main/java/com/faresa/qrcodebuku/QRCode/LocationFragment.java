package com.faresa.qrcodebuku.QRCode;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.faresa.qrcodebuku.R;


public class LocationFragment extends PreferenceFragment {

    private final static String beep = "beep";
    private final static String frontCamera = "frontCamera";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }

}
