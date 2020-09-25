package com.aof.mcinabox.launcher.version.support;

import com.aof.mcinabox.R;

public class LocalVersionListBean {
    private String version_Id;
    private int version_image;

    public LocalVersionListBean() {
        this.version_Id = "";
        this.version_image = R.drawable.ic_extension_black_24dp;
    }

    public String getVersion_Id() {
        return version_Id;
    }

    public LocalVersionListBean setVersion_Id(String version_Id) {
        this.version_Id = version_Id;
        return this;
    }

    public int getVersion_image() {
        return version_image;
    }

    public LocalVersionListBean setVersion_image(int version_image) {
        this.version_image = version_image;
        return this;
    }
}
