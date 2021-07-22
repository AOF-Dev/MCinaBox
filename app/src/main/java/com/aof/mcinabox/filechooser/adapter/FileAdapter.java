package com.aof.mcinabox.filechooser.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aof.mcinabox.R;
import com.aof.mcinabox.filechooser.model.ChooserFile;

import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private static final int TYPE_FILE = 1;
    private static final int TYPE_FOLDER = 2;
    private static final int TYPE_OPEN_FOLDER = 3;

    private List<ChooserFile> files;
    private final OnClickListener listener;

    public FileAdapter(List<ChooserFile> files, OnClickListener listener) {
        this.files = files;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_row, parent, false);
        ViewHolder holder = new ViewHolder(v);

        switch (viewType) {
            case TYPE_FILE:
                holder.icon.setImageResource(R.drawable.ic_file);
                holder.size.setVisibility(View.VISIBLE);
                break;
            case TYPE_FOLDER:
                holder.icon.setImageResource(R.drawable.ic_folder);
                holder.size.setVisibility(View.GONE);
                break;
            case TYPE_OPEN_FOLDER:
                holder.icon.setImageResource(R.drawable.ic_folder_open);
                holder.size.setVisibility(View.GONE);
                break;
        }

        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        ChooserFile f = files.get(position);

        if (f.getFile().isDirectory()) {
            if ("..".equals(f.getName())) {
                return TYPE_OPEN_FOLDER;
            } else {
                return TYPE_FOLDER;
            }
        } else {
            return TYPE_FILE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChooserFile f = files.get(position);

        holder.name.setText(f.getName());
        holder.size.setText(f.getSize());
        holder.itemView.setOnClickListener(v -> listener.onClick(f));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void setFiles(List<ChooserFile> files) {
        this.files = files;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final ImageView icon;
        final TextView size;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            icon = itemView.findViewById(R.id.icon);
            size = itemView.findViewById(R.id.size);
        }
    }

    public interface OnClickListener {
        void onClick(ChooserFile file);
    }
}
