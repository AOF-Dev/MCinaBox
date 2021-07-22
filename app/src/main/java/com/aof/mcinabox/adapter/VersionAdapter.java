package com.aof.mcinabox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aof.mcinabox.R;
import com.aof.mcinabox.model.Profile;

import java.util.List;

public class VersionAdapter extends ArrayAdapter<Profile> {
    public VersionAdapter(@NonNull Context context, @NonNull List<Profile> profiles) {
        super(context, 0, profiles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.version_row, parent, false);
        }

        Profile profile = getItem(position);
        ImageView icon = convertView.findViewById(R.id.icon);
        TextView name = convertView.findViewById(R.id.name);
        TextView description = convertView.findViewById(R.id.description);

        icon.setImageResource(R.drawable.grass);
        name.setText(profile.getName());
        description.setText(profile.getDescription());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
