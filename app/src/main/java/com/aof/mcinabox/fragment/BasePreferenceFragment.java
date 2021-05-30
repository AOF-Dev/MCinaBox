package com.aof.mcinabox.fragment;

import androidx.preference.PreferenceFragmentCompat;

import com.aof.mcinabox.MCinaBox;

public abstract class BasePreferenceFragment extends PreferenceFragmentCompat {

    public BasePreferenceFragment() {
        super();
    }

    public MCinaBox getMCinaBox() {
        return (MCinaBox) getActivity().getApplication();
    }
}
