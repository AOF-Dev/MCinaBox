package com.aof.mcinabox.fragment;

import android.os.Bundle;

import com.aof.mcinabox.R;

public class AccountSettingsFragment extends BasePreferenceFragment {
    private static final String TAG = "AccountSettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.account_preferences, rootKey);
    }

}
