package com.aof.mcinabox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aof.mcinabox.R;
import com.aof.mcinabox.databinding.VersionRowBinding;
import com.aof.mcinabox.model.Profile;

import java.util.List;

public class VersionAdapter extends ArrayAdapter<Profile> {
    public VersionAdapter(@NonNull Context context, @NonNull List<Profile> profiles) {
        super(context, 0, profiles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final VersionRowBinding binding;

        if (convertView == null) {
            binding = VersionRowBinding.inflate(LayoutInflater.from(getContext()), parent, false);
        } else {
            binding = VersionRowBinding.bind(convertView);
        }

        final Profile profile = getItem(position);
        binding.icon.setImageResource(R.drawable.grass);
        binding.name.setText(profile.getName());
        binding.description.setText(profile.getDescription());

        return binding.getRoot();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
