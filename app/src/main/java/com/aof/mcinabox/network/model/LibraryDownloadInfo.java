package com.aof.mcinabox.network.model;

import java.util.Map;

public class LibraryDownloadInfo {
    private DownloadInfo artifact;
    private Map<String, DownloadInfo> classifiers;

    public DownloadInfo getDownloadInfo(String classifier) {
        if (classifier == null)
            return artifact;
        return classifiers.get(classifier);
    }
}
