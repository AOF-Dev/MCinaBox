package com.aof.mcinabox.filechooser;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aof.mcinabox.databinding.FileChooserDialogBinding;
import com.aof.mcinabox.filechooser.adapter.FileAdapter;
import com.aof.mcinabox.filechooser.model.ChooserFile;
import com.aof.mcinabox.filechooser.model.ChooserStackDirectory;
import com.aof.mcinabox.filechooser.model.MarginItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FileChooserDialog implements FileAdapter.OnClickListener {
    private static final String TAG = "FileChooserDialog";

    private final LinkedList<ChooserStackDirectory> directoryStack;
    private final String extension;
    private final OnFileChosenListener listener;
    private final FileChooserDialogBinding binding;
    private final AlertDialog alertDialog;
    private final FileAdapter fileAdapter;

    private FileChooserDialog(Context context, String title, String startPath, String extension, OnFileChosenListener listener) {
        this.directoryStack = new LinkedList<>();
        if (startPath != null) {
            this.directoryStack.add(new ChooserStackDirectory(new File(startPath)));
        } else {
            this.directoryStack.add(new ChooserStackDirectory(Environment.getExternalStorageDirectory()));
        }
        this.extension = extension;
        this.listener = listener;
        this.binding = FileChooserDialogBinding.inflate(LayoutInflater.from(context));
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setView(binding.getRoot())
                .setNegativeButton("Cancel", null);
        final String alertDialogTitle;
        if (title != null) {
            alertDialogTitle = title;
        } else {
            title = "Select a file";
            if (extension != null) {
                title += " (" + extension + ")";
            }
        }
        alertDialogBuilder.setTitle(title);
        this.alertDialog = alertDialogBuilder.create();

        this.fileAdapter = new FileAdapter(getFiles(), this);

        binding.recyclerView.setAdapter(fileAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.addItemDecoration(new MarginItemDecoration());
    }

    public void show() {
        alertDialog.show();
    }

    public void dismiss() {
        alertDialog.dismiss();
    }

    private List<ChooserFile> getFiles() {
        List<ChooserFile> directories = new ArrayList<>();
        List<ChooserFile> files = new ArrayList<>();

        for (File f : directoryStack.getLast().getFile().listFiles()) {
            if (f.isDirectory()) {
                directories.add(getChooserFile(f, false));
            } else if (extension == null || f.getName().endsWith(extension)) {
                files.add(getChooserFile(f, false));
            }
        }

        Collections.sort(directories, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        Collections.sort(files, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

        if (directoryStack.size() > 1) {
            directories.add(0, getChooserFile(directoryStack.get(directoryStack.size() - 2).getFile(), true));
        }

        directories.addAll(files);

        return directories;
    }

    private ChooserFile getChooserFile(File file, boolean isRoot) {
        if (isRoot) {
            return new ChooserFile("..", "Size: " + file.length() + "B", file);
        } else {
            return new ChooserFile(file.getName(), "Size: " + file.length() + "B", file);
        }
    }

    @Override
    public void onClick(ChooserFile file) {
        if (file.getFile().isDirectory()) {
            if ("..".equals(file.getName())) {
                directoryStack.removeLast();
            } else {
                directoryStack.getLast().setOffset(binding.recyclerView.computeVerticalScrollOffset());
                directoryStack.addLast(new ChooserStackDirectory(file.getFile()));
            }
            fileAdapter.setFiles(getFiles());
            binding.recyclerView.scrollBy(0, directoryStack.getLast().getOffset());
        } else {
            listener.onFileChosen(file.getFile());
            dismiss();
        }
    }

    public static class Builder {
        private final Context context;
        private final OnFileChosenListener listener;
        private String title;
        private String startPath;
        private String extension;

        public Builder(Context context, OnFileChosenListener listener) {
            this.context = context;
            this.listener = listener;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setStartPath(String startPath) {
            this.startPath = startPath;
            return this;
        }

        public Builder setExtension(String extension) {
            if (extension != null && !extension.startsWith("."))
                extension = "." + extension;
            this.extension = extension;
            return this;
        }

        public FileChooserDialog build() {
            return new FileChooserDialog(context, title, startPath, extension, listener);
        }
    }

    public interface OnFileChosenListener {
        void onFileChosen(File file);
    }
}
