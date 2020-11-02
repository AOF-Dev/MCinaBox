package com.aof.mcinabox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aof.mcinabox.databinding.VersionRowBinding;
import com.aof.mcinabox.model.Version;

import java.util.List;

public class VersionAdapter extends ArrayAdapter<Version> {
    public VersionAdapter(@NonNull Context context, @NonNull List<Version> versions) {
        super(context, 0, versions);
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

        final Version version = getItem(position);
        binding.name.setText(version.getName());
        binding.description.setText(version.getDescription());

        return binding.getRoot();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
