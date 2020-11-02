package com.aof.mcinabox.model;

public class Account {
    private String name;
    private String description;

    public Account(String name, String description) {
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
