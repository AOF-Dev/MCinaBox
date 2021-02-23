package com.aof.mcinabox.network.model;

public enum OperatingSystem {
    LINUX("linux", "linux", "unix"),
    WINDOWS("windows", "win"),
    OSX("osx", "mac"),
    UNKNOWN("unknown");

    private final String name;
    private final String[] aliases;

    OperatingSystem(String name, String... aliases) {
        this.name = name;
        this.aliases = (aliases == null) ? new String[0] : aliases;
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public boolean isSupported() {
        return (this != UNKNOWN);
    }
}
