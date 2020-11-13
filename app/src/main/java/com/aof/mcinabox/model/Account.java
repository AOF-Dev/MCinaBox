package com.aof.mcinabox.model;

import java.util.UUID;

public class Account {
    private String name;
    private String id;
    private Type accountType;

    public Account(String name) {
        this.name = name;
        this.id = UUID.nameUUIDFromBytes(name.getBytes()).toString();
        this.accountType = Type.OFFLINE;
    }

    public Account(String name, String id) {
        this.name = name;
        this.id = id;
        this.accountType = Type.ONLINE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getAccountType() {
        return accountType;
    }

    public void setAccountType(Type accountType) {
        this.accountType = accountType;
    }

    public enum Type {
        OFFLINE,
        ONLINE
    }
}
