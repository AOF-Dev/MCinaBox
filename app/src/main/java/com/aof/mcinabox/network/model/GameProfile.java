package com.aof.mcinabox.network.model;

import java.util.UUID;

public class GameProfile {
    private String agent;
    private UUID id;
    private final String name;
    private String userId;
    private long createdAt;
    private boolean legacyProfile;
    private boolean suspended;
    private boolean paid;
    private boolean migrated;
    private boolean legacy;

    public GameProfile(String name) {
        this.name = name;
    }

    public String getAgent() {
        return agent;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isLegacyProfile() {
        return legacyProfile;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public boolean isPaid() {
        return paid;
    }

    public boolean isMigrated() {
        return migrated;
    }

    public boolean isLegacy() {
        return legacy;
    }

}
