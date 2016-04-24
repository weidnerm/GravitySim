package com.example.emw010.gravitysim;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class GravityPreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getPreferenceManager().setSharedPreferencesName(
//                Gravity.PREFS_NAME);
        addPreferencesFromResource(R.xml.preferences);
    }

}
