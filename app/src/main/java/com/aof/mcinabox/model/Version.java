package com.aof.mcinabox.model;

public class Version {
    private String name;
    private String description;

    public Version(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
