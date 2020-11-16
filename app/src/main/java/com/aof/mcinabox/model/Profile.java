package com.aof.mcinabox.model;

import com.aof.mcinabox.network.model.Version;

public class Profile {
    private final String name;
    private final String description;
    private Version version;
    private String javaArgs;

    public Profile(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Version getVersion() {
        return version;
    }

    public String getJavaArgs() {
        return javaArgs;
    }
}
