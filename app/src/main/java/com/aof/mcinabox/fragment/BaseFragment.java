package com.aof.mcinabox.fragment;

import androidx.fragment.app.Fragment;

import com.aof.mcinabox.MCinaBox;

public abstract class BaseFragment extends Fragment {

    public BaseFragment() {
        super();
    }

    public BaseFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    public MCinaBox getMCinaBox() {
        return (MCinaBox) getActivity().getApplication();
    }
}
