package com.aof.mcinabox.filechooser.model;

import java.io.File;

public class ChooserFile {
    private final String name;
    private final String size;
    private final File file;

    public ChooserFile(String name, String size, File file) {
        this.name = name;
        this.size = size;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public File getFile() {
        return file;
    }
}
