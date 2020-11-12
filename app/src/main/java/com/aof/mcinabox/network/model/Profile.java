package com.aof.mcinabox.network.model;

public class Profile {
    private final String agent;
    private final String id;
    private final String name;
    private final long createdAt;
    private final boolean legacyProfile;
    private final boolean suspended;
    private final boolean paid;
    private final boolean migrated;
    private final boolean legacy;

    public Profile(String agent, String id, String name, long createdAt, boolean legacyProfile, boolean suspended, boolean paid, boolean migrated, boolean legacy) {
        this.agent = agent;
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.legacyProfile = legacyProfile;
        this.suspended = suspended;
        this.paid = paid;
        this.migrated = migrated;
        this.legacy = legacy;
    }

    public String getAgent() {
        return agent;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
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
