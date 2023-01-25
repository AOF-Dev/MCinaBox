package com.aof.mcinabox.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.aof.mcinabox.fragment.AccountSettingsFragment;
import com.aof.mcinabox.fragment.HomeFragment;

public class SettingsAdapter extends FragmentStateAdapter {

    public SettingsAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AccountSettingsFragment();
            case 1:
                return new HomeFragment();
            case 2:
                return new AccountSettingsFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
