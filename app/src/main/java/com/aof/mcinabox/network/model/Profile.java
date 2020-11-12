package com.aof.mcinabox.network.model;

public class Profile {
    private String agent;
    private String id;
    private String name;
    private String userId;
    private long createdAt;
    private boolean legacyProfile;
    private boolean suspended;
    private boolean paid;
    private boolean migrated;
    private boolean legacy;

    public String getAgent() {
        return agent;
    }

    public String getId() {
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
