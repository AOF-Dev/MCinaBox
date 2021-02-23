package com.aof.mcinabox.filechooser.model;

import java.io.File;

public class ChooserStackDirectory {
    private final File file;
    private int offset;

    public ChooserStackDirectory(File file) {
        this.file = file;
        this.offset = 0;
    }

    public File getFile() {
        return file;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
